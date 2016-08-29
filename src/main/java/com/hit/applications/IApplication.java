package com.hit.applications;

import org.openqa.selenium.WebDriver;

public interface IApplication {
	// Generic interface for an application
	// runs an action and returns a generic response
	public boolean doAction(ApplicationRequest req);
	public void setDriver(WebDriver driver);
}
