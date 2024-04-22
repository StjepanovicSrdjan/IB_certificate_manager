package com.certificate_manager.certificate_manager.dtos;

import jakarta.validation.constraints.NotEmpty;

public class RotatePasswordDTO {
	
	@NotEmpty(message = "is required")
	private String email;
	
	@NotEmpty(message = "is required")
	private String newPassword;
	
	@NotEmpty(message = "is required")
	private String oldPassword;
	
	public RotatePasswordDTO() {
		
	}

	public RotatePasswordDTO(String email, String oldPassword, String newPassword) {
		super();
		this.email = email;
		this.oldPassword = oldPassword;
		this.newPassword = newPassword;
	}

	public String getNewPassword() {
		return newPassword;
	}

	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}

	public String getOldPassword() {
		return oldPassword;
	}

	public void setOldPassword(String oldPassword) {
		this.oldPassword = oldPassword;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
	
	
}
