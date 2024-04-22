package com.certificate_manager.certificate_manager.dtos;

public class TokenDTO {
	private String accessToken;
	private String refreshToken;

	public TokenDTO(String accessToken, String refreshToken) {
		super();
		this.accessToken = accessToken;
		this.setRefreshToken(refreshToken);
	}

	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	public String getRefreshToken() {
		return refreshToken;
	}

	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}
}
