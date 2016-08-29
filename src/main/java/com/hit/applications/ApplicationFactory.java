package com.hit.applications;

import org.openqa.selenium.WebDriver;

public class ApplicationFactory {

	private ApplicationFactory() {
	}
	
	public static IApplication GetGoogleApplication(WebDriver driver) {
		return new GoogleApplicationImpl(driver);
	}
	public static IApplication GetOffice365Application(WebDriver driver) {
		return new Office365ApplicationImpl(driver);
	}
	public static IApplication GetDropBoxApplication(WebDriver driver) {
		return new DropBoxApplicationImpl(driver);
	}
	public static IApplication GetBoxApplication(WebDriver driver) {
		return new BoxApplicationImpl(driver);
	}

}
