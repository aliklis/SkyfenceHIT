package com.hit.scenarios;

import org.openqa.selenium.WebDriver;

import com.hit.applications.ApplicationFactory;
import com.hit.applications.ApplicationRequest;
import com.hit.applications.IApplication;
import com.hit.util.StaticDriver;
import com.hit.util.XMLToRequest;

public class ScenarioManager {
	// this class will read an xml file and parse it to a request
	// currently hard coded
	// basically need to read the xml file in the main method and create req objects.
	// these req objects will be sent to the scenario manager to get the right application
	// and send the action to this application
	public static void run() {
		WebDriver driver;
		ApplicationRequest req;
		IApplication app;
		
		req = XMLToRequest.parseXML();
		
		// try login to one drive
		driver = StaticDriver.getDriver(false, null);
		app = ApplicationFactory.GetApplication(req.getApplication());
		app.setDriver(driver);
		app.doAction(req);
		driver.quit();

	}
}
