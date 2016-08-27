package com.hit.applications;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.hit.util.GetProperties;

public class BoxApplicationImpl extends AbstractApplication {

	@Override
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
		String boxLoginURL = GetProperties.getProp("boxLoginURL");
		String boxUserTextbox = GetProperties.getProp("boxUserTextbox");
		String boxPasswordTextbox = GetProperties.getProp("boxPasswordTextbox");
		boolean boxDoubleSubmit = Boolean.parseBoolean(GetProperties.getProp("boxDoubleSubmit"));
		
		driver.get(boxLoginURL);
		// submit user name
		WebElement username = driver.findElement(By.name(boxUserTextbox));
		username.sendKeys(req.getUsername());
		if(boxDoubleSubmit){
			username.submit();
		}
		// submit password
		WebElement password = driver.findElement(By.name(boxPasswordTextbox));
		password.sendKeys(req.getPassword());
		password.submit();
		
		try {
			Thread.sleep(2000);
		} catch (Exception ex) {

		}
		//driver.quit();
		return true;
	}

}
