package com.certificate_manager.certificate_manager.dtos;

import jakarta.validation.constraints.NotEmpty;

public class ResetPasswordDTO {
	
	@NotEmpty(message = "is required")
	private String newPassword;

	@NotEmpty(message = "is required")
	private String code;

	public ResetPasswordDTO(@NotEmpty(message = "is required") String newPassword,
			@NotEmpty(message = "is required") String code) {
		super();
		this.newPassword = newPassword;
		this.code = code;
	}

	public String getNewPassword() {
		return newPassword;
	}

	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}
}
