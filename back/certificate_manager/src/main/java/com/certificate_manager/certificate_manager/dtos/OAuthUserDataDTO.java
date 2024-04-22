package com.certificate_manager.certificate_manager.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;

public class OAuthUserDataDTO {
	private String sub;
	@JsonProperty("given_name")
	private String name;
	@JsonProperty("family_name")
	private String lastname;
	private String email;
	@JsonProperty("email_verified")
	private Boolean emailVerified;
	
	public OAuthUserDataDTO() {}

	public OAuthUserDataDTO(String sub, String name, String lastname, String email, Boolean emailVerified) {
		super();
		this.sub = sub;
		this.name = name;
		this.lastname = lastname;
		this.email = email;
		this.emailVerified = emailVerified;
	}

	public String getSub() {
		return sub;
	}

	public void setSub(String sub) {
		this.sub = sub;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLastname() {
		return lastname;
	}

	public void setLastname(String lastname) {
		this.lastname = lastname;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Boolean getEmailVerified() {
		return emailVerified;
	}

	public void setEmailVerified(Boolean emailVerified) {
		this.emailVerified = emailVerified;
	}

	@Override
	public String toString() {
		return "OAuthUserDataDTO [sub=" + sub + ", name=" + name + ", lastname=" + lastname + ", email=" + email
				+ ", emailVerified=" + emailVerified + "]";
	}

}
