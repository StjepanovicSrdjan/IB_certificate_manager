package com.certificate_manager.certificate_manager.sms;

public interface ISmsService {
			  
	public void sendVerificationSMS(String email);

	public void sendResetSMS(String phoneNumber);

	public void sendTwoFactorSms(String phoneNumber);

}