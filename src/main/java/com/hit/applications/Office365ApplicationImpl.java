package com.hit.applications;

import java.math.BigInteger;
import java.security.SecureRandom;
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
	 * 
	 * @param driver
	 */
	public Office365ApplicationImpl(WebDriver driver) {
		super(driver);
	}

	/***
	 * override method from IApplication
	 */
	@Override
	public boolean doAction(ApplicationRequest applicationRequest) {
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
		case "DELETE_FILE":
			return delete("file");
		case "DELETE_FOLDER":
			return delete("folder");
		case "RENAME_FILE":
			return rename();
		case "LOGOUT":
			return logout();
		default:
			logger.error("the requested action is not available");
			throw new UnsupportedOperationException("the requested action is not available");
		}

	}

	/***
	 * login to office365
	 * 
	 * @param logoutAtEnd
	 * @return
	 */
	private boolean login(boolean logoutAtEnd) {
		logger.info("logging to office365");
		try {
			String office365LoginURL = GetProperties.getProp("office365LoginURL");
			String office365UserTextbox = GetProperties.getProp("office365UserTextbox");
			String office365PasswordTextbox = GetProperties.getProp("office365PasswordTextbox");
			boolean office365DoubleSubmit = Boolean.parseBoolean(GetProperties.getProp("office365DoubleSubmit"));

			driver.get(office365LoginURL);
			// submit user name
			WebElement username = driver.findElement(By.name(office365UserTextbox));
			username.sendKeys(applicationRequest.getUser().getUsername());
			if (office365DoubleSubmit) {
				username.submit();
			}
			// submit password
			WebElement password = driver.findElement(By.name(office365PasswordTextbox));
			password.sendKeys(applicationRequest.getUser().getPassword());
			password.submit();
		} catch (Exception e) {
			logger.error("could not log in to office 365", e);
			return false;
		}
		try {
			Thread.sleep(2000);
		} catch (Exception ex) {
			logger.error("error in sleeping method", ex);
			return false;

		}

		this.loggedIn = true;
		if (logoutAtEnd)
			logout();
		return true;
	}

	/***
	 * login to oneDrive
	 * 
	 * @return
	 */
	private boolean loginOneDrive() {
		logger.info("logging to OneDrive");
		boolean officeLogin = login(false);
		if (officeLogin != true)
			return false;
		try {
			// if got here - expects to already be in login state in office365
			driver.get("https://portal.office.com/Home");
			WebElement OneDriveForwardURLElem = driver.findElement(By.id("ShellDocuments_link"));
			String URL = OneDriveForwardURLElem.getAttribute("href");
			driver.get(URL);
		} catch (Exception e) {
			logger.error("could not log into login oneDrive", e);
			return false;
		}
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			logger.error("error in sleeping method", e);
			return false;
		}
		return true;
	}

	/***
	 * make multiple actions on page
	 * 
	 * @return
	 */
	private boolean MultipleActions() {
		if (login(false)) {
			SendFeedBack();
			DriverUtils.sleep(5000);
			OpenWordTemplate();
		}
		return true;
	}

	/***
	 * click on the word icon
	 * 
	 * @throws InterruptedException
	 */
	private void OpenWordTemplate() {
		// go to last opened window in the web
		DriverUtils.getLastOpenedWindow(driver);

		// click on an element by tagname
		DriverUtils.clickOnElementByTagNameAndAttribute(driver, "a", "ng-href","https://office.live.com/start/Word.aspx?auth=2", null);

		DriverUtils.sleep(2000);

		// go to last opened window in the web
		DriverUtils.getLastOpenedWindow(driver);

		// click on feedback from help menu
		DriverUtils.clickOnElementByID(driver, "template_TM00002003");

	}

	/***
	 * send feedback
	 * 
	 * @throws InterruptedException
	 */
	private void SendFeedBack() {

		// click on the help menu
		DriverUtils.clickOnElementByID(driver, "O365_MainLink_Help");

		// click on feedback from help menu
		DriverUtils.clickOnElementByID(driver, "O365_SubLink_ShellFeedback");

		// go to last opened window in the web
		DriverUtils.getLastOpenedWindow(driver);

		// insert comment in feedback
		DriverUtils.writeToHTMLElement(driver, "txtFeedbackComment", "nice service, thank you!!!");

		// send feedback
		DriverUtils.clickOnElementByID(driver, "btnFeedbackSubmit");
		// sleep for a second, let the feedback to be sent
		DriverUtils.sleep(1000);
		// close feedBack windows
		DriverUtils.clickOnElementByID(driver, "btnFeedbackClose");

	}

	/***
	 * logout
	 * 
	 * @return
	 */
	private boolean logout() {
		logger.info("logging out from office365");
		if (this.loggedIn) {
			try {
				// click on element by className
				WebElement element = driver.findElement(By.className("o365cs-me-tile-nophoto-username-container"));
				if (element != null) {
					element.click();
				}
				// click on logout button
				DriverUtils.clickOnElementByID(driver, "O365_SubLink_ShellSignout");
				driver.manage().deleteAllCookies();
			} catch (Exception e) {
				logger.error("could not log out from office365", e);
				return false;
			}
		}
		this.loggedIn = false;
		return true;
	}
	
	private boolean rename(){
		if (login(false)) {
			try {
				logger.info("rename file");
				// open oneDrive
				goToOneDrive();
				//click on file row
				DriverUtils.clickOnElementByTagNameAndAttribute(driver, "span", "aria-label", "Microsoft", null);
				DriverUtils.sleep(500);
				//click on delete icon in top bar
				DriverUtils.clickOnElementByTagNameAndAttribute(driver, "span", "class", "commandText", "Rename");				
				DriverUtils.sleep(500);
				//write the renamed name to the file
				WebElement renameTextBox = driver.findElement(By.id("ItemNameEditor-input"));
				//generate random string for renaming
				SecureRandom random = new SecureRandom();
				String newName = new BigInteger(130, random).toString(32);
				//clear the oldName from the textbox
				renameTextBox.clear();
				//write the new name
				renameTextBox.sendKeys(newName);
				DriverUtils.sleep(300);
				//click on rename button in dialog box
				DriverUtils.clickOnElementByTagNameAndAttribute(driver, "span", "class", "ms-Button-label", "Save");
			} catch (Exception e) {
				logger.error("could not rename file");
				return false;
			}
		}
		return true;
	}
	
	/***
	 * download file from the list( the first one)
	 * @return
	 */
	private boolean download() {
		if (login(false)) {
			try {
				logger.info("download file");
				// open oneDrive
				goToOneDrive();
				//click on file row
				DriverUtils.clickOnElementByTagNameAndAttribute(driver, "span", "aria-label", "Microsoft", null);
				DriverUtils.sleep(500);
				//click on delete icon in top bar
				DriverUtils.clickOnElementByTagNameAndAttribute(driver, "span", "class", "commandText", "Download");
				DriverUtils.sleep(2000);
			} catch (Exception e) {
				logger.error("could not upload file");
				return false;
			}
		}
		return true;
	}

	/* I AM HERE - NOT WORKING */
	private boolean upload() {
		if (login(false)) {
			try {
				logger.info("upload file");
				// open oneDrive
				goToOneDrive();
				DriverUtils.clickOnElementByTagNameAndAttribute(driver, "span", "class", "commandText", "Upload");
				DriverUtils.sleep(250);
				//DriverUtils.clickOnElementByTagNameAndAttribute(driver, "span", "class", "commandText", "Files");
				List<WebElement> elementList = driver.findElements(By.tagName("div"));
				String myElement = null;
				String myElement2 = null;
				for (WebElement element : elementList){
					try{
						myElement = element.getAttribute("class");
						myElement2 = element.getAttribute("aria-label");
						
					}catch(Exception e){
						continue;
					}
					if(myElement != null && myElement2 != null){
						//check if the a tag is on word
						if(myElement.contains("od-ContextualMenu-itemContainer") && myElement2.contains("Files")){
							if(element.getText().equals("Files")){
								element.click();
								break;
							}
							
						}
					}
				}
				
				DriverUtils.sleep(2000);
			} catch (Exception e) {
				logger.error("could not upload file");
				return false;
			}
		}
		return true;
	}

	/***
	 * delete action, can delete or file or folder
	 * @param type
	 * @return
	 */
	public boolean delete(String type) {
		if (login(false)) {
			try {
				// open oneDrive
				goToOneDrive();
				if(type.equals("file")){
					deleteFile();
				}else if(type.equals("folder")){
					deleteFolder();
				}
			} catch (Exception e) {
				logger.error("could not delete file/folder", e);
				return false;
			}
		}
		return true;
	}

	/***
	 * delete file (the first in the list)
	 */
	private void deleteFile(){
		logger.info("delete file");
		//click on file row
		DriverUtils.clickOnElementByTagNameAndAttribute(driver, "span", "aria-label", "Microsoft", null);
		clickDelete();

	}
	
	/***
	 * delete folder (the first in the list)
	 */
	private void deleteFolder(){
		logger.info("delete folder");
		//click on file row
		DriverUtils.clickOnElementByTagNameAndAttribute(driver, "span", "aria-label", "Folder", null);
		clickDelete();

	}
	
	/***
	 * press on delete icon and then on delete dialog box
	 */
	private void clickDelete(){
		DriverUtils.sleep(1000);
		//click on delete icon in top bar
		DriverUtils.clickOnElementByTagNameAndAttribute(driver, "span", "class", "commandText", "Delete");
		DriverUtils.sleep(1000);
		//click on delete button in dialog box
		DriverUtils.clickOnElementByTagNameAndAttribute(driver, "span", "class", "ms-Button-label", "Delete");
	}
	
	/***
	 * redirect to oneDrive
	 */
	private void goToOneDrive() {
		// open oneDrive
		driver.get("https://veridinet-my.sharepoint.com/_layouts/15/MySite.aspx?MySiteRedirect=AllDocuments");
		DriverUtils.sleep(8000);
	}
}
