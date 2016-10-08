package com.hit.applications;

import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.hit.util.GetProperties;

public class BoxApplicationImpl extends AbstractApplication {
	private static Logger logger = Logger.getLogger(Office365ApplicationImpl.class);
	
	/***
	 * ctor
	 * @param driver
	 */
	public BoxApplicationImpl(WebDriver driver) {
		super(driver);
	}
	
	/***
	 * override method from IApplication
	 */
	@Override
	public boolean doAction(ApplicationRequest applicationRequest) throws NullPointerException, UnsupportedOperationException {
		logger.info("Application: Box");
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
	 * login to box
	 * @param logoutAtEnd
	 * @return
	 */
	private boolean login(boolean logoutAtEnd) {
		logger.info("logging to box");
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
			logger.error("could not login to box", e);
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
	 * logout form box
	 * @return
	 */
	private boolean logout() {
		logger.info("logout from box");
		if (!this.loggedIn)
			return true;
		try{
			driver.get("https://app.box.com/logout");
			driver.manage().deleteAllCookies();
		}catch(Exception e){
			logger.error("could not logout from box", e);
		}
		this.loggedIn = false;
		return true;
	}

}
