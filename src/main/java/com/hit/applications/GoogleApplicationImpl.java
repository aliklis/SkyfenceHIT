package com.hit.applications;

import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.hit.util.GetProperties;

public class GoogleApplicationImpl extends AbstractApplication {
	private static Logger logger = Logger.getLogger(GoogleApplicationImpl.class);
	
	/***
	 * ctor
	 */
	public GoogleApplicationImpl(WebDriver driver) {
		super(driver);
	}

	/***
	 * override method from IApplication
	 */
	@Override
	public boolean doAction(ApplicationRequest applicationRequest) throws NullPointerException, UnsupportedOperationException {
		logger.info("Application: Google");
		
		if (applicationRequest == null) {
			logger.error("the application request object is not valid");
			throw new NullPointerException("the application request object is not valid");
		}
		if (driver == null) {
			logger.error("the driver object is not valid");
			throw new NullPointerException("the driver object is not valid");
		}
		this.applicationRequest = applicationRequest;
		logger.info("Action:" + applicationRequest.getAction());
		switch (applicationRequest.getAction()) {
		case "LOGIN":
			return login(true);
		default:
			logger.error("the requested action is not available");
			throw new UnsupportedOperationException("the requested action is not available");
		}
	}

	/***
	 * login to Google
	 * @param logoutAtEnd
	 * @return
	 */
	private boolean login(boolean logoutAtEnd) {
		logger.info("logging to google");
		if (this.loggedIn)
			return true;
		try{
			String googleLoginURL = GetProperties.getProp("googleLoginURL");
			String googleUserTextbox = GetProperties.getProp("googleUserTextbox");
			String googlePasswordTextbox = GetProperties.getProp("googlePasswordTextbox");
			boolean googleDoubleSubmit = Boolean.parseBoolean(GetProperties.getProp("googleDoubleSubmit"));
			driver.get(googleLoginURL);
	
			WebElement usernameElem = driver.findElement(By.name(googleUserTextbox));
			usernameElem.sendKeys(applicationRequest.getUser().getUsername());
			if (googleDoubleSubmit) {
				usernameElem.submit();
			}
			// submit password
			WebElement passwordElem = driver.findElement(By.name(googlePasswordTextbox));
			passwordElem.sendKeys(applicationRequest.getUser().getPassword());
			passwordElem.submit();
		}catch(Exception e){
			logger.error("could not login to google", e);
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
	 * logout from google
	 * @return
	 */
	private boolean logout() {
		logger.info("logging out from google");
		if (!this.loggedIn)
			return true;
		try{
			driver.get("https://accounts.google.com/Logout");
			driver.manage().deleteAllCookies();
		}
		catch(Exception e){
			logger.error("could not log out from google", e);
		}
		this.loggedIn = false;
		return true;
	}
}
