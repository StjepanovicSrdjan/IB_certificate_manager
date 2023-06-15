package com.certificate_manager.certificate_manager.services;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.certificate_manager.certificate_manager.dtos.AccessTokenResponseDTO;
import com.certificate_manager.certificate_manager.dtos.OAuthUserDataDTO;
import com.certificate_manager.certificate_manager.dtos.TokenDTO;
import com.certificate_manager.certificate_manager.entities.User;
import com.certificate_manager.certificate_manager.exceptions.GoogleAuthException;
import com.certificate_manager.certificate_manager.exceptions.GoogleIdTokenException;
import com.certificate_manager.certificate_manager.repositories.UserRepository;
import com.certificate_manager.certificate_manager.security.jwt.IJWTTokenService;
import com.certificate_manager.certificate_manager.security.jwt.TokenUtils;
import com.certificate_manager.certificate_manager.services.interfaces.IOAuthService;
import com.certificate_manager.certificate_manager.services.interfaces.IUserService;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;

@Service
public class OAuthServiceImpl implements IOAuthService {

	private static final String CODE_TO_TOKEN_EXCHANGE_ENDPOINT = "https://oauth2.googleapis.com/token";
	private static final int DEFAULT_RANDOM_STRING_LENGTH = 10;
	
	@Autowired
	private UserRepository allUsers;
	
	@Autowired
	private IUserService userService;

	private RestTemplate restTemplate;
	
	@Autowired
	private TokenUtils tokenUtils;
	
	@Autowired
	private IJWTTokenService tokenJWTService;

	@Value("${oauth.google.client-id}")
	String clientId;

	@Value("${oauth.google.client-secret}")
	String clientSecret;

	@Value("${oauth.google.redirect-uri}")
	String redirectURI;

	public OAuthServiceImpl(RestTemplateBuilder templateBuilder) {
		this.restTemplate = templateBuilder.build();
	}

	@Override
	public TokenDTO signUpWithGoogle(String code) {
		AccessTokenResponseDTO returnedToken = this.exchangeCodeForToken(code);
		OAuthUserDataDTO returnedUser = this.getUserData(returnedToken.getIdToken());
		
		String jwt;
		if (returnedUser != null) {
			jwt = signInGoogleUser(returnedUser);
		} else {
			System.out.println("NULL OVDE");
			throw new GoogleAuthException();
		}
		System.out.println(returnedUser);
		System.out.println(jwt);
		return new TokenDTO(jwt, jwt);
	}

	private String signInGoogleUser(OAuthUserDataDTO returnedUser) {
		User dbUser = allUsers.findByEmail(returnedUser.getEmail()).orElse(null);
		
		if (dbUser == null)
			dbUser = registerOauthUser(returnedUser);
	
		String jwt = tokenUtils.generateToken(dbUser);
		this.tokenJWTService.createNoMFAToken(jwt);
		
		return jwt;
	}

	private User registerOauthUser(OAuthUserDataDTO returnedUser) {
		User newUser = new User(returnedUser.getName(), returnedUser.getLastname(), returnedUser.getEmail(), returnedUser.getEmailVerified(), returnedUser.getSub(), generateRandomString());
		newUser = allUsers.save(newUser);
		allUsers.flush();
		
		return newUser;
	}

	private AccessTokenResponseDTO exchangeCodeForToken(String code) {
		MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();

		requestBody.add("code", code);
		requestBody.add("client_id", clientId);
		requestBody.add("client_secret", clientSecret);
		requestBody.add("redirect_uri", redirectURI);
		requestBody.add("grant_type", "authorization_code");

		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

		RequestEntity<MultiValueMap<String, String>> requestEntity;
		AccessTokenResponseDTO res = null;
		try {
			requestEntity = RequestEntity.post(new URI(CODE_TO_TOKEN_EXCHANGE_ENDPOINT)).headers(headers)
					.contentType(MediaType.APPLICATION_FORM_URLENCODED).body(requestBody);
			ResponseEntity<AccessTokenResponseDTO> responseEntity = restTemplate.exchange(requestEntity, AccessTokenResponseDTO.class);
			res = responseEntity.getBody();
		} catch (URISyntaxException e) {
			throw new GoogleIdTokenException();
		}

		return res;
	}

	private OAuthUserDataDTO getUserData(String idTokenString) {
		HttpTransport transport = new NetHttpTransport();
		JsonFactory jsonFactory = new JacksonFactory();
		GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(transport, jsonFactory)
			    .setAudience(Collections.singletonList(clientId))
			    .build();

			GoogleIdToken idToken = null;
			try {
				idToken = verifier.verify(idTokenString);
			} catch (GeneralSecurityException | IOException e) {
				throw new GoogleIdTokenException();
			}
			if (idToken != null) {
			  Payload payload = idToken.getPayload();
			  
			  String userId = payload.getSubject();
			  String email = payload.getEmail();
			  boolean emailVerified = Boolean.valueOf(payload.getEmailVerified());
			  String name = (String) payload.get("name");
			  String pictureUrl = (String) payload.get("picture");
			  String locale = (String) payload.get("locale");
			  String familyName = (String) payload.get("family_name");
			  String givenName = (String) payload.get("given_name");

			  //TODO: Use or store profile information

			  System.out.println(payload.toString());
			  return new OAuthUserDataDTO(userId, givenName, familyName, email, emailVerified);

			} else {
				throw new GoogleIdTokenException();
			}
			
	}
	
	public static String generateRandomString() {
		SecureRandom secureRandom = new SecureRandom();
        byte[] randomBytes = new byte[DEFAULT_RANDOM_STRING_LENGTH];
        secureRandom.nextBytes(randomBytes);
        return Base64.getEncoder().encodeToString(randomBytes);
    }
	

}
