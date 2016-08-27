package com.hit.applications;

import org.openqa.selenium.WebDriver;

public abstract class AbstractApplication implements IApplication {
	
	protected WebDriver driver;
	protected ApplicationRequest req;

	public void setDriver(WebDriver driver) {
		this.driver = driver;
	}
}
