package com.certificate_manager.certificate_manager.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AccessTokenResponseDTO {
	@JsonProperty("access_token")
	private String accessToken;
	@JsonProperty("id_token")
	private String idToken;
	@JsonProperty("token_type")
	private String tokenType;
	private String scope;
	@JsonProperty("expires_in")
	private String expiresIn;
	
	public AccessTokenResponseDTO() {}

	public AccessTokenResponseDTO(String accessToken, String idToken, String tokenType, String scope,
			String expiresIn) {
		super();
		this.accessToken = accessToken;
		this.idToken = idToken;
		this.tokenType = tokenType;
		this.scope = scope;
		this.expiresIn = expiresIn;
	}

	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	public String getIdToken() {
		return idToken;
	}

	public void setIdToken(String idToken) {
		this.idToken = idToken;
	}

	public String getTokenType() {
		return tokenType;
	}

	public void setTokenType(String tokenType) {
		this.tokenType = tokenType;
	}

	public String getScope() {
		return scope;
	}

	public void setScope(String scope) {
		this.scope = scope;
	}

	public String getExpiresIn() {
		return expiresIn;
	}

	public void setExpiresIn(String expiresIn) {
		this.expiresIn = expiresIn;
	}

}