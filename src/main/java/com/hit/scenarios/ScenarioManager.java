package com.hit.scenarios;

import java.util.List;

import org.openqa.selenium.WebDriver;

import com.hit.applications.ApplicationFactory;
import com.hit.applications.ApplicationRequest;
import com.hit.applications.IApplication;
import com.hit.util.DriverUtils;
import com.hit.util.Request;
import com.hit.util.User;

public class ScenarioManager {
	
	/***
	 * 	this class will read an xml file and parse it to a request
	 * currently hard coded
	 * basically need to read the xml file in the main method and create req objects.
	 * these req objects will be sent to the scenario manager to get the right application
	 * and send the action to this application
	 */
	public static void run(Request request, String Proxy) {
		WebDriver driver;
		IApplication app;
		ApplicationRequest applicationRequest = new ApplicationRequest(request.getAction());
		
		/***
		 * request will create either a driver with tor
		 * or a regular login (that might include proxy if proxy != null)
		 * but if tor is set to true - will not check for proxy address
		 */
		
		if (request.isUseTor() == true) {
			driver = DriverUtils.getDriverWithTor(request.isUseIncognito());
		} else {
			driver = DriverUtils.getDriver(request.isUseIncognito(), Proxy);
		}
		try {
			switch (request.getApplication().toUpperCase()) {
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

			int numberOfRuns = request.getNumOfRuns();
			List<User> usersList = request.getUsers();
			//iterate the action as the number of run
			for (int i = 0; i < numberOfRuns; i++) {
				for(User currUser : usersList){
					applicationRequest.setUser(currUser);
					app.doAction(applicationRequest);
				}
				
			}
		} catch (Exception ex) {
			throw ex;
		} finally {
			driver.quit();
			if (request.isUseTor() == true) {
				DriverUtils.endTorSession();
			}
		}


		


	}
}
