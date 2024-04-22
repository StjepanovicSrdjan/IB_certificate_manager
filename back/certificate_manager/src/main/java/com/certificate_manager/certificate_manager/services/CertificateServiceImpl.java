package com.certificate_manager.certificate_manager.services;

import java.io.ByteArrayInputStream;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;

import com.certificate_manager.certificate_manager.dtos.CertificateDTO;
import com.certificate_manager.certificate_manager.dtos.DownloadCertDTO;
import com.certificate_manager.certificate_manager.dtos.WithdrawalReasonDTO;
import com.certificate_manager.certificate_manager.entities.Certificate;
import com.certificate_manager.certificate_manager.entities.User;
import com.certificate_manager.certificate_manager.enums.CertificateType;
import com.certificate_manager.certificate_manager.enums.UserRole;
import com.certificate_manager.certificate_manager.exceptions.CertificateNotFoundException;
import com.certificate_manager.certificate_manager.exceptions.InvalidFileExtensionException;
import com.certificate_manager.certificate_manager.exceptions.NoAuthorizationForKeyException;
import com.certificate_manager.certificate_manager.exceptions.NotTheIssuerException;
import com.certificate_manager.certificate_manager.exceptions.RootCertificateNotForWithdrawalException;
import com.certificate_manager.certificate_manager.exceptions.UserNotFoundException;
import com.certificate_manager.certificate_manager.repositories.CertificateFileRepository;
import com.certificate_manager.certificate_manager.repositories.CertificateRepository;
import com.certificate_manager.certificate_manager.services.interfaces.ICertificateService;
import com.certificate_manager.certificate_manager.services.interfaces.ILoggingService;
import com.certificate_manager.certificate_manager.services.interfaces.IUserService;

@Service
public class CertificateServiceImpl implements ICertificateService {
	 
	@Autowired
	private CertificateRepository allCertificates;
	
	@Autowired
	private CertificateFileRepository allFileCertificates;
	
	@Autowired
	private IUserService userService;
	
	@Autowired
	private ILoggingService loggingService;
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Override
	public List<CertificateDTO> getAll() {
		List<Certificate> certs = allCertificates.findAll();
//		if (certs.size() == 0) {
//			throw new CertificateNotFoundException();
//		}
		List<CertificateDTO> ret = new ArrayList<CertificateDTO>();
		for (Certificate cert : certs) {
			ret.add(new CertificateDTO(cert));
		}
		loggingService.logServerInfo("Successfully got all certificates.", logger);
		return ret;
	}

	@Override
	public boolean validateBySerialNumber(String serialNumber){
		Certificate certificate = allCertificates.findBySerialNumber(serialNumber).orElseThrow(
				() -> new CertificateNotFoundException());
		try {
			if (!certificate.isValid()) {
				loggingService.logServerError("Certificate is not valid, serial number=" + serialNumber, logger);
				return false;
			}
			if (this.hasCertificateExpired(certificate)) {
				loggingService.logServerError("Certificate expired, serial number=" + serialNumber, logger);
				this.invalidateCurrentAndBelow(certificate, "Certificate with SN." + certificate.getSerialNumber() + "has expired.");
				return false;
			}
		
			this.verify(certificate);
			
			return true;
		} catch (Exception e) {
			loggingService.logServerError("Certificate failed X509 verification, serial number=" + serialNumber, logger);
			this.invalidateCurrentAndBelow(certificate, e.getMessage());
			return false;
		} 
	}
	
	@Override
	public boolean validateByUpload(String encodedFile){
		try {
			boolean isCrtFile = false;
			try {
				String[] parts = encodedFile.split(",");
				if (parts.length > 0) {
				    String mimeType = parts[0];
				    if (mimeType.contains("application/x-x509-ca-cert")) {
				        String decodedData = parts[1];
				        byte[] decodedBytes = Base64.getDecoder().decode(decodedData);
				        
				        String decodedString = new String(decodedBytes);
				        if (decodedString.startsWith("-----BEGIN CERTIFICATE-----")) {
				            isCrtFile = true;
				        }
				    }
				}
			} catch (Exception ex){
				throw new InvalidFileExtensionException();
			}
			if (isCrtFile) {
				byte encodedCert[] = Base64.getDecoder().decode(encodedFile.split(",")[1]);
				ByteArrayInputStream inputStream  =  new ByteArrayInputStream(encodedCert);
				CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
				X509Certificate cert = (X509Certificate)certFactory.generateCertificate(inputStream);
				return this.validateBySerialNumber(cert.getSerialNumber().toString());
			} else {
			    throw new InvalidFileExtensionException();
			}
			
		} catch (CertificateException e) {
			return false;
		}
	}
	
	private boolean hasCertificateExpired(Certificate certificate) {
		return !certificate.getValidTo().isAfter(LocalDateTime.now());
	}
	
	private void verify(Certificate certificate) throws Exception {
		X509Certificate currentCert509 = allFileCertificates.readX509Certificate(certificate.getSerialNumber());
		X509Certificate issuerCert509 = allFileCertificates.readX509Certificate(certificate.getIssuer().getSerialNumber());
		currentCert509.verify(issuerCert509.getPublicKey());
	}
    
	public CertificateDTO getBySerialNumber(String serialNumber) {
		Certificate cert = allCertificates.findBySerialNumber(serialNumber).orElse(null);
		
		if (cert == null) {
			throw new CertificateNotFoundException();
		}
		
		return new CertificateDTO(cert);
	}

	@Override
	public void withdraw(String serialNumber, WithdrawalReasonDTO withdrawReasonDTO) {
		User user = this.userService.getCurrentUser();
		Certificate cert = allCertificates.findBySerialNumber(serialNumber).orElseThrow(() -> new CertificateNotFoundException());
		if (cert.getType() == CertificateType.ROOT && user.getRole() == UserRole.USER) {
			loggingService.logServerError("Certificate can't be withdrawn, USER " + user.getEmail() +  " role can't withdraw ROOT certificate, serialNumber=" + serialNumber, logger);
			throw new RootCertificateNotForWithdrawalException();
		}
		if (user != cert.getIssuedTo() && user.getRole() == UserRole.USER)
		{
			loggingService.logServerError("Certificate can't be withdrawn, user " + user.getEmail() +  " is not the issuer, serialNumber=" + serialNumber, logger);
			throw new NotTheIssuerException();
		}

		this.invalidateCurrentAndBelow(cert, withdrawReasonDTO.getReason());
	}
	
	@Override
	public void invalidateCurrentAndBelow(Certificate cert, String reason) {
		cert.setValid(false);
		cert.setWithdrawalReason(reason);
		loggingService.logServerInfo("Successfully withdrew certificate, serial number=" + cert.getSerialNumber(), logger);
		allCertificates.save(cert);
		allCertificates.flush();
		
		if (cert.getType() == CertificateType.END)
			return; 
		
		List<Certificate> allCertificatesWithCurrentCertificateAsIssuer = allCertificates.getAllCertificatesWithCurrentCertificateAsIssuer(cert.getId());
		System.out.println(allCertificatesWithCurrentCertificateAsIssuer);
		for (Certificate c: allCertificatesWithCurrentCertificateAsIssuer) {
			System.err.println(c.getId());
			System.err.println(cert.getId());
			if (c.getId() != cert.getId())
				this.invalidateCurrentAndBelow(c, reason);
		}
		System.out.println("ee");
	}
		
	public List<CertificateDTO> getAllForUser() {
		User user = this.userService.getCurrentUser();
		
		if (user == null) {
			throw new UserNotFoundException();
		}
		
		List<Certificate> certs = allCertificates.findAllForUser(user.getId());

		List<CertificateDTO> ret = new ArrayList<CertificateDTO>();
		for (Certificate cert : certs) {
			ret.add(new CertificateDTO(cert));
		}
		return ret;
	}
	
	@Override
	public List<Certificate> getAllCertificatesWithCurrentCertificateAsIssuer(Certificate certificate){
		return allCertificates.getAllCertificatesWithCurrentCertificateAsIssuer(certificate.getId());
	}
	
	@Override
	public List<Certificate> getRootCertificates(){
		return allCertificates.getRootCertificates();
	}

	@Override
	public DownloadCertDTO download(String serialNumber) {
		Certificate wantedCertificate = this.allCertificates.findBySerialNumber(serialNumber).orElse(null);
		if (wantedCertificate == null) {
			throw new CertificateNotFoundException();
		}
		return this.allFileCertificates.readCertificateAsResource(serialNumber);
	}

	@Override
	public DownloadCertDTO downloadKey(String serialNumber) {
		User loggedUser = this.userService.getCurrentUser();
		Certificate wantedCertificate = this.allCertificates.findBySerialNumber(serialNumber).orElse(null);
		
		if (wantedCertificate == null) {
			throw new CertificateNotFoundException();
		}
		
		if (loggedUser.getRole() != UserRole.ADMIN && wantedCertificate.getIssuedTo().getId() != loggedUser.getId()) {
			loggingService.logServerError("Certificate key can't be downloaded, user " + loggedUser.getEmail() +  " is not the issuer nor the ADMIN, serialNumber=" + serialNumber, logger);
			throw new NoAuthorizationForKeyException();
		}
		
		return this.allFileCertificates.readKeyAsResource(serialNumber);
	}

}
