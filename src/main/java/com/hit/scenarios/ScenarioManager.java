package com.hit.scenarios;

import org.openqa.selenium.WebDriver;

import com.hit.applications.ApplicationFactory;
import com.hit.applications.ApplicationRequest;
import com.hit.applications.IApplication;
import com.hit.util.DriverUtils;

public class ScenarioManager {
	
	/***
	 * 	this class will read an xml file and parse it to a request
	 * currently hard coded
	 * basically need to read the xml file in the main method and create req objects.
	 * these req objects will be sent to the scenario manager to get the right application
	 * and send the action to this application
	 */
	public static void run(ApplicationRequest req) {
		WebDriver driver;
		IApplication app;
		
		/***
		 * request will create either a driver with tor
		 * or a regular login (that might include proxy if proxy != null)
		 * but if tor is set to true - will not check for proxy address
		 */
		if(req.getUseTor() == true) {
			driver = DriverUtils.getDriverWithTor(false);
		}
		else {
			driver = DriverUtils.getDriver(false, req.getProxyAddr());
		}
		
		app = ApplicationFactory.GetApplication(req.getApplication());
		app.setDriver(driver);
		app.doAction(req);
		
		//ApplicationRequest req2 = new ApplicationRequest.Builder(req.getApplication(), "logout").build();
		//app.doAction(req2);
		
		driver.quit();
		if(req.getUseTor() == true) {
			DriverUtils.endTorSession();
		}
		


	}
}
