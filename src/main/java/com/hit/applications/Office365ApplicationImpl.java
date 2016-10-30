package com.hit.applications;

import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.event.InputEvent;
import java.io.File;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.Point;
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
		case "UPLOAD_FILE":
			return uploadFile();
//		case "UPLOAD_FOLDER":
//			return uploadFolder();
		case "DOWNLOAD":
			return download();
		case "DELETE_FILE":
			return delete("file");
		case "DELETE_FOLDER":
			return delete("folder");
		case "RENAME_FILE":
			return rename();
		case "RENAME_ALL":
			return renameAll();
		case "CREATE_FOLDER":
			return createFolder();
		case "MOVE_FILES_TO_FOLDER":
			return moveXFilesToFolder();
		case "EMPTY_RECYCLE_BIN":
			return emptyRecycleBin();
		case "SHARE_FILE":
			return shareFile();
		case "SHARE_FOLDER":
			return shareFolder();
		case "EXPORT_CONTACTS":
			return exportContacts();
		case "OPEN_MAIL_BOX":
			return openMailBox();
		case "OFFLINE_SETTINGS":
			return offlineSettings();
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
		DriverUtils.clickOnElementByTagNameAndAttribute(driver, "a", "ng-href",
				"https://office.live.com/start/Word.aspx?auth=2", null);

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

	/***
	 * download file from the list( the first one)
	 * 
	 * @return
	 */
	private boolean download() {
		if (login(false)) {
			try {
				logger.info("download file");
				// open oneDrive
				goToOneDrive();
				// click on file row
				DriverUtils.clickOnElementByTagNameAndAttribute(driver, "span", "aria-label", "Microsoft", null);
				DriverUtils.sleep(500);
				// click on delete icon in top bar
				DriverUtils.clickOnElementByTagNameAndAttribute(driver, "span", "class", "commandText", "Download");
			} catch (Exception e) {
				logger.error("could not upload file", e);
				return false;
			}
		}
		return true;
	}

	/***
	 * upload files
	 * 
	 * @return
	 */
	private boolean uploadFile() {
		if (login(false)) {
			try {
				logger.info("upload file");
				// open oneDrive
				goToOneDrive();

				// get directory of images
				String filesDir = GetProperties.getProp("uploadFilesDir");
				// get list of all the image names
				List<String> fileNamesList = getListFilesNames(filesDir);
				// check if there are images in the folder
				if (fileNamesList.size() > 0) {
					for (int i = 0; i < fileNamesList.size(); i++) {
						// Click On new button label on top navigation
						List<WebElement> elementList = driver.findElements(By.tagName("span"));
						String myElement = null;
						Point coordinates = null;
						for (WebElement element : elementList) {
							try {
								myElement = element.getAttribute("class");
							} catch (Exception e) {
								continue;
							}
							if (myElement != null) {
								// check if the a tag is on word
								if (myElement.contains("commandText")) {
									if (element.getText().equals("Upload")) {
										element.click();
										// get coordinates of the element
										coordinates = element.getLocation();
										break;
									}
								}
							}
						}
						// move mouse to the coordinates
						Robot robot = new Robot();
						robot.mouseMove(coordinates.getX(), coordinates.getY() + 120);
						
						DriverUtils.clickOnElementByTagNameAndAttribute(driver, "span", "class", "commandText","Files");

						StringSelection strSelection;
						// set the copy value as the next image name in the
						// order
						strSelection = new StringSelection(fileNamesList.get(i));
						Toolkit.getDefaultToolkit().getSystemClipboard().setContents(strSelection, null);

						try {
							// paste the value in the dialog box and press enter
							DriverUtils.doRobot(robot);
						} catch (InterruptedException e) {
							logger.error("robot action", e);
						}
						robot.mouseMove(150, 150);
						DriverUtils.sleep(300);
					}

				} else {
					logger.warn("no files in the folder: " + filesDir);
				}
			} catch (Exception e) {
				logger.error("could not upload file", e);
				return false;
			}
		}
		return true;
	}

	//NOT WORKING!
	/***
	 * upload folder
	 * @return
	 */
	private boolean uploadFolder() {
		if (login(false)) {
			try {
				logger.info("upload folder");
				// open oneDrive
				goToOneDrive();

				// get directory of images
				String filesDir = GetProperties.getProp("uploadFilesDir");
				// Click On new button label on top navigation
				List<WebElement> elementList = driver.findElements(By.tagName("span"));
				String myElement = null;
				Point coordinates = null;
				for (WebElement element : elementList) {
					try {
						myElement = element.getAttribute("class");
					} catch (Exception e) {
						continue;
					}
					if (myElement != null) {
						// check if the a tag is on word
						if (myElement.contains("commandText")) {
							if (element.getText().equals("Upload")) {
								element.click();
								// get coordinates of the element
								coordinates = element.getLocation();
								break;
							}
						}
					}
				}
				// move mouse to the coordinates
				Robot robot = new Robot();
				robot.mouseMove(coordinates.getX(), coordinates.getY() + 160);
				DriverUtils.clickOnElementByTagNameAndAttribute(driver, "div", "class", "commandText", "Folder");

				StringSelection strSelection;
				// set the copy value as the next image name in the order
				strSelection = new StringSelection(filesDir);
				Toolkit.getDefaultToolkit().getSystemClipboard().setContents(strSelection, null);

				try {
					// paste the value in the dialog box and press enter
					DriverUtils.doRobot(robot);
				} catch (InterruptedException e) {
					logger.error("robot action", e);
				}
			} catch (Exception e) {
				logger.error("could not upload folder", e);
				return false;
			}
		}
		return true;
	}

	/***
	 * delete action, can delete or file or folder
	 * 
	 * @param type
	 * @return
	 */
	public boolean delete(String type) {
		if (login(false)) {
			try {
				// open oneDrive
				goToOneDrive();
				if (type.equals("file")) {
					deleteFile();
				} else if (type.equals("folder")) {
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
	private void deleteFile() {
		logger.info("delete file");
		// click on file row
		DriverUtils.clickOnElementByTagNameAndAttribute(driver, "span", "aria-label", "Microsoft", null);
		clickDelete();

	}

	/***
	 * delete folder (the first in the list)
	 */
	private void deleteFolder() {
		logger.info("delete folder");
		// click on file row
		DriverUtils.clickOnElementByTagNameAndAttribute(driver, "span", "aria-label", "Folder", null);
		clickDelete();

	}

	/***
	 * rename a file
	 * 
	 * @return
	 */
	private boolean rename() {
		if (login(false)) {
			try {

				logger.info("rename file");
				// open oneDrive
				goToOneDrive();

				// click on file row
				DriverUtils.clickOnElementByTagNameAndAttribute(driver, "span", "aria-label", "Microsoft", null);
				DriverUtils.sleep(500);
				// click on rename icon in top bar
				DriverUtils.clickOnElementByTagNameAndAttribute(driver, "span", "class", "commandText", "Rename");
				DriverUtils.sleep(500);
				// write the renamed name to the file
				WebElement renameTextBox = driver.findElement(By.id("ItemNameEditor-input"));
				// generate random string for renaming
				SecureRandom random = new SecureRandom();
				String newName = new BigInteger(130, random).toString(32);
				// clear the oldName from the textbox
				renameTextBox.clear();
				// write the new name
				renameTextBox.sendKeys(newName);
				DriverUtils.sleep(300);
				// click on the save button in dialog box
				DriverUtils.clickOnElementByTagNameAndAttribute(driver, "span", "class", "ms-Button-label", "Save");
			} catch (Exception e) {
				logger.error("could not rename file", e);
				return false;
			}
		}
		return true;
	}

	//NOT WORKING
	/***
	 * rename all files in the list
	 * 
	 * @return
	 */
	private boolean renameAll() {
		if (login(false)) {
			try {

				logger.info("rename all files");
				// open oneDrive
				goToOneDrive();
				
				//Click on file to rename it
				List<WebElement> elementList = driver.findElements(By.tagName("span"));
				String myElement = null;
				for (WebElement element : elementList){
					try{
						myElement = element.getAttribute("aria-label");
					}catch(Exception e){}
					if(myElement != null){
						//check if the a tag is on word
						if(myElement.contains("Microsoft") || myElement.contains("Image")){

							//clicking on the file
							element.click();
							DriverUtils.sleep(500);
							// click on delete icon in top bar
							DriverUtils.clickOnElementByTagNameAndAttribute(driver, "span", "class", "commandText", "Rename");
							DriverUtils.sleep(500);
							// write the renamed name to the file
							WebElement renameTextBox = driver.findElement(By.id("ItemNameEditor-input"));
							// generate random string for renaming
							SecureRandom random = new SecureRandom();
							String newName = new BigInteger(130, random).toString(32);
							// clear the oldName from the textbox
							renameTextBox.clear();
							// write the new name
							renameTextBox.sendKeys(newName);
							DriverUtils.sleep(300);
							// click on the save button
							DriverUtils.clickOnElementByTagNameAndAttribute(driver, "span", "class", "ms-Button-label", "Save");
							DriverUtils.sleep(5000);
						}
					}
				}

			} catch (Exception e) {
				logger.error("could not rename all files", e);
				return false;
			}
		}
		return true;
	}
	
	
	/***
	 * create folder
	 * 
	 * @return
	 */
	private boolean createFolder() {
		if (login(false)) {
			try {

				logger.info("create folder");
				// open oneDrive
				goToOneDrive();

				// Click On new button label on top navigation
				List<WebElement> elementList = driver.findElements(By.tagName("span"));
				String myElement = null;
				Point coordinates = null;
				for (WebElement element : elementList) {
					try {
						myElement = element.getAttribute("class");
					} catch (Exception e) {
						continue;
					}
					if (myElement != null) {
						// check if the a tag is on word
						if (myElement.contains("commandText")) {
							if (element.getText().equals("New")) {
								element.click();
								// get coordinates of the element
								coordinates = element.getLocation();
								break;
							}
						}
					}
				}

				// move mouse to the coordinates
				Robot robot = new Robot();
				robot.mouseMove(coordinates.getX(), coordinates.getY() + 120);
				DriverUtils.sleep(50);
				// choose folder
				DriverUtils.clickOnElementByTagNameAndAttribute(driver, "span", "class", "commandText", "Folder");
				DriverUtils.sleep(1000);
				// enter the name of the new folder
				WebElement createFolderTextBox = driver.findElement(By.className("od-FolderBuilder-nameInput"));
				SecureRandom random = new SecureRandom();
				String folderName = new BigInteger(130, random).toString(32);
				createFolderTextBox.sendKeys(folderName);
				DriverUtils.sleep(300);
				// click on the create button
				DriverUtils.clickOnElementByTagNameAndAttribute(driver, "span", "class", "ms-Button-label", "Create");

			} catch (Exception e) {
				logger.error("could not create folder", e);
				return false;
			}
		}
		return true;
	}

	/***
	 * move x files to folder
	 * 
	 * @return
	 */
	private boolean moveXFilesToFolder() {
		if (login(false)) {
			try {

				logger.info("move files to folder");
				// open oneDrive
				goToOneDrive();
				// get number of files to move to folder from config file
				int numberOfFileToMove = Integer.parseInt(GetProperties.getProp("numberOfFilesToMove"));
				// move each file
				for (int i = 0; i < numberOfFileToMove; i++) {
					// click on file row
					DriverUtils.clickOnElementByTagNameAndAttribute(driver, "span", "aria-label", "Microsoft", null);
					DriverUtils.sleep(500);
					// click on delete icon in top bar
					DriverUtils.clickOnElementByTagNameAndAttribute(driver, "span", "class", "commandText", "Move to");
					DriverUtils.sleep(500);

					// choose folder from the list
					List<WebElement> elementList = driver.findElements(By.tagName("span"));
					String myElement = null;
					// click on the second element
					int count = 0;
					for (WebElement element : elementList) {
						try {
							myElement = element.getAttribute("class");
						} catch (Exception e) {
							continue;
						}
						if (myElement != null) {
							// check if the a tag is on word
							if (myElement.contains("od-FolderTree-folderName")) {
								if (count > 0) {
									element.click();
									break;
								}
								count++;
							}
						}
					}

					DriverUtils.sleep(500);
					// click on the move button
					DriverUtils.clickOnElementByTagNameAndAttribute(driver, "span", "class", "commandText", "Move");
					DriverUtils.sleep(3000);
				}

			} catch (Exception e) {
				logger.error("could not move files to folder", e);
				return false;
			}
		}
		return true;
	}

	/***
	 * empty recycle bin
	 * 
	 * @return
	 */
	private boolean emptyRecycleBin() {
		if (login(false)) {
			try {

				logger.info("empty recycle bin");
				// open oneDrive
				goToOneDrive();

				DriverUtils.clickOnElementByTagNameAndAttribute(driver, "span", "class", "LeftNav-linkText",
						"Recycle bin");
				DriverUtils.sleep(1000);
				DriverUtils.clickOnElementByTagNameAndAttribute(driver, "span", "class", "CommandBarItem-commandText",
						"Empty recycle bin");
				DriverUtils.sleep(1000);
				DriverUtils.clickOnElementByTagNameAndAttribute(driver, "span", "class", "ms-Button-label", "Delete");
			} catch (Exception e) {
				logger.error("could not empty recycle bin", e);
				return false;
			}
		}
		return true;
	}
	
	/***
	 * share a file
	 * 
	 * @return
	 */
	private boolean shareFile() {
		if (login(false)) {
			try {

				logger.info("share file");
				// open oneDrive
				goToOneDrive();

				DriverUtils.clickOnElementByTagNameAndAttribute(driver, "span", "aria-label", "Microsoft", null);
				DriverUtils.sleep(300);
				DriverUtils.clickOnElementByTagNameAndAttribute(driver, "span", "class", "commandText", "Share");
				DriverUtils.sleep(300);
				DriverUtils.writeToHTMLElement(driver, "PeoplePicker-textBox", "alan@veridinet.com");
				DriverUtils.sleep(500);

				// move mouse to center of screen
				Robot robot = new Robot();
				robot.mouseMove((int) Toolkit.getDefaultToolkit().getScreenSize().getWidth() / 2,
						(int) Toolkit.getDefaultToolkit().getScreenSize().getHeight() / 2);
				// click in the middle to enter the suggests mail
				robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
				DriverUtils.sleep(500);
				// share the folder
				DriverUtils.clickOnElementByTagNameAndAttribute(driver, "span", "class", "ms-Button-label", "Share");				

			} catch (Exception e) {
				logger.error("could not share the file", e);
				return false;
			}
		}
		return true;
	}

	/***
	 * share a folder
	 * 
	 * @return
	 */
	private boolean shareFolder() {
		if (login(false)) {
			try {

				logger.info("share folder");
				// open oneDrive
				goToOneDrive();

				DriverUtils.clickOnElementByTagNameAndAttribute(driver, "span", "aria-label", "Folder", null);
				DriverUtils.sleep(300);
				DriverUtils.clickOnElementByTagNameAndAttribute(driver, "span", "class", "commandText", "Share");
				DriverUtils.sleep(300);
				DriverUtils.writeToHTMLElement(driver, "PeoplePicker-textBox", "alan@veridinet.com");
				DriverUtils.sleep(500);

				// move mouse to center of screen
				Robot robot = new Robot();
				robot.mouseMove((int) Toolkit.getDefaultToolkit().getScreenSize().getWidth() / 2,
						(int) Toolkit.getDefaultToolkit().getScreenSize().getHeight() / 2);
				// click in the middle to enter the suggests mail
				robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
				DriverUtils.sleep(500);
				// share the folder
				DriverUtils.clickOnElementByTagNameAndAttribute(driver, "span", "class", "ms-Button-label", "Share");


			} catch (Exception e) {
				logger.error("could not share the folder", e);
				return false;
			}
		}
		return true;
	}

	/***
	 * export contacts
	 * @return
	 */
	private boolean exportContacts(){
		if (login(false)) {
			try {

				logger.info("export contacts");
				// open people
				driver.get("https://outlook.office.com/owa/?realm=veridinet.com&exsvurl=1&ll-cc=1033&modurl=2&path=/people");
				DriverUtils.sleep(5000);
				
				//run on all the divs
				List<WebElement> elementList = driver.findElements(By.tagName("div"));
				String myElement = null;
				String myElement2 = null;
				int counter = 0;
				for (WebElement element : elementList){
					try{
						//get class and style attributes
						myElement = element.getAttribute("class");
						myElement2 = element.getAttribute("style");
					}catch(Exception e){
						continue;
					}
					if(myElement != null && myElement2 != null){
						//check if the class is _ph_u5 and the style contains inline;
						if(myElement.contains("_ph_u5") && myElement2.contains("inline;")){
							//if its the seconds element with the properties i click it
							if(counter == 2){
								//click the Manage text in top toolbar
								element.click();
								break;
							}
							counter++;
						}
					}
				}
				
				//click on the Export contacts
				DriverUtils.clickOnElementByTagNameAndAttribute(driver, "span", "aria-label", "Export contacts","Export contacts");
				DriverUtils.sleep(3000);
				//click on the button to export the contacts
				DriverUtils.clickOnElementByTagNameAndAttribute(driver, "span", "class", "o365buttonLabel _fce_r","Export");
				//wait for the file to finish download
				DriverUtils.sleep(5000);
			} catch (Exception e) {
				logger.error("could not export contacts", e);
				return false;
			}
		}
		return true;
	}
		
	/***
	 * open mail box
	 * @return
	 */
	private boolean openMailBox(){
		if (login(false)) {
			try {

				logger.info("open mail box");
				// open people
				driver.get("https://outlook.office.com/owa/?realm=veridinet.com&exsvurl=1&ll-cc=1033&modurl=2&path=/people");
				DriverUtils.sleep(5000);
							
				//click on the icon in the top right corner
				DriverUtils.clickOnElementByTagNameAndAttribute(driver, "div", "class", "ms-Icon--person", null);
				DriverUtils.sleep(3000);
				
				//click on the "Open another mailbox..." option
				DriverUtils.clickOnElementByTagNameAndAttribute(driver, "span", "aria-label", "Open another mailbox...","Open another mailbox...");
				
				// get the user to open the mail box
				String mailBox = GetProperties.getProp("mailbox");
				// write the user mail box to the textbox
				WebElement element = driver.findElement(By.xpath("//form[@class='_fp_3']/input[@role='textbox']"));
				Point elementPoint = element.getLocation();
				element.sendKeys(mailBox);
				DriverUtils.sleep(2000);

				// move mouse to the coordinates and click
				Robot robot = new Robot();
				robot.mouseMove(elementPoint.getX(), elementPoint.getY() + 50);
				robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
				DriverUtils.sleep(2000);
				
				//try to press on open button, if the user exist its gone open else nothing will happen
				driver.findElement(By.xpath("//button[@class='o365button']")).click();
				
			} catch (Exception e) {
				logger.error("could not open mail box", e);
				return false;
			}
		}
		return true;
	}
	
	/***
	 * turn offline settings
	 * @return
	 */
	private boolean offlineSettings(){
		if (login(false)) {
			try {

				logger.info("offline settings");
				// open people
				driver.get("https://outlook.office.com/owa/?realm=veridinet.com&exsvurl=1&ll-cc=1033&modurl=2&path=/people");
				DriverUtils.sleep(8000);
				
				//click on the setting button
				driver.findElement(By.id("O365_MainLink_Settings")).click();
				DriverUtils.sleep(2000);
				
				//click on the offline setting button
				driver.findElement(By.id("offlinesettings_People")).click();
				DriverUtils.sleep(2000);
				
				//check the Turn on offline access checkbox
				driver.findElement(By.xpath("//div[contains(@class,'_opc_N')]")).click();
				driver.findElement(By.xpath("//div[@class='_opc_S']/div/button[@type='button']")).click();
				DriverUtils.sleep(3000);
				
				//get the elements i need to click
				WebElement element = driver.findElement(By.xpath("//div[@class='conductorContent']/div[contains(@class,'_op_w3')]/div[@class='_op_x3']/div[@class='_opc_m']/div[@class='_opc_o']/button[@autoid='_opc_1']"));
				WebElement element2 = driver.findElement(By.xpath("//div[@class='conductorContent']/div[contains(@class,'_op_w3')]/div[@class='_op_x3']/div[@class='_opc_m']/div[@class='_opc_o']/button[@autoid='_opc_2']"));
				//click YES
				element.click();
				//click:
				//1) NEXT
				//2) NEXT
				//3) OK
				for (int i = 0; i < 3; i++) {
					DriverUtils.sleep(300);
					element2.click();
				}
				
			} catch (Exception e) {
				logger.error("could not change offline settings", e);
				return false;
			}
		}
		return true;
	}
	
	/***
	 * press on delete icon and then on delete dialog box
	 */
	private void clickDelete() {
		DriverUtils.sleep(1000);
		// click on delete icon in top bar
		DriverUtils.clickOnElementByTagNameAndAttribute(driver, "span", "class", "commandText", "Delete");
		DriverUtils.sleep(1000);
		// click on delete button in dialog box
		DriverUtils.clickOnElementByTagNameAndAttribute(driver, "span", "class", "ms-Button-label", "Delete");
		DriverUtils.sleep(5000);
	}

	/**
	 * if there a div of discover it will take it off(in the future you may
	 * disable this function)
	 */
	private void closeDiscoverDiv() {
		try {
			DriverUtils.clickOnElementByTagNameAndAttribute(driver, "span", "class", "od-TeachingBubble-closeButton",
					null);
		} catch (Exception e) {
			logger.error("In Closing discover div, maybe its not there anymore", e);
		}
	}

	/***
	 * redirect to oneDrive
	 */
	private void goToOneDrive() {
		// open oneDrive
		driver.get("https://veridinet-my.sharepoint.com/_layouts/15/MySite.aspx?MySiteRedirect=AllDocuments");
		DriverUtils.sleep(8000);
		closeDiscoverDiv();
	}

	/***
	 * get file list for uploading
	 * 
	 * @param filesDir
	 * @return
	 */
	private List<String> getListFilesNames(String filesDir) {
		try {

			List<String> fileNamesList = new ArrayList<String>();
			File dir = new File(filesDir);
			if (dir.exists() && dir.isDirectory()) {
				File[] directoryListing = dir.listFiles();
				if (directoryListing != null) {
					for (File child : directoryListing) {
						String filename = child.getName();
						fileNamesList.add(filesDir + filename);
					}
				}
			}
			return fileNamesList;
		} catch (Exception e) {
			logger.error("getting list of files names", e);
		}
		return null;
	}

}
