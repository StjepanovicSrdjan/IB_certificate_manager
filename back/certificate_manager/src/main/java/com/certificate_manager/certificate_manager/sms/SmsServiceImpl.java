package com.certificate_manager.certificate_manager.sms;

import java.util.Base64;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import com.certificate_manager.certificate_manager.entities.User;
import com.certificate_manager.certificate_manager.enums.SecureTokenType;
import com.certificate_manager.certificate_manager.exceptions.UserNotFoundException;
import com.certificate_manager.certificate_manager.mail.tokens.ISecureTokenService;
import com.certificate_manager.certificate_manager.mail.tokens.SecureToken;
import com.certificate_manager.certificate_manager.services.interfaces.IUserService; 

//import okhttp3.internal.ws.RealWebSocket.Message;

@Service
public class SmsServiceImpl implements ISmsService{

	static final String text = "Your verification code for Certivus is %s. It's valid for two minutes.";
	
	@Value("${spring.twilio-account-sid}")
	private String twilioAccountSid;
	
	@Value("${spring.twilio-phone-number}")
	private String twilioPhoneNumber;
	
	@Value("${spring.twilio-auth-token}")
	private String twilioAuthToken;
	
	private final String TWILIO_API_URL = "https://api.twilio.com/2010-04-01/Accounts/{Your_Account_SID}/Messages.json";
    
    	
	@Autowired
	private IUserService userService;
	
	@Autowired
	private ISecureTokenService tokenService;
 
	@Override
    public void sendVerificationSMS(String email) {
		User user = userService.getUserByEmail(email);
		if (user.getVerified()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "This account has been already verified.");
		}
		SecureToken token = tokenService.createToken(user, SecureTokenType.REGISTRATION);
		sendRequestForSms(user.getPhoneNumber(), twilioPhoneNumber, String.format(text, token.getToken()));
	}
	
	@Override
	public void sendResetSMS(String phoneNumber) {
		User user;
		try {
			user = userService.getUserByPhoneNumber(phoneNumber);
		} catch (UserNotFoundException e){
			return;
		}
		SecureToken token = tokenService.createToken(user, SecureTokenType.FORGOT_PASSWORD);
		
		sendRequestForSms(phoneNumber, twilioPhoneNumber, String.format(text, token.getToken()));
	}
	
	@Override
	public void sendTwoFactorSms(String phoneNumber) {
		User user;
		try {
			user = userService.getUserByPhoneNumber(phoneNumber);
		} catch (UserNotFoundException e){
			return;
		}
		SecureToken token = tokenService.createToken(user, SecureTokenType.TWO_FACTOR_AUTH);
		sendRequestForSms(phoneNumber, twilioPhoneNumber, String.format(text, token.getToken()));
	}
       

    public void sendRequestForSms(String to, String from, String body) {
        RestTemplate restTemplate = new RestTemplate();

        restTemplate.getMessageConverters().add(new FormHttpMessageConverter());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setBasicAuth(twilioAccountSid, twilioAuthToken);

        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
        requestBody.add("To", to);
        requestBody.add("From", from);
        requestBody.add("Body", body);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(requestBody, headers);

        ResponseEntity<String> response = restTemplate.exchange(
                TWILIO_API_URL.replace("{Your_Account_SID}", twilioAccountSid),
                HttpMethod.POST,
                request,
                String.class
        );

        if (response.getStatusCode().is2xxSuccessful()) {
            System.out.println("SMS sent successfully!");
        } else {
            System.out.println("Failed to send SMS. Response: " + response.getBody());
        }
    }
}