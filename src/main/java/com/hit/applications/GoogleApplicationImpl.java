package com.hit.applications;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.hit.util.GetProperties;

public class GoogleApplicationImpl extends AbstractApplication {
	
	public boolean doAction(ApplicationRequest req) throws NullPointerException, UnsupportedOperationException {
		if (req == null) {
			throw new NullPointerException("the application request object is not valid");
		}
		if (driver == null) {
			throw new NullPointerException("the driver object is not valid");
		}
		this.req = req;
		
		switch (req.getAction().toUpperCase()) {
		case "LOGIN":
			return login();
		default:
			throw new UnsupportedOperationException("the requested action is not available");
		}
	}

	private boolean login() {
		String googleLoginURL = GetProperties.getProp("googleLoginURL");
		String googleUserTextbox = GetProperties.getProp("googleUserTextbox");
		String googlePasswordTextbox = GetProperties.getProp("googlePasswordTextbox");
		boolean googleDoubleSubmit = Boolean.parseBoolean(GetProperties.getProp("googleDoubleSubmit"));
		driver.get(googleLoginURL);

		WebElement usernameElem = driver.findElement(By.name(googleUserTextbox));
		usernameElem.sendKeys(req.getUsername());
		if (googleDoubleSubmit) {
			usernameElem.submit();
		}
		// submit password
		WebElement passwordElem = driver.findElement(By.name(googlePasswordTextbox));
		passwordElem.sendKeys(req.getPassword());
		passwordElem.submit();
		try {
			Thread.sleep(2000);
		} catch (Exception ex) {

		}

		return true;
	}
}
