package com.certificate_manager.certificate_manager.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.certificate_manager.certificate_manager.entities.UsedPassword;
import com.certificate_manager.certificate_manager.entities.User;
import com.certificate_manager.certificate_manager.exceptions.PasswordAlreadyUsedException;
import com.certificate_manager.certificate_manager.repositories.UsedPasswordRepository;
import com.certificate_manager.certificate_manager.services.interfaces.IUsedPasswordService;

@Service
public class UsedPasswordServiceImpl implements IUsedPasswordService{
	
	@Autowired
	private UsedPasswordRepository allUsedPasswords;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Value("${password-limit}")
	private int limit;
	
	@Override
	public void checkForUsedPasswordsOfOwner(int ownerId, String newPassword) {
		List<UsedPassword> usedPasswords = this.getUsedPasswordsByOwner(ownerId);
		String encodedNewPassword = passwordEncoder.encode(newPassword);
		for (UsedPassword usedPassword: usedPasswords) {
			if (usedPassword.getPassword().equals(encodedNewPassword))
				throw new PasswordAlreadyUsedException();
		}
	}
	
	@Override
	public void addNewPassword(User owner) {
		UsedPassword newUsedPassword = new UsedPassword(owner);
		this.deletePasswordsNotForChecking(owner.getId());
		allUsedPasswords.save(newUsedPassword);
		allUsedPasswords.flush();
	}
	
	private List<UsedPassword> getUsedPasswordsByOwner(int ownerId) {
		return allUsedPasswords.findByOwnerId(ownerId);
	}
	
	private void deletePasswordsNotForChecking(int ownerId) {
		List<UsedPassword> usedPasswords = this.getUsedPasswordsByOwner(ownerId);
		for (int i = 0; i < this.limit; i++) {
			allUsedPasswords.delete(usedPasswords.get(i));
			allUsedPasswords.flush();
		}
	}

}
