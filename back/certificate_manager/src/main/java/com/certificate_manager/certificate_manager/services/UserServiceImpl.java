package com.certificate_manager.certificate_manager.services;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.certificate_manager.certificate_manager.dtos.RotatePasswordDTO;
import com.certificate_manager.certificate_manager.dtos.ResetPasswordDTO;
import com.certificate_manager.certificate_manager.dtos.UserDTO;
import com.certificate_manager.certificate_manager.dtos.UserRetDTO;
import com.certificate_manager.certificate_manager.entities.User;
import com.certificate_manager.certificate_manager.enums.SecureTokenType;
import com.certificate_manager.certificate_manager.enums.UserRole;
import com.certificate_manager.certificate_manager.exceptions.PasswordsNotMatchingException;
import com.certificate_manager.certificate_manager.exceptions.UserAlreadyExistsException;
import com.certificate_manager.certificate_manager.exceptions.UserNotFoundException;
import com.certificate_manager.certificate_manager.mail.IMailService;
import com.certificate_manager.certificate_manager.mail.tokens.ISecureTokenService;
import com.certificate_manager.certificate_manager.mail.tokens.SecureToken;
import com.certificate_manager.certificate_manager.repositories.UserRepository;
import com.certificate_manager.certificate_manager.services.interfaces.IUsedPasswordService;
import com.certificate_manager.certificate_manager.security.jwt.IJWTTokenService;
import com.certificate_manager.certificate_manager.services.interfaces.IUserService;

@Service
public class UserServiceImpl implements IUserService, UserDetailsService{
	
	@Autowired
	private UserRepository allUsers;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Autowired
	private IUsedPasswordService usedPasswordService;
	
	@Autowired
	private ISecureTokenService tokenService;
	
	@Autowired
	private IJWTTokenService jwtService;
	
	@Autowired
	private IMailService mailService;
	
	@Value("${password-time-for-renawal}")
	private long timeForRenawal;
	
	@Override
	public User getUserByEmail(String email) {
		return allUsers.findByEmail(email).orElseThrow(() -> new UserNotFoundException());
	}
	
	@Override
	public boolean doesUserExist(String email) {
		try {
			this.getUserByEmail(email);
			return true;
		}
		catch (UserNotFoundException e){
			return false;
		}
	}
	
	@Override
	public void register(UserDTO userDTO) {
		if (this.doesUserExist(userDTO.getEmail()))
			throw new UserAlreadyExistsException();
		System.out.println(passwordEncoder);
		User user = new User(userDTO);
		//this.usedPasswordService.checkForUsedPasswords(user, userDTO.getPassword());
		user.setPassword(userDTO.getPassword());
		user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
		user.setTimeOfLastSetPassword(LocalDateTime.now());
		user.setRole(UserRole.USER);
		allUsers.save(user);
		allUsers.flush();
	}
	
	@Override
	public void sendEmailVerification(String email) {
		User user = getUserByEmail(email);
		if (user.getVerified()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "This account has been already verified.");
		}
		SecureToken token = tokenService.createToken(user, SecureTokenType.REGISTRATION);
		this.mailService.sendVerificationMail(user, token.getToken());
	}
	
	@Override
	public void sendTwoFactorEmail(String email) {
		User user = getUserByEmail(email);
		SecureToken token = tokenService.createToken(user, SecureTokenType.TWO_FACTOR_AUTH);
		this.mailService.sendVerificationMail(user, token.getToken());
	}
	
	@Override
	public void verifyTwoFactor(String verificationCode, String jwt) {
		SecureToken token = this.tokenService.findByToken(verificationCode);
		
		if (token == null) {
			jwtService.invalidateToken(jwt);
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Two-factor authentication with entered id does not exist!");
		}

		if (!this.tokenService.isValid(token) || token.isExpired()) {
			jwtService.invalidateToken(jwt);
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid token!");
		}
		
		jwtService.verifyToken(jwt);
		tokenService.markAsUsed(token);
	}
	
	@Override
	public void verifyRegistration(String verificationCode) {
		SecureToken token = this.tokenService.findByToken(verificationCode);
		
		if (token == null) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Activation with entered id does not exist!");
		}

		if (!this.tokenService.isValid(token) || token.isExpired() || token.getType() != SecureTokenType.REGISTRATION) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid token!");
		}

		this.activateUser(token.getUser());
		this.tokenService.markAsUsed(token);
	}
	
	@Override
	public void resetPassword(ResetPasswordDTO dto) {
		SecureToken token = this.tokenService.findByToken(dto.getCode());
		if (token == null || !this.tokenService.isValid(token) || token.isExpired() || token.getType() != SecureTokenType.FORGOT_PASSWORD) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Code is expired or not correct!");
		}
		
		User user = token.getUser();
		
		user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
		allUsers.save(user);
		allUsers.flush();

		tokenService.markAsUsed(token);
		System.err.println(dto.getNewPassword());  
	}
	
	@Override
	public void sendResetPasswordMail(String email) {
		User user = this.allUsers.findByEmail(email).orElse(null);
		if (user == null){
			throw new UserNotFoundException();
		}
		SecureToken token = tokenService.createToken(user, SecureTokenType.FORGOT_PASSWORD);

		mailService.sendForgotPasswordMail(user, token.getToken());
	}
	
	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		Optional<User> ret = allUsers.findByEmail(email);
		System.out.println(email);
		System.out.println(allUsers.count());
		if (!ret.isEmpty()) {
			System.out.println(ret.get().getEmail());
			return org.springframework.security.core.userdetails.User.withUsername(email).password(ret.get().getPassword()).roles(ret.get().getRole().toString()).build();
		}
		throw new UsernameNotFoundException("User not found with this email: " + email);
	}
	
	@Override
	public User getCurrentUser() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		return allUsers.findByEmail(auth.getName()).orElse(null);
	}

	@Override
	public UserRetDTO findById(int id) {
		return new UserRetDTO(allUsers.findById((long) id).orElseThrow(() -> new UserNotFoundException()));
	}
	
	
	private void activateUser(User user) {
		user.setVerified(true);
		this.allUsers.save(user);
		this.allUsers.flush();
	}

	@Override
	public boolean isPasswordForRenewal(User user) {
		if (user.getTimeOfLastSetPassword().isBefore(LocalDateTime.now().minusMinutes(timeForRenawal))) 
			return true;
		return false;
	}

	@Override
	public void rotatePassword(RotatePasswordDTO dto) {
		User user = this.getUserByEmail(dto.getEmail());
		if (user.getPassword() != passwordEncoder.encode(dto.getOldPassword()))
			throw new PasswordsNotMatchingException();
		
		this.usedPasswordService.checkForUsedPasswordsOfOwner(user.getId(), dto.getNewPassword());
		user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
		this.usedPasswordService.addNewPassword(user);
		user.setTimeOfLastSetPassword(LocalDateTime.now());
		allUsers.save(user);
		allUsers.flush();
	}

}
