package com.certificate_manager.certificate_manager.services.interfaces;

import com.certificate_manager.certificate_manager.dtos.TokenDTO;

public interface IOAuthService {
	public TokenDTO signUpWithGoogle(String code);
}
