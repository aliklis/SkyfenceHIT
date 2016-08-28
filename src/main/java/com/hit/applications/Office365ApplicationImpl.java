package com.hit.applications;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.hit.util.GetProperties;
import com.hit.util.DriverUtils;

public class Office365ApplicationImpl extends AbstractApplication {

	@Override
	public boolean doAction(ApplicationRequest req){
		if (req == null) {
			throw new NullPointerException("the application request object is not valid");
		}
		if (driver == null) {
			throw new NullPointerException("the driver object is not valid");
		}
		this.req = req;
		
		switch (req.getAction().toUpperCase()) {
		case "LOGIN":
			return login();
		case "LOGINONEDRIVE":
			return loginOneDrive();
		case "MULTIPLEACTIONS":
			return MultipleActions();	
		case "LOGOUT":
			return logout();
		default:
			throw new UnsupportedOperationException("the requested action is not available");
		}
		
	}
	
	private boolean login() {
		String office365LoginURL = GetProperties.getProp("office365LoginURL");
		String office365UserTextbox = GetProperties.getProp("office365UserTextbox");
		String office365PasswordTextbox = GetProperties.getProp("office365PasswordTextbox"); 
		boolean office365DoubleSubmit = Boolean.parseBoolean(GetProperties.getProp("office365DoubleSubmit"));
		
		driver.get(office365LoginURL);
		// submit user name
		WebElement username = driver.findElement(By.name(office365UserTextbox));
		username.sendKeys(req.getUsername());
		if(office365DoubleSubmit){
			username.submit();
		}
		// submit password
		WebElement password = driver.findElement(By.name(office365PasswordTextbox));
		password.sendKeys(req.getPassword());
		password.submit();
		
		try {
			Thread.sleep(2000);
		} catch (Exception ex) {

		}
		
		setLoggedIn(true);
		return true;
	}
	
	private boolean loginOneDrive() {
		boolean officeLogin = login();
		if(officeLogin != true)
			return false;
		// if got here - expects to already be in login state in office365
		driver.get("https://portal.office.com/Home");
		WebElement OneDriveForwardURLElem = driver.findElement(By.id("ShellDocuments_link"));
		String URL = OneDriveForwardURLElem.getAttribute("href");
		driver.get(URL);

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
		if(login()){
			SendFeedBack();
			DriverUtils.sleep(3);
			OpenWordTemplate();
		}
		return true;
	}
	
	
	//1)
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
	
	//2)
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
	
	
	
	//3)
	private boolean logout(){
		if (loggedIn) {
			// click on element by className
			WebElement element = driver.findElement(By.className("o365cs-me-tile-nophoto-username-container"));
			if (element != null) {
				element.click();
			}
			// click on logout button
			DriverUtils.clickOnElementByID(driver,"O365_SubLink_ShellSignout");
		}
		setLoggedIn(false);
		return true;
	}
	
	
}
