package com.certificate_manager.certificate_manager.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;

import com.certificate_manager.certificate_manager.services.interfaces.IScheduledService;

import jakarta.transaction.Transactional;

@Controller
public class ScheduledController {
	
	@Autowired 
	private IScheduledService scheduledService; 

	@Transactional
	@Scheduled(initialDelayString = "${initialdelay}", fixedDelayString = "${fixeddelay}")
	public void test() {
		this.scheduledService.invalidateCertificatesWhichExpired();
	} 
} 
