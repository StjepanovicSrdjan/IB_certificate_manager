package com.certificate_manager.certificate_manager.dtos;

import java.time.LocalDateTime;

import com.certificate_manager.certificate_manager.entities.Certificate;
import com.certificate_manager.certificate_manager.enums.CertificateType;

public class CertificateDTO {
	
	private int id;

	private String serialNumber;

	private LocalDateTime validFrom;

	private LocalDateTime validTo;

	private boolean valid;

	private CertificateType type;
	
	private UserRetDTO issuer;

	private UserRetDTO issuedTo;
	
	public CertificateDTO() {}

	
	
	public CertificateDTO(int id, String serialNumber, LocalDateTime validFrom, LocalDateTime validTo, boolean valid,
			CertificateType type, UserRetDTO issuer, UserRetDTO issuedTo) {
		super();
		this.id = id;
		this.serialNumber = serialNumber;
		this.validFrom = validFrom;
		this.validTo = validTo;
		this.valid = valid;
		this.type = type;
		this.issuer = issuer;
		this.issuedTo = issuedTo;
	}



	public CertificateDTO(Certificate cert) {
		this.id = cert.getId();
		this.serialNumber = cert.getSerialNumber();
		this.validFrom = cert.getValidFrom();
		this.validTo = cert.getValidTo();
		this.valid = cert.isValid();
		this.type = cert.getType();
		this.issuedTo = new UserRetDTO(cert.getIssuedTo());
		this.issuer = new UserRetDTO(cert.getIssuer().getIssuedTo());
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getSerialNumber() {
		return serialNumber;
	}

	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}

	public LocalDateTime getValidFrom() {
		return validFrom;
	}

	public void setValidFrom(LocalDateTime validFrom) {
		this.validFrom = validFrom;
	}

	public LocalDateTime getValidTo() {
		return validTo;
	}

	public void setValidTo(LocalDateTime validTo) {
		this.validTo = validTo;
	}

	public boolean isValid() {
		return valid;
	}

	public void setValid(boolean valid) {
		this.valid = valid;
	}

	public CertificateType getType() {
		return type;
	}

	public void setType(CertificateType type) {
		this.type = type;
	}

	public UserRetDTO getIssuedTo() {
		return issuedTo;
	}

	public void setIssuedTo(UserRetDTO issuedTo) {
		this.issuedTo = issuedTo;
	}



	public UserRetDTO getIssuer() {
		return issuer;
	}



	public void setIssuer(UserRetDTO issuer) {
		this.issuer = issuer;
	}
	
	
}
