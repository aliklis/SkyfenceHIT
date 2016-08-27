package com.hit.applications;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.hit.util.GetProperties;

public class Office365ApplicationImpl extends AbstractApplication {

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
		case "LOGINONEDRIVE":
			return loginOneDrive();
		default:
			throw new UnsupportedOperationException("the requested action is not available");
		}
	}
	
	private boolean login() {
		String office365LoginURL = GetProperties.getProp("office365LoginURL");
		String office365UserTextbox = GetProperties.getProp("office365UserTextbox");
		String office365PasswordTextbox = GetProperties.getProp("office365PasswordTextbox"); 
		boolean office365DoubleSubmit = Boolean.parseBoolean(GetProperties.getProp("office365DoubleSubmit"));
		
		driver.get(office365LoginURL);
		// submit user name
		WebElement username = driver.findElement(By.name(office365UserTextbox));
		username.sendKeys(req.getUsername());
		if(office365DoubleSubmit){
			username.submit();
		}
		// submit password
		WebElement password = driver.findElement(By.name(office365PasswordTextbox));
		password.sendKeys(req.getPassword());
		password.submit();
		
		try {
			Thread.sleep(2000);
		} catch (Exception ex) {

		}
		return true;
	}
	
	private boolean loginOneDrive() {
		boolean officeLogin = login();
		if(officeLogin != true)
			return false;
		// if got here - expects to already be in login state in office365
		driver.get("https://portal.office.com/Home");
		WebElement OneDriveForwardURLElem = driver.findElement(By.id("ShellDocuments_link"));
		String URL = OneDriveForwardURLElem.getAttribute("href");
		driver.get(URL);

		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// } else {
		// TODO throw exception
		// }
		return true;
	}

}
