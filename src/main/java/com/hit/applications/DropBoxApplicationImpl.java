package com.hit.applications;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.hit.util.GetProperties;

public class DropBoxApplicationImpl extends AbstractApplication {

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
			return login(true);
		default:
			throw new UnsupportedOperationException("the requested action is not available");
		}
	}
	private boolean login(boolean logoutAtEnd) {
		if(this.loggedIn)
			return true;
		String dropboxLoginURL = GetProperties.getProp("dropboxLoginURL");
		String dropboxUserTextbox = GetProperties.getProp("dropboxUserTextbox");
		String dropboxPasswordTextbox = GetProperties.getProp("dropboxPasswordTextbox");
		boolean dropboxDoubleSubmit = Boolean.parseBoolean(GetProperties.getProp("dropboxDoubleSubmit"));
		driver.get(dropboxLoginURL);
		
		// submit user name
		//By byXpathUN = By.xpath("//input[(@name='login_email') and (@type = 'email')]");
		By byXpathUN = By.xpath("//input[(@name='"+dropboxUserTextbox+"') and (@type = 'email')]");
		WebElement username = driver.findElement(byXpathUN);
		username.sendKeys(req.getUsername());
		
		if(dropboxDoubleSubmit){
			username.submit();
		}
		
		By byXpathPW = By.xpath("//input[(@name='"+dropboxPasswordTextbox+"') and (@type = 'password')]");
		WebElement password = driver.findElement(byXpathPW);
		// submit password
		password.sendKeys(req.getPassword());
		password.submit();
		
		try{
		Thread.sleep(3000);
		}
		catch(Exception ex){
			
		}
		
		this.loggedIn = true;
		if (logoutAtEnd)
			logout();
		return true;
	}
	
	private boolean logout() {
		if (!this.loggedIn)
			return true;
		driver.get("https://www.dropbox.com/logout");
		driver.manage().deleteAllCookies();
		this.loggedIn = false;
		return true;
	}

}
