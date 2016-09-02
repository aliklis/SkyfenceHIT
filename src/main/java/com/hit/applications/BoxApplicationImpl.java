package com.hit.applications;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.hit.util.GetProperties;

public class BoxApplicationImpl extends AbstractApplication {

	public BoxApplicationImpl(WebDriver driver) {
		super(driver);
	}
	@Override
	public boolean doAction(ApplicationRequest applicationRequest) throws NullPointerException, UnsupportedOperationException {
		if (applicationRequest == null) {
			throw new NullPointerException("the application request object is not valid");
		}
		if (driver == null) {
			throw new NullPointerException("the driver object is not valid");
		}
		this.applicationRequest = applicationRequest;
		
		switch (applicationRequest.getAction()) {
		case "LOGIN":
			return login(true);
		default:
			throw new UnsupportedOperationException("the requested action is not available");
		}
	}
	
	private boolean login(boolean logoutAtEnd) {
		if(this.loggedIn)
			return true;
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
		
		try {
			Thread.sleep(2000);
		} catch (Exception ex) {

		}
		this.loggedIn = true;
		if (logoutAtEnd)
			logout();
		return true;
	}
	
	private boolean logout() {
		if (!this.loggedIn)
			return true;
		driver.get("https://app.box.com/logout");
		driver.manage().deleteAllCookies();
		this.loggedIn = false;
		return true;
	}

}
