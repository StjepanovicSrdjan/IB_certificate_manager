package com.certificate_manager.certificate_manager.services.interfaces;

import com.certificate_manager.certificate_manager.entities.User;

public interface IUsedPasswordService {
	
	public void addNewPassword(User user);

	public void checkForUsedPasswordsOfOwner(int id, String newPassword);

}
