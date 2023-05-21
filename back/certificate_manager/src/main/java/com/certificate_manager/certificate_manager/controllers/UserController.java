package com.certificate_manager.certificate_manager.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.certificate_manager.certificate_manager.dtos.CredentialsDTO;
import com.certificate_manager.certificate_manager.dtos.ResetPasswordDTO;
import com.certificate_manager.certificate_manager.dtos.ResponseMessageDTO;
import com.certificate_manager.certificate_manager.dtos.RotatePasswordDTO;
import com.certificate_manager.certificate_manager.dtos.TokenDTO;
import com.certificate_manager.certificate_manager.dtos.UserDTO;
import com.certificate_manager.certificate_manager.dtos.UserRetDTO;
import com.certificate_manager.certificate_manager.entities.User;
import com.certificate_manager.certificate_manager.security.jwt.IJWTTokenService;
import com.certificate_manager.certificate_manager.security.jwt.TokenUtils;
import com.certificate_manager.certificate_manager.security.recaptcha.ValidateCaptcha;
import com.certificate_manager.certificate_manager.services.interfaces.ICertificateGenerator;
import com.certificate_manager.certificate_manager.services.interfaces.IUsedPasswordService;
import com.certificate_manager.certificate_manager.services.interfaces.IUserService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;

@RestController
@RequestMapping("/api/user")
@CrossOrigin(origins = "http://localhost:4200")
@Validated
public class UserController {
	
	@Autowired
	private IUserService userService;
	
	@Autowired
	private ValidateCaptcha captchaValidator;
	
	@Autowired
	private ICertificateGenerator certificateGenerator;
	
	@Autowired
	private TokenUtils tokenUtils;
	
	@Autowired
	private IJWTTokenService tokenService;
	
	@Autowired
	private AuthenticationManager authenticationManager;
	
	@GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
//	@PreAuthorize("hasAnyRole('ADMIN', 'USER')")
	public ResponseEntity<?> getById(@PathVariable @Min(value = 0, message = "Field id must be greater than 0.") int id) {
		return new ResponseEntity<UserRetDTO>(this.userService.findById(id), HttpStatus.OK);
	}
	
	@PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> register(@Valid @RequestBody UserDTO userDTO, @RequestHeader String captcha) {
		this.captchaValidator.validateCaptcha(captcha);
		this.userService.register(userDTO);
		return new ResponseEntity<ResponseMessageDTO>(new ResponseMessageDTO("You have successfully registered!"), HttpStatus.OK);
	}
	
	@PostMapping(value = "send/verification/email/{email}")
	public ResponseEntity<?> sendVerificationMail(@PathVariable @NotEmpty(message = "Email is required") String email) {
		this.userService.sendEmailVerification(email); 
		return new ResponseEntity<ResponseMessageDTO>(new ResponseMessageDTO("We sent you a verification code!"), HttpStatus.OK);
	}
	
	@PostMapping(value = "send/twofactor/email/{email}")
	public ResponseEntity<?> sendTwoFactorMail(@PathVariable @NotEmpty(message = "Email is required") String email) {
		this.userService.sendTwoFactorEmail(email); 
		return new ResponseEntity<ResponseMessageDTO>(new ResponseMessageDTO("We sent you a verification code!"), HttpStatus.OK);
	}
	
	@GetMapping(value = "verify/twofactor/{activationId}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> verifyTwoFactor(@PathVariable("activationId") @NotEmpty(message = "Activation code is required") String verificationCode, HttpServletRequest request) {
		this.userService.verifyTwoFactor(verificationCode, tokenUtils.getToken(request));
		return new ResponseEntity<ResponseMessageDTO>(new ResponseMessageDTO("You have successfully signed in!"), HttpStatus.OK);
	}
	
	@GetMapping(value = "activate/{activationId}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> verifyRegistration(@PathVariable("activationId") @NotEmpty(message = "Activation code is required") String verificationCode) {
		this.userService.verifyRegistration(verificationCode);
		return new ResponseEntity<ResponseMessageDTO>(new ResponseMessageDTO("You have successfully activated your account!"), HttpStatus.OK);
	}
	
	@PostMapping(value = "/login", consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> login(@Valid @RequestBody CredentialsDTO credentials, @RequestHeader String captcha) {
		System.out.println(credentials);
		
		this.captchaValidator.validateCaptcha(captcha);
		
		Authentication authentication;
		try {
			authentication = authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(credentials.getEmail(), credentials.getPassword()));
		} catch (BadCredentialsException e) {
			return new ResponseEntity<String>("Wrong username or password!", HttpStatus.BAD_REQUEST);
		} catch (Exception ex) {
			System.out.println(ex.getStackTrace());
			return new ResponseEntity<String>(HttpStatus.UNAUTHORIZED);
		}
		SecurityContextHolder.getContext().setAuthentication(authentication);

		UserDetails user = (UserDetails) authentication.getPrincipal();
		User userFromDb = this.userService.getUserByEmail(credentials.getEmail());
		
		if (this.userService.isPasswordForRenewal(userFromDb))
			return new ResponseEntity<String>("You should renew your password!", HttpStatus.UNAUTHORIZED);
		if (!userFromDb.getVerified()) {
			return new ResponseEntity<ResponseMessageDTO>(new ResponseMessageDTO("This account have not been activated yet!"), HttpStatus.UNAUTHORIZED);
		}
		
		String jwt = tokenUtils.generateToken(user, userFromDb);
		this.tokenService.createToken(jwt);
		

		return new ResponseEntity<TokenDTO>(new TokenDTO(jwt, jwt), HttpStatus.OK);
		
	}
	
	@PutMapping(value = "rotatePassword", consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> rotatePassword(@Valid @RequestBody RotatePasswordDTO dto) {
		this.userService.rotatePassword(dto);
		return new ResponseEntity<ResponseMessageDTO>(new ResponseMessageDTO("Password successfully rotated!"), HttpStatus.NO_CONTENT);
	}
	
	@GetMapping(value = "reset/password/email/{email}")
	public ResponseEntity<?> sendResetPasswordMail(@PathVariable @NotEmpty(message = "Email is required") String email) {
		System.err.println("usao");
		this.userService.sendResetPasswordMail(email);
		return new ResponseEntity<ResponseMessageDTO>(new ResponseMessageDTO("Email with reset code has been sent!"), HttpStatus.NO_CONTENT);
	}
	
	@PutMapping(value = "resetPassword")
	public ResponseEntity<?> resetPassword(@Valid @RequestBody ResetPasswordDTO dto) {
		this.userService.resetPassword(dto);	
		return new ResponseEntity<ResponseMessageDTO>(new ResponseMessageDTO("Password successfully changed!"), HttpStatus.NO_CONTENT);
	}
}
