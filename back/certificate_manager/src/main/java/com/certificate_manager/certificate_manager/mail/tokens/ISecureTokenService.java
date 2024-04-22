package com.certificate_manager.certificate_manager.mail.tokens;

import com.certificate_manager.certificate_manager.entities.User;
import com.certificate_manager.certificate_manager.enums.SecureTokenType;

public interface ISecureTokenService {
	
public SecureToken findByToken(String token);
	
	public SecureToken createToken(User user, SecureTokenType type);

	public boolean isValid(SecureToken token);

	public void markAsUsed(SecureToken token);

}
