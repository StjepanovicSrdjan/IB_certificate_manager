package com.certificate_manager.certificate_manager.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;

import com.certificate_manager.certificate_manager.services.interfaces.ILoggingService;
import com.certificate_manager.certificate_manager.dtos.ResponseMessageDTO;
import com.certificate_manager.certificate_manager.services.interfaces.IScheduledService;
import com.certificate_manager.certificate_manager.sms.ISmsService;

import jakarta.transaction.Transactional;

@Controller
public class ScheduledController {
	
	@Autowired  
	private IScheduledService scheduledService; 
	
	@Autowired 
	ILoggingService loggingService;
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
//	@Transactional
//	@Scheduled(initialDelayString = "${initialdelay}", fixedDelayString = "${fixeddelay}")
//	public void test() {
//		loggingService.logServerInfo("Scheduled method for withdrawing expired certificates activated.", logger);
//		this.scheduledService.invalidateCertificatesWhichExpired();
//	} 

} 
