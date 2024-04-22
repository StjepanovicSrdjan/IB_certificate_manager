package com.certificate_manager.certificate_manager.services;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.certificate_manager.certificate_manager.entities.Certificate;
import com.certificate_manager.certificate_manager.services.interfaces.ICertificateService;
import com.certificate_manager.certificate_manager.services.interfaces.IScheduledService;

@Service
public class ScheduledServiceImpl implements IScheduledService{
	
	@Autowired
	private ICertificateService certificateService;
	
	public void invalidateCertificatesWhichExpired() {
		List<Certificate> rootCertificates = certificateService.getRootCertificates();
		for (Certificate certRoot : rootCertificates) {
			if (certRoot.getValidTo().isBefore(LocalDateTime.now())) {
				this.certificateService.invalidateCurrentAndBelow(certRoot, "Certificate with SN" + certRoot.getSerialNumber() + "has expired.");
				return;
			}
			this.checkAndInvalidateChildren(certRoot);
		}
	}
	
	private void checkAndInvalidateChildren(Certificate certRoot) {
		List<Certificate> certificatesUnderRoot = this.certificateService.getAllCertificatesWithCurrentCertificateAsIssuer(certRoot);
		for (Certificate cert : certificatesUnderRoot) {
			if (cert.getValidTo().isBefore(LocalDateTime.now()))
				this.certificateService.invalidateCurrentAndBelow(cert, "Certificate with SN" + cert.getSerialNumber() + "has expired.");
			else
				this.checkAndInvalidateChildren(cert);
		}
	}

}
