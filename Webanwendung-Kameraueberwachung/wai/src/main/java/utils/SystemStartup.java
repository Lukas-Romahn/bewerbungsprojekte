package utils;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public final class SystemStartup implements ServletContextListener {
	
	private static Logger logger = LogManager.getLogger(SystemStartup.class);
	
	public void contextInitialized(ServletContextEvent event) {
		logger.info("ContextInitialized");
	}
	
	public void contextDestroyed(ServletContextEvent event) {
		logger.info("ContextDestroyed");
	}
}
