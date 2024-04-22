package com.certificate_manager.certificate_manager.security.recaptcha;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class RecaptchaResponse {
	boolean success;
	LocalDateTime challenge_ts;
	String hostname;
	@JsonProperty("error-codes")
	List<String> errorCodes;

	public RecaptchaResponse(boolean success, LocalDateTime challenge_ts, String hostname, List<String> errorCodes) {
		super();
		this.success = success;
		this.challenge_ts = challenge_ts;
		this.hostname = hostname;
		this.errorCodes = errorCodes;
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public LocalDateTime getChallenge_ts() {
		return challenge_ts;
	}

	public void setChallenge_ts(LocalDateTime challenge_ts) {
		this.challenge_ts = challenge_ts;
	}

	public String getHostname() {
		return hostname;
	}

	public void setHostname(String hostname) {
		this.hostname = hostname;
	}

	public List<String> getErrorCodes() {
		return errorCodes;
	}

	public void setErrorCodes(List<String> errorCodes) {
		this.errorCodes = errorCodes;
	}

}
