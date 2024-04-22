package com.certificate_manager.certificate_manager.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "Error occured while trying to proccess your Google sign in. Try again.")
public class GoogleIdTokenException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4262688874751271352L;

}
