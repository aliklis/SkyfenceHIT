package com.hit.scenarios;

import java.util.List;

import org.apache.log4j.Logger;
import org.openqa.selenium.WebDriver;

import com.hit.applications.ApplicationFactory;
import com.hit.applications.ApplicationRequest;
import com.hit.applications.IApplication;
import com.hit.util.DriverUtils;
import com.hit.util.Request;
import com.hit.util.User;

public class ScenarioManager {
	private static Logger logger = Logger.getLogger(ScenarioManager.class);

	/**
	 * Runs a scenario by the request and proxy
	 * 
	 * @param request
	 *            to be ran
	 * @param Proxy
	 *            if needed
	 */
	public static void run(Request request, String proxy) {
		WebDriver driver;
		IApplication app;
		ApplicationRequest applicationRequest = new ApplicationRequest(request.getAction());

		/***
		 * request will create either a driver with Tor or a regular login (that
		 * might include proxy if proxy != null) but if tor is set to true -
		 * will not check for proxy address
		 */
		if (request.isUseTor() == true) {
			logger.info("Using Tor to run this scenario");
			driver = DriverUtils.getDriverWithTor(request.isUseIncognito());
		} else {
			driver = DriverUtils.getDriver(request.isUseIncognito(), proxy);
		}

		logger.info("Is incognito:" + request.isUseIncognito());
		logger.info("Number of runs:" + request.getNumOfRuns());
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
				logger.error("An invalid application has been requested");
				throw new NullPointerException("An invalid application has been requested");
			}

			int numberOfRuns = request.getNumOfRuns();
			List<User> usersList = request.getUsers();

			// Run the action numberOfRuns times
			for (int i = 0; i < numberOfRuns; i++) {
				for (User currUser : usersList) {
					applicationRequest.setUser(currUser);
					app.doAction(applicationRequest);
				}
			}
		} catch (Exception ex) {
			logger.error("Scenario Manager failed to run this scenario");
			throw ex;
		} finally {
			driver.quit();
			if (request.isUseTor() == true) {
				DriverUtils.endTorSession();
			}
		}
	}
}
