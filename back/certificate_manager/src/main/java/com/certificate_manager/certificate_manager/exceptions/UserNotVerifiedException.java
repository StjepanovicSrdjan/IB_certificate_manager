package com.certificate_manager.certificate_manager.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.UNAUTHORIZED, reason = "User not verified!")
public class UserNotVerifiedException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5034183354283359103L;

}
