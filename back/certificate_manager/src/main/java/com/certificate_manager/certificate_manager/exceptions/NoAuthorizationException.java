package com.certificate_manager.certificate_manager.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.UNAUTHORIZED, reason = "You don't have authorization to intiate this operation.")
public class NoAuthorizationException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7751012643757745753L;

}
