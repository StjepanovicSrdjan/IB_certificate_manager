package com.certificate_manager.certificate_manager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class CertificateManagerApplication {
  
	public static void main(String[] args) {
		SpringApplication.run(CertificateManagerApplication.class, args);
	}
 
}
