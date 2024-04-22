package com.certificate_manager.certificate_manager.services.interfaces;

import org.slf4j.Logger;

public interface ILoggingService {

	public void logServerInfo(String action, Logger logger);

	public void logUserInfo(String action, Logger logger);

	public void logUserError(String action, Logger logger);

	public void logServerError(String action, Logger logger);

}
