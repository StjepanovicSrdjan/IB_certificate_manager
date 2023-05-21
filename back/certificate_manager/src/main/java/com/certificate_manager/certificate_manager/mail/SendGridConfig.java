package com.certificate_manager.certificate_manager.mail;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.sendgrid.SendGrid;

@Configuration
public class SendGridConfig {
	
	@Value("${spring.sendgrid.api-key}")
	private String apiKey;
	
	@Bean
	public SendGrid getSendGrid() {
		return new SendGrid(this.apiKey);
	}
}
