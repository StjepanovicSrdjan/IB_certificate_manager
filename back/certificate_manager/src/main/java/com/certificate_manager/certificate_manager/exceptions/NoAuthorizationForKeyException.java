package com.certificate_manager.certificate_manager.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "You have no authorization to acces the key for the certificate.")
public class NoAuthorizationForKeyException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7436760917787145765L;

}
