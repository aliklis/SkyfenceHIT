package com.hit.applications;

import java.util.List;

import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.hit.util.GetProperties;
import com.hit.util.DriverUtils;

public class Office365ApplicationImpl extends AbstractApplication {
	private static Logger logger = Logger.getLogger(Office365ApplicationImpl.class);
	
	/***
	 * ctor
	 * @param driver
	 */
	public Office365ApplicationImpl(WebDriver driver) {
		super(driver);
	}
	
	/***
	 * override method from IApplication
	 */
	@Override
	public boolean doAction(ApplicationRequest applicationRequest){
		logger.info("Application: Office365");
		
		if (applicationRequest == null) {
			logger.error("the application request object is not valid");
			throw new NullPointerException("the application request object is not valid");
		}
		if (driver == null) {
			logger.error("the driver object is not valid");
			throw new NullPointerException("the driver object is not valid");
		}
		
		this.applicationRequest = applicationRequest;
		logger.info("Action:" + applicationRequest.getAction());
		switch (applicationRequest.getAction()) {
		case "LOGIN":
			return login(true);
		case "LOGINONEDRIVE":
			return loginOneDrive();
		case "MULTIPLEACTIONS":
			return MultipleActions();	
		case "UPLOAD":
			return upload();
		case "DOWNLOAD":
			return download();
		case "LOGOUT":
			return logout();
		default:
			logger.error("the requested action is not available");
			throw new UnsupportedOperationException("the requested action is not available");
		}
		
	}
	
	/***
	 * login to office365
	 * @param logoutAtEnd
	 * @return
	 */
	private boolean login(boolean logoutAtEnd) {
		logger.info("logging to office365");
		try{
			String office365LoginURL = GetProperties.getProp("office365LoginURL");
			String office365UserTextbox = GetProperties.getProp("office365UserTextbox");
			String office365PasswordTextbox = GetProperties.getProp("office365PasswordTextbox"); 
			boolean office365DoubleSubmit = Boolean.parseBoolean(GetProperties.getProp("office365DoubleSubmit"));
			
			driver.get(office365LoginURL);
			// submit user name
			WebElement username = driver.findElement(By.name(office365UserTextbox));
			username.sendKeys(applicationRequest.getUser().getUsername());
			if(office365DoubleSubmit){
				username.submit();
			}
			// submit password
			WebElement password = driver.findElement(By.name(office365PasswordTextbox));
			password.sendKeys(applicationRequest.getUser().getPassword());
			password.submit();
		}catch(Exception e){
			logger.error("could not log in to office 365", e);
		}
		try {
			Thread.sleep(2000);
		} catch (Exception ex) {
			
		}
		
		this.loggedIn = true;
		if(logoutAtEnd)
			logout();
		return true;
	}
	
	/***
	 * login to oneDrive
	 * @return
	 */
	private boolean loginOneDrive() {
		logger.info("logging to OneDrive");
		boolean officeLogin = login(false);
		if(officeLogin != true)
			return false;
		try{
			// if got here - expects to already be in login state in office365
			driver.get("https://portal.office.com/Home");
			WebElement OneDriveForwardURLElem = driver.findElement(By.id("ShellDocuments_link"));
			String URL = OneDriveForwardURLElem.getAttribute("href");
			driver.get(URL);
		}catch(Exception e){
			logger.error("could not log into login oneDrive", e);
		}
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) { 
			e.printStackTrace();
		}
		return true;
	}

	/***
	 * make multiple actions on page
	 * @return
	 */
	private boolean MultipleActions(){
		if(login(false)){
			SendFeedBack();
			DriverUtils.sleep(5);
			OpenWordTemplate();
		}
		return true;
	}
	
	/***
	 * click on the word icon
	 * @throws InterruptedException 
	 */
	private void OpenWordTemplate(){
		//go to last opened window in the web
		DriverUtils.getLastOpenedWindow(driver);
		
		//click on an element by tagname
		DriverUtils.clickOnElementByTagNameAndAttribute(driver,"a", "ng-href", "https://office.live.com/start/Word.aspx?auth=2");
		
		DriverUtils.sleep(2);
		
		//go to last opened window in the web
		DriverUtils.getLastOpenedWindow(driver);
			
		//click on feedback from help menu
		DriverUtils.clickOnElementByID(driver, "template_TM00002003");
		

		
	}
	
	/***
	 * send feedback
	 * @throws InterruptedException 
	 */
	private void SendFeedBack(){
		
		//click on the help menu
		DriverUtils.clickOnElementByID(driver, "O365_MainLink_Help");
		
		//click on feedback from help menu
		DriverUtils.clickOnElementByID(driver, "O365_SubLink_ShellFeedback");
		
		//go to last opened window in the web
		DriverUtils.getLastOpenedWindow(driver);
		
		//insert comment in feedback
		DriverUtils.writeToHTMLElement(driver, "txtFeedbackComment","nice service, thank you!!!");
	
		//send feedback
		DriverUtils.clickOnElementByID(driver, "btnFeedbackSubmit");
		//sleep for a second, let the feedback to be sent
		DriverUtils.sleep(1);
		//close feedBack windows
		DriverUtils.clickOnElementByID(driver, "btnFeedbackClose");
						
	}
	
	/***
	 * logout
	 * @return
	 */
	private boolean logout(){
		logger.info("logging out from office365");
		if (this.loggedIn) {
			try{
				// click on element by className
				WebElement element = driver.findElement(By.className("o365cs-me-tile-nophoto-username-container"));
				if (element != null) {
					element.click();
				}
				// click on logout button
				DriverUtils.clickOnElementByID(driver,"O365_SubLink_ShellSignout");
				driver.manage().deleteAllCookies();
			}catch(Exception e){
				logger.error("could not log out from office365", e);
			}
		}
		this.loggedIn = false;
		return true;
	}
	
	private boolean download(){
		return true;
	}
	
	
	/*I AM HERE*/
	private boolean upload(){
		//logger.info("upload file");
		if(login(false)){
			try{
				//open oneDrive
				driver.get("https://veridinet-my.sharepoint.com/_layouts/15/MySite.aspx?MySiteRedirect=AllDocuments");
				DriverUtils.sleep(10);
				
				//click on upload button in navigation bar
				List<WebElement> elementList = driver.findElements(By.tagName("span"));	
				for (WebElement element : elementList){					
					if(element.getText().toUpperCase().equals("UPLOAD")){
						element.click();
						break;
					}
				}
				DriverUtils.sleep(1);
				//click on file upload
				DriverUtils.clickOnElementByTagNameAndAttribute(driver, "div", "aria-label", "Files");
				
				DriverUtils.sleep(2);
				
				
			}catch(Exception e){
				logger.error("could not upload file");
			}
		}
		return true;
	}
		
}
