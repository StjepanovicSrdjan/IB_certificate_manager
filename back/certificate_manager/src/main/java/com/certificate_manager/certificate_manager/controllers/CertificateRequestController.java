package com.certificate_manager.certificate_manager.controllers;

import java.nio.file.AccessDeniedException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.certificate_manager.certificate_manager.dtos.CertificateRequestCreateDTO;
import com.certificate_manager.certificate_manager.dtos.CertificateRequestReturnedDTO;
import com.certificate_manager.certificate_manager.dtos.ResponseMessageDTO;
import com.certificate_manager.certificate_manager.services.interfaces.ICertificateRequestGenerator;
import com.certificate_manager.certificate_manager.services.interfaces.ICertificateRequestService;
import com.certificate_manager.certificate_manager.services.interfaces.ILoggingService;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Controller
@RequestMapping("api/certificate/request")
@CrossOrigin(origins = "https://localhost:4200")
@Validated
public class CertificateRequestController {
	
	@Autowired
	private ICertificateRequestGenerator requestGenerator;
	
	@Autowired
	private ICertificateRequestService requestService;
	
	@Autowired
	private ILoggingService loggingService;
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@GetMapping(value = "")
//	@PreAuthorize("hasAnyRole('ADMIN', 'USER')")
	public ResponseEntity<?> getAllRequests() {
		loggingService.logUserInfo("Arrived request GET /api/certificate/request", logger);
		return new ResponseEntity<List<CertificateRequestReturnedDTO>>(requestService.getAllForRequester(), HttpStatus.OK);
	}
	
	@GetMapping(value = "/byMe")
	public ResponseEntity<?> getAllRequestsByMe() {
		loggingService.logUserInfo("Arrived request GET /api/certificate/request/byMe", logger);
		return new ResponseEntity<List<CertificateRequestReturnedDTO>>(requestService.getAllRequestsByMe(), HttpStatus.OK);
	}
	
	@GetMapping(value = "/fromMe")
	public ResponseEntity<?> getAllRequestsFromMe() {
		loggingService.logUserInfo("Arrived request GET /api/certificate/request/fromMe", logger);
		return new ResponseEntity<List<CertificateRequestReturnedDTO>>(requestService.getAllRequestsFromMe(), HttpStatus.OK);
	}
	
	@PostMapping(value = "")
//	@PreAuthorize("hasAnyRole('ADMIN', 'USER')")
	public ResponseEntity<?> generateCertificateRequest(@RequestBody CertificateRequestCreateDTO dto) throws AccessDeniedException {
		loggingService.logUserInfo("Arrived request POST /api/certificate/request/", logger);
		requestGenerator.generateCertificateRequest(dto);
		return new ResponseEntity<ResponseMessageDTO>(new ResponseMessageDTO("Successfully created certificate request"), HttpStatus.OK);
	}
	
	@PutMapping(value = "/accept/{id}")
//	@PreAuthorize("hasAnyRole('ADMIN', 'USER')")
	public ResponseEntity<?> acceptCertificateRequest(@PathVariable @Min(value = 0, message = "Field id must be greater than 0.") long id) {
		loggingService.logUserInfo("Arrived request POST /api/certificate/request/accept/{id}", logger);
		this.requestService.acceptRequest(id);
		return new ResponseEntity<ResponseMessageDTO>(new ResponseMessageDTO("Request successfully accepted. Certificate generated."), HttpStatus.OK);
	}
	
	@PutMapping(value = "/deny/{id}")
//	@PreAuthorize("hasAnyRole('ADMIN', 'USER')")
	public ResponseEntity<?> denyCertificateRequest(@PathVariable @Min(value = 0, message = "Field id must be greater than 0.") long id, @RequestBody @NotNull String rejectionReason) {
		loggingService.logUserInfo("Arrived request POST /api/certificate/request/deny/{id}", logger);
		this.requestService.denyRequest(id, rejectionReason);
		return new ResponseEntity<ResponseMessageDTO>(new ResponseMessageDTO("Request successfully denied."), HttpStatus.OK);
	}

}
