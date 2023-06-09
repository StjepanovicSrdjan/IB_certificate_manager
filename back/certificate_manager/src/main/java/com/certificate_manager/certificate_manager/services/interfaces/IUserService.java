package com.certificate_manager.certificate_manager.services.interfaces;

import com.certificate_manager.certificate_manager.dtos.UserDTO;
import com.certificate_manager.certificate_manager.entities.User;

public interface IUserService {
	
	public void register(UserDTO userDTO);
	
	public User getUserByEmail(String email);
	
	public boolean doesUserExist(String email);

	public User getCurrentUser();
}
