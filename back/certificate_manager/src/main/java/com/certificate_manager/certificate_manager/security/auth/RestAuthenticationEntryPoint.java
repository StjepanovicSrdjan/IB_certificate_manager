package com.certificate_manager.certificate_manager.security.auth;

import java.io.IOException;
import java.util.List;

import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.certificate_manager.certificate_manager.exceptions.CertificateNotFoundException;
import com.certificate_manager.certificate_manager.exceptions.CertificateNotValidException;
import com.certificate_manager.certificate_manager.exceptions.NoRequestForSMSVerification;
import com.certificate_manager.certificate_manager.exceptions.NotPendingRequestException;
import com.certificate_manager.certificate_manager.exceptions.NotTheIssuerException;
import com.certificate_manager.certificate_manager.exceptions.SMSCodeExpiredException;
import com.certificate_manager.certificate_manager.exceptions.SMSCodeIncorrectException;
import com.certificate_manager.certificate_manager.exceptions.UserAlreadyExistsException;
import com.certificate_manager.certificate_manager.exceptions.UserNotRegisteredOrAlreadyVerifiedException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.ConstraintViolationException;

@ControllerAdvice
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {

	@Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
    	System.out.println(authException);
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, authException.getMessage());
    }
	
    @ExceptionHandler (value = {AccessDeniedException.class})
    public void commence(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException {
        // 403
        setResponseError(response, HttpServletResponse.SC_FORBIDDEN, String.format("Access Denies: %s", accessDeniedException.getMessage()));
    }
    
    @ExceptionHandler (value = {NotFoundException.class})
    public void commence(HttpServletRequest request, HttpServletResponse response, NotFoundException notFoundException) throws IOException {
        // 404
        setResponseError(response, HttpServletResponse.SC_NOT_FOUND, String.format("Not found: %s", notFoundException.getMessage()));
    }
    
    @ExceptionHandler (value = {HttpMessageNotReadableException.class})
	protected ResponseEntity<String> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
		return new ResponseEntity<String>("Request body is required.", HttpStatus.BAD_REQUEST);
	}
    
    @ExceptionHandler(ConstraintViolationException.class)
	protected ResponseEntity<String> handleConstraintValidationException(ConstraintViolationException e) {
		return new ResponseEntity<String>("Constraint violation!", HttpStatus.BAD_REQUEST);
	}
    
    @ExceptionHandler(CertificateNotFoundException.class)
  	protected ResponseEntity<String> handleCertificateNotFoundException(CertificateNotFoundException e) {
  		return new ResponseEntity<String>("Certificate not found!", HttpStatus.BAD_REQUEST);
  	}
    
    @ExceptionHandler(BadCredentialsException.class)
  	protected ResponseEntity<String> handleBadCredentialsException(BadCredentialsException e) {
  		return new ResponseEntity<String>("Bad credentials!", HttpStatus.BAD_REQUEST);
  	}
    
    @ExceptionHandler(UserAlreadyExistsException.class)
    protected ResponseEntity<Object> handleUserAlreadyExistsException(UserAlreadyExistsException e){
    	return new ResponseEntity<>("User with this email already exists!", HttpStatus.BAD_REQUEST);
    }
    
    @ExceptionHandler(NotTheIssuerException.class)
    protected ResponseEntity<Object> handleNotTheIssuerException(NotTheIssuerException e){
    	return new ResponseEntity<>("Only the issuer can manage certificate requests.", HttpStatus.BAD_REQUEST);
    }
    
    @ExceptionHandler(NotPendingRequestException.class)
    protected ResponseEntity<Object> handleNotPendingRequestException(NotPendingRequestException e){
    	return new ResponseEntity<>("Only pending requests can be processed.", HttpStatus.BAD_REQUEST);
    }
    
    @ExceptionHandler(CertificateNotValidException.class)
    protected ResponseEntity<Object> handleCertificateNotValidException(CertificateNotValidException e){
    	return new ResponseEntity<>("Certificate is not valid!", HttpStatus.EXPECTATION_FAILED);
    }
    
    @ExceptionHandler (value = {UserNotRegisteredOrAlreadyVerifiedException.class})
	protected ResponseEntity<String> handleUserNotRegisteredOrAlreadyVerifiedException(UserNotRegisteredOrAlreadyVerifiedException e) {
		return new ResponseEntity<String>("User not registered or already verified!", HttpStatus.BAD_REQUEST);
	}
    
    @ExceptionHandler (value = {NoRequestForSMSVerification.class})
 	protected ResponseEntity<String> handleNoRequestForSMSVerification(NoRequestForSMSVerification e) {
 		return new ResponseEntity<String>("There has been no SMS code previously sent!", HttpStatus.NOT_FOUND);
 	}
    
    @ExceptionHandler (value = {SMSCodeExpiredException.class})
 	protected ResponseEntity<String> handleSMSCodeExpiredException(SMSCodeExpiredException e) {
 		return new ResponseEntity<String>("SMS code has expired!", HttpStatus.BAD_REQUEST);
 	}
    
    @ExceptionHandler (value = {SMSCodeIncorrectException.class})
 	protected ResponseEntity<String> handleSMSCodeIncorrectException(SMSCodeIncorrectException e) {
 		return new ResponseEntity<String>("SMS code entered is incorrect!", HttpStatus.BAD_REQUEST);
 	}
    
	
	@ExceptionHandler (value = {MethodArgumentNotValidException.class})
	protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
		List<ObjectError> errorList = ex.getBindingResult().getAllErrors();
        StringBuilder sb = new StringBuilder("Request finished with validation errors: \n");

        for (ObjectError error : errorList ) {
        	sb.append("Field ");
            FieldError fe = (FieldError) error;
            sb.append(fe.getField() + " ");
            sb.append(error.getDefaultMessage()+ "!\n\n");
        }

        return new ResponseEntity<>(sb.toString(), HttpStatus.BAD_REQUEST);
	}
    
    private void setResponseError(HttpServletResponse response, int errorCode, String errorMessage) throws IOException{
        response.setStatus(errorCode);
        response.getWriter().write(errorMessage);
        response.getWriter().flush();
        response.getWriter().close();
    }
}

