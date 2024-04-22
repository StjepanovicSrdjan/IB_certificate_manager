package com.certificate_manager.certificate_manager.services.interfaces;

import com.certificate_manager.certificate_manager.dtos.ResetPasswordDTO;
import com.certificate_manager.certificate_manager.dtos.RotatePasswordDTO;
import com.certificate_manager.certificate_manager.dtos.UserDTO;
import com.certificate_manager.certificate_manager.dtos.UserRetDTO;
import com.certificate_manager.certificate_manager.entities.User;


public interface IUserService {
	
	public void register(UserDTO userDTO);
	
//	public String login(CredentialsDTO credDTO);
	
	public User getUserByEmail(String email);
	
	public boolean doesUserExist(String email);

	public User getCurrentUser();

	public UserRetDTO findById(int id);

	public boolean isPasswordForRenewal(User user);

	public void rotatePassword(RotatePasswordDTO dto);

	public void sendEmailVerification(String email);

	public void verifyRegistration(String verificationCode);

	public void resetPassword(ResetPasswordDTO dto);

	public void sendResetPasswordMail(String email);

	public void sendTwoFactorEmail(String email);

	public void verifyTwoFactor(String verificationCode, String jwt);
	
	public User getUserByPhoneNumber(String phoneNumber); 
}
