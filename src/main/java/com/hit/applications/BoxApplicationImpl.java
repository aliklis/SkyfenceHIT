package com.hit.applications;

import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.hit.util.GetProperties;

public class BoxApplicationImpl extends AbstractApplication {
	private static Logger logger = Logger.getLogger(BoxApplicationImpl.class);
	
	public BoxApplicationImpl(WebDriver driver) {
		super(driver);
	}
	
	@Override
	public boolean doAction(ApplicationRequest applicationRequest) throws NullPointerException, UnsupportedOperationException {
		logger.info("Application: Box");
		if (applicationRequest == null) {
			logger.error("The application request object is not valid");
			throw new NullPointerException("The application request object is not valid");
		}
		if (driver == null) {
			logger.error("The driver object is not valid");
			throw new NullPointerException("The driver object is not valid");
		}
		this.applicationRequest = applicationRequest;
		logger.info("Action requested : " + applicationRequest.getAction());
		switch (applicationRequest.getAction()) {
		case "LOGIN":
			return login(true);
		default:
			logger.error("The requested action is not available");
			throw new UnsupportedOperationException("The requested action is not available");
		}
	}
	
	/***
	 * Log in to Box
	 * @param logoutAtEnd
	 * @return
	 */
	private boolean login(boolean logoutAtEnd) {
		logger.info("Trying to log in to box");
		if(this.loggedIn)
			return true;
		try{
			String boxLoginURL = GetProperties.getProp("boxLoginURL");
			String boxUserTextbox = GetProperties.getProp("boxUserTextbox");
			String boxPasswordTextbox = GetProperties.getProp("boxPasswordTextbox");
			boolean boxDoubleSubmit = Boolean.parseBoolean(GetProperties.getProp("boxDoubleSubmit"));
			
			driver.get(boxLoginURL);
			// submit user name
			WebElement username = driver.findElement(By.name(boxUserTextbox));
			username.sendKeys(applicationRequest.getUser().getUsername());
			if(boxDoubleSubmit){
				username.submit();
			}
			// submit password
			WebElement password = driver.findElement(By.name(boxPasswordTextbox));
			password.sendKeys(applicationRequest.getUser().getPassword());
			password.submit();
		}catch(Exception e){
			logger.error("Could not log in to box", e);
		}
		try {
			Thread.sleep(2000);
		} catch (Exception ex) {

		}
		this.loggedIn = true;
		if (logoutAtEnd)
			logout();
		return true;
	}
	
	/***
	 * Log out from box
	 * @return
	 */
	private boolean logout() {
		logger.info("Trying to log out from box");
		if (!this.loggedIn)
			return true;
		try{
			driver.get("https://app.box.com/logout");
			driver.manage().deleteAllCookies();
		}catch(Exception e){
			logger.error("Could not log out from box", e);
		}
		this.loggedIn = false;
		return true;
	}

}
