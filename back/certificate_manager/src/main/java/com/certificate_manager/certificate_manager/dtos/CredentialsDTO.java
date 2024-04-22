package com.certificate_manager.certificate_manager.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;

public class CredentialsDTO {
	
	@NotEmpty(message="is required")
	@Email(message="format is not valid")
	String email;
	
	@NotEmpty(message="is required")
	String password;
	
	public CredentialsDTO(String email, String password) {
		super();
		this.email = email;
		this.password = password;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@Override
	public String toString() {
		return "[email=" + email + ", password=" + password + "]";
	}
	
	
}
