package com.hit.applications;

import org.openqa.selenium.WebDriver;

public abstract class AbstractApplication implements IApplication {
	
	protected WebDriver driver;
	protected ApplicationRequest req;
	protected boolean loggedIn;

	public void setDriver(WebDriver driver) {
		this.driver = driver;
	}
	
	public boolean isLoggedIn() {
		return loggedIn;
	}
	public void setLoggedIn(boolean loggedIn) {
		this.loggedIn = loggedIn;
	}
}
