package com.certificate_manager.certificate_manager.mail.tokens;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.time.Instant;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.certificate_manager.certificate_manager.entities.User;
import com.certificate_manager.certificate_manager.enums.SecureTokenType;

@Service
public class SecureTokenServiceImpl implements ISecureTokenService {
	
	@Autowired
	private SecureTokenRepository allTokens;
	
	
	@Override
	public SecureToken findByToken(String token) {
		return allTokens.findByToken(token).orElse(null);
	}

	@Override
	public SecureToken createToken(User user, SecureTokenType type) {
		SecureToken token = new SecureToken();
		SecureRandom random = new SecureRandom();
		String generatedToken = new BigInteger(30, random).toString(32).toUpperCase();

		if (generatedToken.length() > 6) {
		    generatedToken = generatedToken.substring(0, 6);
		} else if (generatedToken.length() < 6) {
		    while (generatedToken.length() < 6) {
		        generatedToken = "0" + generatedToken;
		    }
		}
		
		token.setToken(generatedToken);
		token.setUsed(false);
		token.setUser(user);
		token.setExpirationDate(Date.from(Instant.now().plus(2, ChronoUnit.MINUTES)).toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
		token.setType(type);
		
		this.allTokens.save(token);
		this.allTokens.flush();
		
		return token;
	}

	@Override
	public boolean isValid(SecureToken token) {
		if (token == null)
			return false;
		
		User user = token.getUser();
		if (user == null || (token.getType() == SecureTokenType.REGISTRATION && user.getVerified()) || token.isUsed()) {
			return false;
		}
		
		return true;
	}

	@Override
	public void markAsUsed(SecureToken token) {
		token.setUsed(true);
		allTokens.save(token);
		allTokens.flush();
	}
}
