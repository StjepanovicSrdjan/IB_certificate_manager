package com.certificate_manager.certificate_manager.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_GATEWAY, reason = "Error occured while trying to proccess your Google sign in. Try again.")
public class GoogleAuthException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 94795334979837953L;

}
