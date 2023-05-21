package com.certificate_manager.certificate_manager.security.jwt;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "jwttokens")
public class JWTToken {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	
	@Size(max = 500)
	private String token;
	
	private boolean valid;
	
	private boolean verified;

	public JWTToken() {}
	
	public JWTToken(int id, String token, boolean valid) {
		super();
		this.id = id;
		this.token = token;
		this.valid = valid;
	}
	
	public JWTToken(String token) {
		this.token = token;
		this.valid = false;
		this.verified = false;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public boolean isValid() {
		return valid;
	}

	public void setValid(boolean valid) {
		this.valid = valid;
	}

	public boolean isVerified() {
		return verified;
	}

	public void setVerified(boolean verified) {
		this.verified = verified;
	}
	
}
