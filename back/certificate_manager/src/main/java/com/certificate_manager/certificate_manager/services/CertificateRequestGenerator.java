package com.certificate_manager.certificate_manager.services;

import java.nio.file.AccessDeniedException;
import java.time.DateTimeException;
import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.certificate_manager.certificate_manager.dtos.CertificateRequestCreateDTO;
import com.certificate_manager.certificate_manager.entities.Certificate;
import com.certificate_manager.certificate_manager.entities.CertificateRequest;
import com.certificate_manager.certificate_manager.entities.User;
import com.certificate_manager.certificate_manager.enums.CertificateType;
import com.certificate_manager.certificate_manager.enums.UserRole;
import com.certificate_manager.certificate_manager.exceptions.CertificateNotFoundException;
import com.certificate_manager.certificate_manager.exceptions.CertificateNotValidException;
import com.certificate_manager.certificate_manager.repositories.CertificateRepository;
import com.certificate_manager.certificate_manager.repositories.CertificateRequestRepository;
import com.certificate_manager.certificate_manager.services.interfaces.ICertificateGenerator;
import com.certificate_manager.certificate_manager.services.interfaces.ICertificateRequestGenerator;
import com.certificate_manager.certificate_manager.services.interfaces.ILoggingService;
import com.certificate_manager.certificate_manager.services.interfaces.IUserService;

@Service
public class CertificateRequestGenerator implements ICertificateRequestGenerator {
	
	@Autowired
	private CertificateRequestRepository allRequests;
	
	@Autowired
	private CertificateRepository allCertificates;
	
	@Autowired 
	private ICertificateGenerator certificateGenerator;
	
	@Autowired
	private IUserService userService;
	
	@Autowired
	private ILoggingService loggingService;
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Override 
	public void generateCertificateRequest(CertificateRequestCreateDTO dto) throws AccessDeniedException {
		User user = userService.getCurrentUser();
		if (user.getRole() == UserRole.ADMIN && dto.getType() == CertificateType.ROOT) {
			certificateGenerator.generateSelfSignedCertificate();
		}
		else {
			createRequestByDto(dto, user);
		}
		
	}
	
	private void createRequestByDto(CertificateRequestCreateDTO dto, User user) throws AccessDeniedException {
		Certificate cert = allCertificates.findBySerialNumber(dto.getIssuerSerialNumber()).get();
		if (dto.getValidTo().isBefore(LocalDateTime.now())) {
			loggingService.logServerError("Incorrect date! IssuerSerialNum="+dto.getIssuerSerialNumber(), logger);
			throw new DateTimeException("Incorrect date!");
		}
		else if (cert == null) {
			loggingService.logServerError("Incorrect issuer serial number! IssuerSerialNum="+dto.getIssuerSerialNumber(), logger);
			throw new CertificateNotFoundException();
		} 
		else if (!cert.isValid() || cert.getType() == CertificateType.END || cert.getValidTo().isBefore(dto.getValidTo())) {
			loggingService.logServerError("Invalid issuer certificate! IssuerSerialNum="+dto.getIssuerSerialNumber(), logger);
			throw new CertificateNotValidException();
		}
		else if (user.getRole() != UserRole.ADMIN && dto.getType() == CertificateType.ROOT) {
			loggingService.logServerError("User tried to create root! IssuerSerialNum="+dto.getIssuerSerialNumber(), logger);
			throw new AccessDeniedException(null);
		}
		
		CertificateRequest request = new CertificateRequest(dto, userService.getCurrentUser());
		this.allRequests.save(request);
		this.allRequests.flush();
		loggingService.logServerInfo("Created new certificate request! RequestID="+request.getId(), logger);

		if (user.getRole() == UserRole.ADMIN || user == cert.getIssuedTo()) {
			certificateGenerator.generateCertificate(request);
		}
	}
	
}
