package com.certificate_manager.certificate_manager.controllers;

import org.springframework.http.HttpHeaders;

import java.io.IOException;
import java.nio.file.Files;
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

import com.certificate_manager.certificate_manager.dtos.CertificateDTO;
import com.certificate_manager.certificate_manager.dtos.DownloadCertDTO;
import com.certificate_manager.certificate_manager.dtos.ResponseMessageDTO;
import com.certificate_manager.certificate_manager.dtos.WithdrawalReasonDTO;
import com.certificate_manager.certificate_manager.exceptions.CertificateNotFoundException;
import com.certificate_manager.certificate_manager.exceptions.UserNotFoundException;
import com.certificate_manager.certificate_manager.services.interfaces.ICertificateGenerator;
import com.certificate_manager.certificate_manager.services.interfaces.ICertificateService;
import com.certificate_manager.certificate_manager.services.interfaces.ILoggingService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

@Controller
@RequestMapping("api/certificate")
@CrossOrigin(origins = "https://localhost:4200")
@Validated
public class CertificateController {
	
	@Autowired
	private ICertificateService certificateService;
	
	@Autowired
	private ICertificateGenerator certificateGenerator;
	
	@Autowired
	private ILoggingService loggingService;
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@GetMapping(value = "")
//	@PreAuthorize("hasAnyRole('ADMIN', 'USER')")
	public ResponseEntity<?> getAll() {
		loggingService.logUserInfo("Arrived request GET /api/certificate", logger);
		return new ResponseEntity<List<CertificateDTO>>(certificateService.getAll(), HttpStatus.OK);
	}
	
	@GetMapping(value = "/mine")
//	@PreAuthorize("hasAnyRole('ADMIN', 'USER')")
	public ResponseEntity<?> getAllForUser() {
		loggingService.logUserInfo("Arrived request GET /api/certificate/mine", logger);
		try {
			return new ResponseEntity<List<CertificateDTO>>(certificateService.getAllForUser(), HttpStatus.OK);
		} catch(UserNotFoundException e) {
			loggingService.logServerInfo(e.getMessage(), logger);
			return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
		}
		
	}
	
	@PreAuthorize("hasRole('ADMIN')")
	@PostMapping(value = "/root")
	public ResponseEntity<?> generateRoot() {
		loggingService.logUserInfo("Arrived request POST /api/certificate/root", logger);
		certificateGenerator.generateSelfSignedCertificate();
		return new ResponseEntity<ResponseMessageDTO>(new ResponseMessageDTO("Sucessefully created root certificate."), HttpStatus.OK);
	}
	
	@GetMapping(value = "/validate/{serialNumber}")
//	@PreAuthorize("hasAnyRole('ADMIN', 'USER')")
	public ResponseEntity<?> validateBySerialNumber(@PathVariable @NotEmpty String serialNumber){
		loggingService.logUserInfo("Arrived request GET /api/certificate/validate/{serialNumber}=" + serialNumber, logger);
		String validationMessage = "This certificate is valid!";
		if (!certificateService.validateBySerialNumber(serialNumber)) {
			validationMessage = "This certificate is not valid!";
		};
		loggingService.logServerInfo(validationMessage, logger);
		return new ResponseEntity<ResponseMessageDTO>(new ResponseMessageDTO(validationMessage), HttpStatus.OK);
	}
	
	@PostMapping(value = "/validate-upload")
//	@PreAuthorize("hasAnyRole('ADMIN', 'USER')")
	public ResponseEntity<?> validateByUpload(@Valid @RequestBody @NotEmpty String encodedFile){
		loggingService.logUserInfo("Arrived request POST /api/certificate/validate-upload", logger);
		String validationMessage = "This certificate is valid!";
		System.out.println("neca");
		if (!certificateService.validateByUpload(encodedFile)) {
			validationMessage = "This certificate is not valid!";
		};
		loggingService.logServerInfo(validationMessage, logger);
		return new ResponseEntity<ResponseMessageDTO>(new ResponseMessageDTO(validationMessage), HttpStatus.OK);
	}
	
	@PutMapping(value = "/withdraw/{serialNumber}")
//	@PreAuthorize("hasAnyRole('ADMIN','USER')")
	public ResponseEntity<?> withdraw(@PathVariable @NotEmpty String serialNumber, @Valid @RequestBody WithdrawalReasonDTO withdrawReasonDTO){
		loggingService.logUserInfo("Arrived request PUT /api/certificate/withdraw/{serialNumber}=" + serialNumber + " , reason=" + withdrawReasonDTO.getReason(), logger);
		this.certificateService.withdraw(serialNumber, withdrawReasonDTO);
		loggingService.logServerInfo("Successfully withdraw of certificate " + serialNumber, logger);
		return new ResponseEntity<ResponseMessageDTO>(new ResponseMessageDTO("Successfully withdraw of certificate"), HttpStatus.OK);
	}
	
	@GetMapping(value="/download/{serialNumber}")
	public ResponseEntity<?> download(@PathVariable @NotEmpty String serialNumber) {
		loggingService.logUserInfo("Arrived request GET /api/certificate/download/{serialNumber}=" + serialNumber, logger);
		DownloadCertDTO ret = this.certificateService.download(serialNumber);
		try {
			loggingService.logUserInfo("Sertificate successfully downloaded " + serialNumber, logger);
			return ResponseEntity.ok()
			          .header(HttpHeaders.CONTENT_TYPE, Files.probeContentType(ret.getPath()))
			          .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + serialNumber + ".crt" + "\"")
			          .body(ret.getFile());
		} catch (IOException e) {
			loggingService.logServerError("Certificate not found " + serialNumber, logger);
			throw new CertificateNotFoundException();
		}
	}
	
	@GetMapping(value="/download-key/{serialNumber}")
	public ResponseEntity<?> downloadKey(@PathVariable @NotEmpty String serialNumber) {
		loggingService.logUserInfo("Arrived request GET /api/certificate/download-key/{serialNumber}=" + serialNumber, logger);
		DownloadCertDTO ret = this.certificateService.downloadKey(serialNumber);
		try {
			loggingService.logUserInfo("Sertificate key successfully downloaded " + serialNumber, logger);
			return ResponseEntity.ok()
			          .header(HttpHeaders.CONTENT_TYPE, Files.probeContentType(ret.getPath()))
			          .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + serialNumber + ".key" + "\"")
			          .body(ret.getFile());
		} catch (IOException e) {
			loggingService.logServerError("Certificate not found " + serialNumber, logger);
			throw new CertificateNotFoundException();
		}
	}
}
