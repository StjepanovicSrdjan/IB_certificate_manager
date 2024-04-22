package com.certificate_manager.certificate_manager.dtos;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

public class WithdrawalReasonDTO {
	@Valid
	@NotEmpty(message="is required")
	private String reason;
	
	public WithdrawalReasonDTO() {};
	
	public WithdrawalReasonDTO(String reason) {
		this.reason = reason;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}
	
	
	
}
