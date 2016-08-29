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
		
		if (req.getUseTor() == true) {
			driver = DriverUtils.getDriverWithTor(req.getUseIncognito());
		} else {
			driver = DriverUtils.getDriver(req.getUseIncognito(), req.getProxyAddr());
		}
		try {
			switch (req.getApplication().toUpperCase()) {
			case "GOOGLE":
				app = ApplicationFactory.GetGoogleApplication(driver);
				break;
			case "OFFICE365":
				app = ApplicationFactory.GetOffice365Application(driver);
				break;
			case "DROPBOX":
				app = ApplicationFactory.GetDropBoxApplication(driver);
				break;
			case "BOX":
				app = ApplicationFactory.GetBoxApplication(driver);
				break;
			default:
				throw new NullPointerException("Please enter a valid application");
			}
			int numOfRuns = req.getNumberOfRuns();
			for (int i = 0; i < numOfRuns; i++) {
				app.doAction(req);
			}
		} catch (Exception ex) {
			throw ex;
		} finally {
			driver.quit();
			if (req.getUseTor() == true) {
				DriverUtils.endTorSession();
			}
		}


		


	}
}
