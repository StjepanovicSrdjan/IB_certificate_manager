package com.certificate_manager.certificate_manager.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.certificate_manager.certificate_manager.entities.User;
import com.certificate_manager.certificate_manager.repositories.UserRepository;
import com.certificate_manager.certificate_manager.services.interfaces.ILoggingService;
import com.certificate_manager.certificate_manager.services.interfaces.IUserService;

@Service
public class LoggingService implements ILoggingService {
	
	@Autowired
	UserRepository allUsers;
	
//	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Override
	public void logUserInfo(String action, Logger logger) {
        User currentUser = this.getCurrentUser();

        MDC.put("user", currentUser.getEmail());
        logger.info(action);
        MDC.remove("user");
	}
	
	@Override
	public void logServerInfo(String action, Logger logger) {
	 	MDC.put("user", "SERVER");
	    logger.info(action);
	    MDC.remove("user");
	}
	
	@Override
	public void logUserError(String action, Logger logger) {
        User currentUser = this.getCurrentUser();

        MDC.put("user", currentUser.getEmail());
        logger.error(action);
        MDC.remove("user");
	}
	
	@Override
	public void logServerError(String action, Logger logger) {
	 	MDC.put("user", "SERVER");
	    logger.error(action);
	    MDC.remove("user");
	}
	
	private User getCurrentUser() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		return allUsers.findByEmail(auth.getName()).orElse(null);
	}
}
