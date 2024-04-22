package com.certificate_manager.certificate_manager.entities;

import java.security.cert.X509Certificate;
import java.time.LocalDateTime;

import com.certificate_manager.certificate_manager.enums.CertificateType;
import com.certificate_manager.certificate_manager.utils.DateUtils;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "certificates")
public class Certificate {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	private String serialNumber;

	private LocalDateTime validFrom;

	private LocalDateTime validTo;

	private boolean valid;

	private CertificateType type;
	
	private String withdrawalReason;

	@ManyToOne
	private Certificate issuer;

	@ManyToOne
	private User issuedTo;

	public Certificate() {

	}

	public Certificate(String serialNumber, LocalDateTime validFrom, LocalDateTime validTo, boolean valid,
			CertificateType type, Certificate issuer, User issuedTo) {
		super();    
		this.serialNumber = serialNumber;
		this.validFrom = validFrom;
		this.validTo = validTo;
		this.valid = valid;
		this.type = type;
		this.issuer = issuer;
		this.issuedTo = issuedTo;
	} 

	public Certificate(CertificateRequest request, X509Certificate cert509, Certificate issuer) {
		this.serialNumber = cert509.getSerialNumber().toString();
		this.issuedTo = request.getRequester();
		this.validFrom = DateUtils.toLocalDate(cert509.getNotBefore());
		this.validTo = DateUtils.toLocalDate(cert509.getNotAfter());
		this.issuer = issuer;
		this.valid = true;
		this.type = request.getType();
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

	public Certificate getIssuer() {
		return issuer;
	}

	public void setIssuer(Certificate issuer) {
		this.issuer = issuer;
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

	public User getIssuedTo() {
		return issuedTo;
	}

	public void setIssuedTo(User issuedTo) {
		this.issuedTo = issuedTo;
	}

	public String getWithdrawalReason() {
		return withdrawalReason;
	}

	public void setWithdrawalReason(String withdrawalReason) {
		this.withdrawalReason = withdrawalReason;
	}
	
	

}
