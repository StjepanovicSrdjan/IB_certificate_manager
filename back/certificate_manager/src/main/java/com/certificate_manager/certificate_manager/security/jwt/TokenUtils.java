package com.certificate_manager.certificate_manager.security.jwt;

import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import com.certificate_manager.certificate_manager.entities.User;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.servlet.http.HttpServletRequest;

// Utility klasa za rad sa JSON Web Tokenima
@Component
public class TokenUtils {

	@Value("Certificate_manager")
	private String APP_NAME;

	@Value("somesecret")
	public String SECRET;

	@Value("1800000")
	private int EXPIRES_IN;
	
	@Value("Authorization")
	private String AUTH_HEADER;
	
	private static final String AUDIENCE_WEB = "web";

	// Algoritam za potpisivanje JWT
	private SignatureAlgorithm SIGNATURE_ALGORITHM = SignatureAlgorithm.HS512;
	

	// ============= Funkcije za generisanje JWT tokena =============
	
	/**
	 * Funkcija za generisanje JWT tokena.
	 * 
	 * @param username Korisničko ime korisnika kojem se token izdaje
	 * @return JWT token
	 */
	public String generateToken(UserDetails user, User userFromDb) {
		return Jwts.builder()
				.setIssuer(APP_NAME)
				.setSubject(user.getUsername())
				.claim("role", user.getAuthorities())
				.claim("id", userFromDb.getId())
				.claim("name", userFromDb.getName())
				.claim("lastname", userFromDb.getLastname())
				.setAudience(generateAudience())
				.setIssuedAt(new Date())
				.setExpiration(generateExpirationDate())
				.signWith(SIGNATURE_ALGORITHM, SECRET.getBytes()).compact();
	}
	
	public String generateToken(User user) {
		return Jwts.builder()
				.setIssuer(APP_NAME)
				.setSubject(user.getEmail())
				.claim("role", "ROLE_USER")
				.claim("id", user.getId())
				.claim("name", user.getName())
				.claim("lastname", user.getLastname())
				.setAudience(generateAudience())
				.setIssuedAt(new Date())
				.setExpiration(generateExpirationDate())
				.signWith(SIGNATURE_ALGORITHM, SECRET.getBytes()).compact();
	}
	
	public String renewToken(String token) {
		return Jwts.builder()
				.setIssuer(APP_NAME)
				.setSubject(getUsernameFromToken(token))
				.claim("role", getRoleFromToken(token))
				.setAudience(generateAudience())
				.setIssuedAt(new Date())
				.setExpiration(generateExpirationDate())
				.signWith(SIGNATURE_ALGORITHM, SECRET.getBytes()).compact();
	}
	
	
	/**
	 * Funkcija za utvrđivanje tipa uređaja za koji se JWT kreira.
	 * @return Tip uređaja. 
	 */
	private String generateAudience() {
		return AUDIENCE_WEB;
	}

	/**
	 * Funkcija generiše datum do kog je JWT token validan.
	 * 
	 * @return Datum do kojeg je JWT validan.
	 */
	private Date generateExpirationDate() {
		return new Date(new Date().getTime() + EXPIRES_IN);
	}
	
	// =================================================================
	
	// ============= Funkcije za citanje informacija iz JWT tokena =============
	
	/**
	 * Funkcija za preuzimanje JWT tokena iz zahteva.
	 * 
	 * @param request HTTP zahtev koji klijent šalje.
	 * @return JWT token ili null ukoliko se token ne nalazi u odgovarajućem zaglavlju HTTP zahteva.
	 */
	public String getToken(HttpServletRequest request) {
		String authHeader = getAuthHeaderFromHeader(request);

		if (authHeader != null && authHeader.startsWith("Bearer ")) {
			authHeader =  authHeader.substring(7);
			while(authHeader.charAt(0) == '\"' || authHeader.charAt(authHeader.length() -1) == '\"') {
				if (authHeader.charAt(0) == '\"')
					authHeader = authHeader.substring(1, authHeader.length());
				if (authHeader.charAt(authHeader.length() - 1) == '\"')
					authHeader = authHeader.substring(0, authHeader.length() - 1);
			}
			return authHeader;
		}

		return null;
	}
	
	/**
	 * Funkcija za preuzimanje vlasnika tokena (korisničko ime).
	 * @param token JWT token.
	 * @return Korisničko ime iz tokena ili null ukoliko ne postoji.
	 */
	public String getUsernameFromToken(String token) {
		String username;
		
		try {
			final Claims claims = this.getAllClaimsFromToken(token);
			username = claims.getSubject();
		} catch (ExpiredJwtException ex) {
			throw ex;
		} catch (Exception e) {
			username = null;
		}
		
		return username;
	}
	
	public Object getRoleFromToken(String token) {
		Object role;
		
		try {
			final Claims claims = this.getAllClaimsFromToken(token);
			role = claims.get("role");
		} catch (ExpiredJwtException ex) {
			throw ex;
		} catch (Exception e) {
			role = null;
		}
		
		return role;
	}
	

	/**
	 * Funkcija za preuzimanje datuma kreiranja tokena.
	 * @param token JWT token.
	 * @return Datum kada je token kreiran.
	 */
	public Date getIssuedAtDateFromToken(String token) {
		Date issueAt;
		try {
			final Claims claims = this.getAllClaimsFromToken(token);
			issueAt = claims.getIssuedAt();
		} catch (ExpiredJwtException ex) {
			throw ex;
		} catch (Exception e) {
			issueAt = null;
		}
		return issueAt;
	}

	/**
	 * Funkcija za preuzimanje informacije o uređaju iz tokena.
	 * 
	 * @param token JWT token.
	 * @return Tip uredjaja.
	 */
	public String getAudienceFromToken(String token) {
		String audience;
		try {
			final Claims claims = this.getAllClaimsFromToken(token);
			audience = claims.getAudience();
		} catch (ExpiredJwtException ex) {
			throw ex;
		} catch (Exception e) {
			audience = null;
		}
		return audience;
	}

	/**
	 * Funkcija za preuzimanje datuma do kada token važi.
	 * 
	 * @param token JWT token.
	 * @return Datum do kojeg token važi.
	 */
	public Date getExpirationDateFromToken(String token) {
		Date expiration;
		try {
			final Claims claims = this.getAllClaimsFromToken(token);
			expiration = claims.getExpiration();
		} catch (ExpiredJwtException ex) {
			throw ex;
		} catch (Exception e) {
			expiration = null;
		}
		
		return expiration;
	}
	
	/**
	 * Funkcija za čitanje svih podataka iz JWT tokena
	 * 
	 * @param token JWT token.
	 * @return Podaci iz tokena.
	 */
	private Claims getAllClaimsFromToken(String token) {
		Claims claims;
		try {
			claims = Jwts.parser()
					.setSigningKey(SECRET.getBytes())
					.parseClaimsJws(token)
					.getBody();
		} catch (ExpiredJwtException ex) {
			throw ex;
		} catch (Exception e) {
			claims = null;
		}
				
		return claims;
	}
	
	// =================================================================
	
	// ============= Funkcije za validaciju JWT tokena =============
	
	/**
	 * Funkcija za validaciju JWT tokena.
	 * 
	 * @param token JWT token.
	 * @param userDetails Informacije o korisniku koji je vlasnik JWT tokena.
	 * @return Informacija da li je token validan ili ne.
	 */
	public Boolean validateToken(String token, UserDetails userDetails) {
		UserDetails user = (UserDetails) userDetails;
		final String username = getUsernameFromToken(token);
		final Date created = getIssuedAtDateFromToken(token);
		
		//TODO: need to add verification for password change, if user changed his password after token creation then it's invalid. Use method below
		return (username != null
			&& username.equals(userDetails.getUsername())); 
	}
	
	/**
	 * Funkcija proverava da li je lozinka korisnika izmenjena nakon izdavanja tokena.
	 * 
	 * @param created Datum kreiranja tokena.
	 * @param lastPasswordReset Datum poslednje izmene lozinke.
	 * @return Informacija da li je token kreiran pre poslednje izmene lozinke ili ne.
	 */
	private Boolean isCreatedBeforeLastPasswordReset(Date created, Date lastPasswordReset) {
		return (lastPasswordReset != null && created.before(lastPasswordReset));
	}
	
	// =================================================================
	
	/**
	 * Funkcija za preuzimanje perioda važenja tokena.
	 * 
	 * @return Period važenja tokena.
	 */
	public int getExpiredIn() {
		return EXPIRES_IN;
	}

	/**
	 * Funkcija za preuzimanje sadržaja AUTH_HEADER-a iz zahteva.
	 * 
	 * @param request HTTP zahtev.
	 * 
	 * @return Sadrzaj iz AUTH_HEADER-a.
	 */
	public String getAuthHeaderFromHeader(HttpServletRequest request) {
		return request.getHeader(AUTH_HEADER);
	}
	
}