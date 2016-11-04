package com.hit.applications;

import org.openqa.selenium.WebDriver;

public abstract class AbstractApplication implements IApplication {
	/**
	 * AbstractApplication constructor
	 * @param driver - Webdriver to use for the given application
	 */
	public AbstractApplication(WebDriver driver) {
		this.driver = driver;
	}
	
	protected WebDriver driver;
	protected ApplicationRequest applicationRequest;
	protected boolean loggedIn;

	public void setDriver(WebDriver driver) {
		this.driver = driver;
	}
}
