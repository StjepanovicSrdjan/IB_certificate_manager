package com.certificate_manager.certificate_manager.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "No captcha was received!")
public class NoCaptchaException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6212178527151597983L;

}
