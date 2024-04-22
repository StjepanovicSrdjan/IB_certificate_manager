package com.certificate_manager.certificate_manager.security.recaptcha;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.certificate_manager.certificate_manager.exceptions.BadCaptchaException;
import com.certificate_manager.certificate_manager.exceptions.NotTheIssuerException;

@Service
public class ValidateCaptcha {
	private RestTemplate template;
	
    @Value("${recaptcha.verify.url}")
    String recaptchaUrl;
    
    @Value("${recaptcha.secret}")
    String recaptchaSecret;

    public ValidateCaptcha(RestTemplateBuilder templateBuilder) {
        this.template = templateBuilder.build();
    }
    
    public void validateCaptcha(String captchaToken) {
    	
		if (captchaToken == null || captchaToken.isBlank()) {
			throw new NotTheIssuerException();
		}
		
		if (!this.validateCaptchaGoogle(captchaToken)) {
			throw new BadCaptchaException();
		}
    }
    
    private boolean validateCaptchaGoogle(String captchaToken) {
    	final MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
    	params.add("secret", recaptchaSecret);
    	params.add("response", captchaToken);
    	
    	ResponseEntity<RecaptchaResponse> response = template.postForEntity(recaptchaUrl, params, RecaptchaResponse.class);
    	
    	if (response.getStatusCode() != HttpStatusCode.valueOf(200)) {
    		System.out.println("NIJE 200 " + response);
    		return false;
    	}
    	
    	RecaptchaResponse responseBody = response.getBody();
    	
    	if (responseBody == null || !responseBody.isSuccess()) {
    		System.out.println("NIJE BODY " + responseBody.toString());
    		System.out.println("NIJE BODY " + responseBody.errorCodes);
    		System.out.println("NIJE success " + responseBody.isSuccess());
    		return false;
    	}
    	
    	return true;
    }
}
