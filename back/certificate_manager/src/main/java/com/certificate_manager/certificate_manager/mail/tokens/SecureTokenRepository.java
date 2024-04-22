package com.certificate_manager.certificate_manager.mail.tokens;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SecureTokenRepository extends JpaRepository<SecureToken, Integer> {
	
	public Optional<SecureToken> findByToken(String token);

}
