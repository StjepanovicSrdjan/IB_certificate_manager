package com.certificate_manager.certificate_manager.services;

import java.io.IOException;
import java.math.BigInteger;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.SecureRandom;
import java.security.Security;
import java.security.cert.X509Certificate;
import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.util.UUID;

import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.X500NameBuilder;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.certificate_manager.certificate_manager.entities.Certificate;
import com.certificate_manager.certificate_manager.entities.CertificateRequest;
import com.certificate_manager.certificate_manager.entities.User;
import com.certificate_manager.certificate_manager.enums.CertificateType;
import com.certificate_manager.certificate_manager.enums.RequestStatus;
import com.certificate_manager.certificate_manager.enums.UserRole;
import com.certificate_manager.certificate_manager.exceptions.CertificateNotFoundException;
import com.certificate_manager.certificate_manager.exceptions.NoAuthorizationException;
import com.certificate_manager.certificate_manager.repositories.CertificateFileRepository;
import com.certificate_manager.certificate_manager.repositories.CertificateRepository;
import com.certificate_manager.certificate_manager.repositories.CertificateRequestRepository;
import com.certificate_manager.certificate_manager.repositories.UserRepository;
import com.certificate_manager.certificate_manager.services.interfaces.ICertificateGenerator;
import com.certificate_manager.certificate_manager.services.interfaces.IUserService;
import com.certificate_manager.certificate_manager.utils.DateUtils;

@Service
public class CertificateGenerator implements ICertificateGenerator{

	@Autowired
	private CertificateFileRepository fileRepository;

	@Autowired
	private CertificateRepository allCertificates;
	
	@Autowired
	private CertificateRequestRepository allRequests;
	
	@Autowired
	private UserRepository allUsers;
	
	@Autowired
	private IUserService userService;

	public CertificateGenerator() {

	}

	public X509Certificate generateCertificate(CertificateRequest request) {
		
		try {
			Security.addProvider(new BouncyCastleProvider());  
			JcaContentSignerBuilder builder = new JcaContentSignerBuilder("SHA256WithRSAEncryption");
			builder = builder.setProvider("BC");

			PrivateKey issuerPrivateKey = fileRepository.readPrivateKey(request.getIssuerSerialNumber());

			ContentSigner contentSigner = builder.build(issuerPrivateKey);

			// TODO: OVDE TREBA NEKI PAMETNIJI NACIN KAO STO JE REKAO - NEKI API ZA TACNO
			// VREME, ILI JE OVO OK?
			LocalDateTime validFrom = LocalDateTime.now().toLocalDate().atStartOfDay();
			if (request.getValidTo().isBefore(validFrom)) {
				throw new DateTimeException(null);
			}

			KeyPair keys = generateKeyPair();

			String serialNumber = UUID.randomUUID().toString().replace("-", "");

			X509v3CertificateBuilder certGen = new JcaX509v3CertificateBuilder(getIssuerData(request),
					new BigInteger(serialNumber, 16), DateUtils.toDate(validFrom), DateUtils.toDate(request.getValidTo()),
					getSubjectData(request), keys.getPublic());

			X509CertificateHolder certHolder = certGen.build(contentSigner);

			JcaX509CertificateConverter certConverter = new JcaX509CertificateConverter();
			certConverter = certConverter.setProvider("BC");

			X509Certificate cert509 = certConverter.getCertificate(certHolder);
			System.out.println(cert509);
			
			Certificate certDB = saveCertificate(request, cert509, keys);
			System.out.println(certDB);
			
			request.setStatus(RequestStatus.ACCEPTED);
			allRequests.save(request);
			allRequests.flush();

			return cert509;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@Override
	public void generateSelfSignedCertificate() {
		try {
			Security.addProvider(new BouncyCastleProvider());  
			JcaContentSignerBuilder builder = new JcaContentSignerBuilder("SHA256WithRSAEncryption");
			builder = builder.setProvider("BC");
			
			KeyPair keys = generateKeyPair();
			ContentSigner contentSigner = builder.build(keys.getPrivate());

			LocalDateTime validFrom = LocalDateTime.now().toLocalDate().atStartOfDay();
			LocalDateTime validTo = validFrom.plusYears(1);

			String serialNumber = UUID.randomUUID().toString().replace("-", "");

//			User user = allUsers.findAdmin().orElseThrow(() -> new UserNotFoundException());

			User user = userService.getCurrentUser();
			
			if (user.getRole() != UserRole.ADMIN) {
				throw new NoAuthorizationException();
			}
			
			X509v3CertificateBuilder certGen = new JcaX509v3CertificateBuilder(buildX500Name(user),
					new BigInteger(serialNumber, 16), DateUtils.toDate(validFrom), DateUtils.toDate(validTo),
					buildX500Name(user), keys.getPublic());

			X509CertificateHolder certHolder = certGen.build(contentSigner);

			JcaX509CertificateConverter certConverter = new JcaX509CertificateConverter();
			certConverter = certConverter.setProvider("BC");

			X509Certificate cert509 = certConverter.getCertificate(certHolder);
			System.out.println(cert509);
			
			Certificate parent = allCertificates.findBySerialNumber(cert509.getSerialNumber().toString()).orElse(null);
			
			Certificate certDB = new Certificate(cert509.getSerialNumber().toString(), validFrom, validTo, true, CertificateType.ROOT, parent, user);
			
			fileRepository.saveCertificateAsPEMFile(cert509);
			fileRepository.savePrivateKeyAsPEMFile(keys.getPrivate(), cert509.getSerialNumber().toString());
			allCertificates.save(certDB);
			allCertificates.flush();
			
			if (parent == null) {
				parent = allCertificates.findBySerialNumber(cert509.getSerialNumber().toString()).orElse(null);
				certDB.setIssuer(parent);
				allCertificates.save(certDB);
				allCertificates.flush();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// TODO: izmestiti da bude vise clean
	private Certificate saveCertificate(CertificateRequest request, X509Certificate cert509, KeyPair keys) {
		try {
			// save DB instance
			Certificate parent = allCertificates.findBySerialNumber(request.getIssuerSerialNumber()).orElse(null);
			
			if (parent == null) {
				throw new CertificateNotFoundException();
			}
			
			Certificate certDB = new Certificate(request, cert509, parent);
	
			// save Disk .crt instance
			fileRepository.saveCertificateAsPEMFile(cert509);
	
			// save Disk .key instance
			fileRepository.savePrivateKeyAsPEMFile(keys.getPrivate(), cert509.getSerialNumber().toString());
			
			allCertificates.save(certDB);
			allCertificates.flush();
			
			return certDB;
		} catch (IOException e) {
			// TODO: bolju gresku baci
			e.printStackTrace();
		}
		
		return null;
	}

	private KeyPair generateKeyPair() {
		try {
			KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
			SecureRandom random = SecureRandom.getInstance("SHA1PRNG", "SUN");
			keyGen.initialize(2048, random);
			return keyGen.generateKeyPair();
		} catch (NoSuchAlgorithmException | NoSuchProviderException e) {
			e.printStackTrace();
		}
		return null;
	}

	private X500Name getIssuerData(CertificateRequest request) {
		// TODO: mozda izmestiti u neki certificateservice 125-130. liniju
		Certificate issuerCertificate = allCertificates.findBySerialNumber(request.getIssuerSerialNumber())
				.orElse(null);
		
		if (issuerCertificate == null) {
			throw new CertificateNotFoundException();
		}
		
		User issuer = issuerCertificate.getIssuedTo();

		return buildX500Name(issuer);
	}

	private X500Name getSubjectData(CertificateRequest request) {
		User requester = request.getRequester();

		return (buildX500Name(requester));
	}

	private X500Name buildX500Name(User user) {
		X500NameBuilder builder = new X500NameBuilder(BCStyle.INSTANCE);
		builder.addRDN(BCStyle.CN, user.getName() + user.getLastname());
		builder.addRDN(BCStyle.SURNAME, user.getLastname());
		builder.addRDN(BCStyle.GIVENNAME, user.getName());
		builder.addRDN(BCStyle.E, user.getEmail());

		// UID (USER ID) je ID korisnika, proveriti da l za issuera isto treba njegov id
		// ili id njegovog sertifikata
		builder.addRDN(BCStyle.UID, Long.toString(user.getId()));

		return builder.build();
	}

}
