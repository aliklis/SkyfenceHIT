package com.hit.applications;

import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.InputEvent;
import java.io.File;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

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
		//case "LOGIN_ONE_DRIVE":
			//return loginOneDrive();
		case "MULTIPLE_ACTIONS":
			return MultipleActions();
		case "UPLOAD_FILE":
			return uploadFile();
		case "DOWNLOAD_FILE":
			return downloadFile();
		case "DOWNLOAD_RANDOM_FILE":
			return downloadRandomFile();
		case "DOWNLOAD_ALL":
			return downloadAll();
		case "DELETE_FILE":
			return delete("file");
		case "DELETE_FOLDER":
			return delete("folder");
		case "RENAME_FILE":
			return renameFile();
		case "RENAME_RANDOM_FILE":
			return renameRadnomFile();
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

	//david
	/***
	 * download file from the list( the first one)
	 * 
	 * @return
	 */
	private boolean downloadFile() {
		if (login(false)) {
			try {

				logger.info("download the first file");
				// open oneDrive
				goToOneDrive();
				// get list of all file names
				List<WebElement> elementList = driver.findElements(By.tagName("div"));
				List<WebElement> elementFiles = new ArrayList<WebElement>();
				String myElement = null;
				String myElement2 = null;
				for (WebElement element : elementList) {
					try {
						myElement = element.getAttribute("class");
						myElement2 = element.getAttribute("aria-label");
					} catch (Exception e) {
						continue;
					}
					if (myElement != null && myElement2 != null) {
						if (myElement.contains("DetailsRow can-select") && !myElement2.contains("Folder")) {
							element.click();
							open3Dots("Download");
							break;
						}
					}
				}
				
				DriverUtils.sleep(2000);
			} catch (Exception e) {
				logger.error("could not download a file", e);
				return false;
			}
		}
		return true;
	}

	//david
	/***
	 * download all the files from the list
	 * 
	 * @return
	 */
	private boolean downloadAll() {
		if (login(false)) {
			try {

				logger.info("download all the files");
				// open oneDrive
				goToOneDrive();
				// get list of all file names
				List<WebElement> elementList = driver.findElements(By.tagName("div"));
				String myElement = null;
				String myElement2 = null;
				for (WebElement element : elementList) {
					try {
						myElement = element.getAttribute("class");
						myElement2 = element.getAttribute("aria-label");
					} catch (Exception e) {
						continue;
					}
					if (myElement != null && myElement2 != null) {
						if (myElement.contains("DetailsRow can-select") && !myElement2.contains("Folder")) {
							try {
								// clicking on the file
								element.click();
								DriverUtils.sleep(200);
								// click on download icon in top bar
								open3Dots("Download");
								DriverUtils.sleep(2000);
							} catch (Exception e) {
								continue;
							}
						}
					}
				}
			} catch (Exception e) {
				logger.error("could not download all files", e);
				return false;
			}
		}
		DriverUtils.sleep(10000);
		return true;

	}

	//david
	/***
	 * download a random file from the list
	 * 
	 * @return
	 */
	private boolean downloadRandomFile() {
		if (login(false)) {
			try {

				logger.info("download a random file");
				// open oneDrive
				goToOneDrive();
				// get list of all file names
				List<WebElement> elementList = driver.findElements(By.tagName("div"));
				List<WebElement> elementFiles = new ArrayList<WebElement>();
				String myElement = null;
				String myElement2 = null;
				for (WebElement element : elementList) {
					try {
						myElement = element.getAttribute("class");
						myElement2 = element.getAttribute("aria-label");
					} catch (Exception e) {
						continue;
					}
					if (myElement != null && myElement2 != null) {
						if (myElement.contains("DetailsRow can-select") && !myElement2.contains("Folder")) {
							elementFiles.add(element);
						}
					}
				}
				Random rand = new Random();
				int n = rand.nextInt(elementFiles.size());
				// clicking on the file
				elementFiles.get(n).click();
				DriverUtils.sleep(200);
				// click on download icon in top bar
				open3Dots("Download");
			} catch (Exception e) {
				logger.error("could not download a random file", e);
				return false;
			}
		}
		DriverUtils.sleep(10000);
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

				// get directory of files
				String filesDir = GetProperties.getProp("uploadFilesDir");
				// get list of all the image names
				List<String> fileNamesList = getListFilesNames(filesDir);
				
				// check if there are files in the folder
				if (fileNamesList.size() > 0) {

					WebElement a = null;
					// Click on upload button label on top navigation
					List<WebElement> elementList = driver.findElements(By.tagName("span"));
					String myElement = null;
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
									a = element;
									break;
								}
							}
						}
					}
					for (int i = 0; i < fileNamesList.size(); i++) {
						WebDriverWait wait = new WebDriverWait(driver,10);
						a.click();
						WebElement element = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//input[@class='ContextualMenu-fileInput']")));
						element.sendKeys(fileNamesList.get(i));
						//TODO constant that will be "file wait upload"
						DriverUtils.sleep(2000);
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

	//david
	/***
	 * rename a file
	 * 
	 * @return
	 */
	private boolean renameFile() {
		if (login(false)) {
			try {

				logger.info("rename file");
				// open oneDrive
				goToOneDrive();

				// click on file row
				DriverUtils.clickOnElementByTagNameAndAttribute(driver, "span", "aria-label", "Microsoft", null);
				DriverUtils.sleep(200);
				// click on rename icon in top bar
				open3Dots("Rename");
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
				DriverUtils.sleep(2000);
			} catch (Exception e) {
				logger.error("could not rename file", e);
				return false;
			}
		}
		return true;
	}

	//david
	/***
	 * rename all files
	 * @return
	 */
	private boolean renameAll() {
		if (login(false)) {
			try {

				logger.info("rename all files");
				// open oneDrive
				goToOneDrive();

				List<String> fileNames = new ArrayList<String>();

				// get list of all file names
				List<WebElement> elementList = driver.findElements(By.tagName("span"));
				String myElement = null;
				for (WebElement element : elementList) {
					try {
						myElement = element.getAttribute("class");
					} catch (Exception e) {
						continue;
					}
					if (myElement != null) {
						// check if the a tag is on word
						if (myElement.contains("DetailsRow-cell name")){
							WebElement aElement = element.findElements(By.tagName("a")).get(0);
							String myElementAttribute = null;
							try{
								myElementAttribute = aElement.getAttribute("aria-label");
							} catch (Exception e) {
								continue;
							}
							if (myElementAttribute != null) {
								// check if the a tag is on word
								if (!myElementAttribute.contains("Folder")){
									fileNames.add(aElement.getText());
								}
							}
						}
					}
				}

				// Click on file to rename it
				for (String file : fileNames) {
					List<WebElement> myElementList = driver.findElements(By.tagName("div"));
					for (WebElement element : myElementList) {
						try {
							myElement = element.getAttribute("aria-label");
						} catch (Exception e) {
							continue;
						}
						if (myElement != null) {
							// check if the a tag is on word
							if (myElement.contains(file)) {

								// clicking on the file
								element.click();
								DriverUtils.sleep(200);
								// click on rename icon in top bar
								open3Dots("Rename");								
								// write the renamed name to the file
								WebElement renameTextBox = driver.findElement(By.id("ItemNameEditor-input"));
								// generate random string for renaming
								SecureRandom random = new SecureRandom();
								String newName = new BigInteger(130, random).toString(32);
								// clear the oldName from the textbox
								renameTextBox.clear();
								// write the new name
								renameTextBox.sendKeys(newName);
								// click on the save button
								DriverUtils.clickOnElementByTagNameAndAttribute(driver, "span", "class","ms-Button-label", "Save");
								DriverUtils.sleep(2000);
							}
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

	//david
	/***
	 * rename a random file
	 * @return
	 */
	private boolean renameRadnomFile() {
		if (login(false)) {
			try {

				logger.info("rename all files");
				// open oneDrive
				goToOneDrive();

				List<String> fileNames = new ArrayList<String>();

				// get list of all file names
				List<WebElement> elementList = driver.findElements(By.tagName("span"));
				String myElement = null;
				for (WebElement element : elementList) {
					try {
						myElement = element.getAttribute("class");
					} catch (Exception e) {
						continue;
					}
					if (myElement != null) {
						// check if the a tag is on word
						if (myElement.contains("DetailsRow-cell name")){
							WebElement aElement = element.findElements(By.tagName("a")).get(0);
							String myElementAttribute = null;
							try{
								myElementAttribute = aElement.getAttribute("aria-label");
							} catch (Exception e) {
								continue;
							}
							if (myElementAttribute != null) {
								// check if the a tag is on word
								if (!myElementAttribute.contains("Folder")){
									fileNames.add(aElement.getText());
								}
							}
						}
					}
				}
				
				
				Random rand = new Random();
				int n = rand.nextInt(fileNames.size());
				// clicking on the file
				String fileName = fileNames.get(n);

				List<WebElement> myElementList = driver.findElements(By.tagName("div"));
				for (WebElement element : myElementList) {
					try {
						myElement = element.getAttribute("aria-label");
					} catch (Exception e) {
						continue;
					}
					if (myElement != null) {
						// check if the a tag is on word
						if (myElement.contains(fileName)) {

							// clicking on the file
							element.click();
							DriverUtils.sleep(200);
							// click on rename icon in top bar
							open3Dots("Rename");
							// write the renamed name to the file
							WebElement renameTextBox = driver.findElement(By.id("ItemNameEditor-input"));
							// generate random string for renaming
							SecureRandom random = new SecureRandom();
							String newName = new BigInteger(130, random).toString(32);
							// clear the oldName from the textbox
							renameTextBox.clear();
							// write the new name
							renameTextBox.sendKeys(newName);
							// click on the save button
							DriverUtils.clickOnElementByTagNameAndAttribute(driver, "span", "class","ms-Button-label", "Save");
							DriverUtils.sleep(2000);
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
	
	//david
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
				
				
				List<String> fileNames = new ArrayList<String>();

				// get list of all file names
				List<WebElement> elementList = driver.findElements(By.tagName("span"));
				String myElement = null;
				for (WebElement element : elementList) {
					try {
						myElement = element.getAttribute("class");
					} catch (Exception e) {
						continue;
					}
					if (myElement != null) {
						// check if the a tag is on word
						if (myElement.contains("DetailsRow-cell name")){
							WebElement aElement = element.findElements(By.tagName("a")).get(0);
							String myElementAttribute = null;
							try{
								myElementAttribute = aElement.getAttribute("aria-label");
							} catch (Exception e) {
								continue;
							}
							if (myElementAttribute != null) {
								// check if the a tag is on word
								if (!myElementAttribute.contains("Folder")){
									fileNames.add(aElement.getText());
								}
							}
						}
					}
				}
				
				
				
				int numberOfFiles = 0;
				// get number of files to move to folder from config file
				int numberOfFileToMove = Integer.parseInt(GetProperties.getProp("numberOfFilesToMove"));
				//if the input of files to move bigger then the files you can move i take until the number of files, else i take the x files
				if(numberOfFileToMove < fileNames.size()){
					numberOfFiles = numberOfFileToMove;
				}
				else{
					numberOfFiles = fileNames.size();
				}
				// move each file
				for (int i = 0; i < numberOfFiles; i++) {
					List<WebElement> myElementList = driver.findElements(By.tagName("div"));
					for (WebElement element : myElementList) {
						try {
							myElement = element.getAttribute("aria-label");
						} catch (Exception e) {
							continue;
						}
						if (myElement != null) {
							// check if the a tag is on word
							if (myElement.contains(fileNames.get(i))) {
								element.click();
								DriverUtils.sleep(300);
								closeTeachingBubbleDiv();
								// click on move to icon in top bar
								open3Dots("Move to");
								DriverUtils.sleep(2000);
			
								// choose folder from the list
								List<WebElement> myElementList2 = driver.findElements(By.tagName("span"));
								String myElement2 = null;
								// click on the second element
								for (WebElement element2 : myElementList2) {
									try {
										myElement2 = element2.getAttribute("class");
									} catch (Exception e) {
										continue;
									}
									if (myElement2 != null) {
										// check if the a tag is on word
										if (myElement2.contains("od-FolderSelect-folderIcon")) {
												element2.click();
												DriverUtils.sleep(1000);
												// click on the move button
												DriverUtils.clickOnElementByTagNameAndAttribute(driver, "span", "class", "ms-Button-label", "Move here");
												DriverUtils.sleep(3000);
												break;
											}
										}
									}

								}
			

							}
						}
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

	//david
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
				open3Dots("Share");
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

	//david
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
				open3Dots("Share");
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
	 * 
	 * @return
	 */
	private boolean exportContacts() {
		if (login(false)) {
			try {

				logger.info("export contacts");
				// open people
				goToPeople();
				DriverUtils.sleep(15000);

				// run on all the buttons
				List<WebElement> elementList = driver.findElements(By.tagName("button"));
				String myElement = null;
				int counter = 0;
				for (WebElement element : elementList) {
					try {
						// get class and style attributes
						myElement = element.getAttribute("title");
					} catch (Exception e) {
						continue;
					}
					if (myElement != null) {
						// check if the title of the button is Manage
						// there is another manage on the page and that is why
						// we take the second one
						// which is the visible one and the clickable one
						if (myElement.contains("Manage")) {
							if (counter == 1) {
								// click the Manage text in top toolbar
								element.click();
								break;
							}
							counter++;
						}
					}
				}

				DriverUtils.sleep(2000);
				// click on the Export contacts
				DriverUtils.clickOnElementByTagNameAndAttribute(driver, "span", "aria-label", "Export contacts",
						"Export contacts");
				DriverUtils.sleep(3000);
				// click on the button to export the contacts
				DriverUtils.clickOnElementByTagNameAndAttribute(driver, "span", "class", "o365buttonLabel _fce_r",
						"Export");
				// wait for the file to finish download
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
	 * 
	 * @return
	 */
	private boolean openMailBox() {
		if (login(false)) {
			try {
				logger.info("open mail box");
				// open people
				goToPeople();
				DriverUtils.sleep(15000);

				// click on the icon in the top right corner
				DriverUtils.clickOnElementByTagNameAndAttribute(driver, "div", "class", "ms-Icon--person", null);
				DriverUtils.sleep(3000);

				// click on the "Open another mailbox..." option
				DriverUtils.clickOnElementByTagNameAndAttribute(driver, "span", "aria-label", "Open another mailbox...",
						"Open another mailbox...");

				// get the mailbox address to open
				String mailBox = GetProperties.getProp("mailboxToOpen");
				
				// write the address to the textbox
				WebElement element = driver.findElement(By.xpath("//form[@class='_fp_3']/input[@role='textbox']"));
				element.sendKeys(mailBox);
				DriverUtils.sleep(2000);
				
				// click enter for the first choice
				element.sendKeys(Keys.ENTER);
				DriverUtils.sleep(2000);
				
				// finds and clicks the open button
				WebElement openButton = driver.findElement(By.xpath("//span[contains(text(), 'Open')]/parent::button"));
				openButton.click();
				DriverUtils.sleep(2000);

			} catch (Exception e) {
				logger.error("could not open mail box", e);
				return false;
			}
		}
		return true;
	}

	/***
	 * turn offline settings
	 * 
	 * @return
	 */
	private boolean offlineSettings() {
		if (login(false)) {
			try {

				logger.info("offline settings");
				// open people
				driver.get(
						"https://outlook.office.com/owa/?realm=veridinet.com&exsvurl=1&ll-cc=1033&modurl=2&path=/people");
				DriverUtils.sleep(8000);

				// click on the setting button
				driver.findElement(By.id("O365_MainLink_Settings")).click();
				DriverUtils.sleep(2000);

				// click on the offline setting button
				driver.findElement(By.id("offlinesettings_People")).click();
				DriverUtils.sleep(2000);

				// check the Turn on offline access checkbox
				driver.findElement(By.xpath("//div[contains(@class,'_opc_N')]")).click();
				driver.findElement(By.xpath("//div[@class='_opc_S']/div/button[@type='button']")).click();
				DriverUtils.sleep(3000);

				// get the elements i need to click
				WebElement element = driver.findElement(By.xpath(
						"//div[@class='conductorContent']/div[contains(@class,'_op_w3')]/div[@class='_op_x3']/div[@class='_opc_m']/div[@class='_opc_o']/button[@autoid='_opc_1']"));
				WebElement element2 = driver.findElement(By.xpath(
						"//div[@class='conductorContent']/div[contains(@class,'_op_w3')]/div[@class='_op_x3']/div[@class='_opc_m']/div[@class='_opc_o']/button[@autoid='_opc_2']"));
				// click YES
				element.click();
				// click:
				// 1) NEXT
				// 2) NEXT
				// 3) OK
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

	//david
	/***
	 * press on delete icon and then on delete dialog box
	 */
	private void clickDelete() {
		DriverUtils.sleep(1000);
		// click on delete icon in top bar
		open3Dots("Delete");
		DriverUtils.sleep(1000);
		// click on delete button in dialog box
		DriverUtils.clickOnElementByTagNameAndAttribute(driver, "span", "class", "ms-Button-label", "Delete");
		DriverUtils.sleep(5000);
	}

	/**
	 * if there a div of discover it will take it off(in the future you may
	 * disable this function)
	 */
	private void closeTeachingBubbleDiv() {
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
//	private void goToOneDrive() {
//		// open oneDrive
//		driver.get("https://veridinet-my.sharepoint.com/_layouts/15/MySite.aspx?MySiteRedirect=AllDocuments");
//		DriverUtils.sleep(8000);
//		closeTeachingBubbleDiv();
//	}
	
	private void goToOneDrive() {
		try {
			// if got here - expects to already be in login state in office365
			driver.get(GetProperties.getProp("office365Portal"));
			WebElement OneDriveForwardURLElem = driver.findElement(By.id("ShellDocuments_link"));
			String URL = OneDriveForwardURLElem.getAttribute("href");
			driver.get(URL);
			DriverUtils.sleep(8000);
			closeTeachingBubbleDiv();
		} catch (Exception e) {
			logger.error("could not log into oneDrive", e);
		}
	}
	
	private void goToPeople() {
		try {
			// if got here - expects to already be in login state in office365
			driver.get(GetProperties.getProp("office365Portal"));
			WebElement PeopleForwardURLElem = driver.findElement(By.id("ShellPeople_link"));
			String URL = PeopleForwardURLElem.getAttribute("href");
			driver.get(URL);
		} catch (Exception e) {
			logger.error("could not log into people", e);
		}
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

	//david
	/***
	 * open the three dots if the action not find at the toolbar
	 */
	private void open3Dots(String text){
		boolean flag = false ;
		List<WebElement> elementList = driver.findElements(By.tagName("span"));
		String myElement = null;
		for (WebElement element : elementList){
			try{
				myElement = element.getAttribute("class");
			}catch(Exception e){
				continue;
			}
			if(myElement != null){
				//check if the a tag is on word
				if(myElement.contains("commandText")){
					if(element.getText().equals(text)){
						flag = true;
						element.click();
						break;
					}
				}
			}
		}
		if(!flag){
			DriverUtils.clickOnElementByTagNameAndAttribute(driver, "div", "title", "Other things you can do with the selected items", null);
			DriverUtils.clickOnElementByTagNameAndAttribute(driver, "span", "class", "commandText", text);
		}
	}
}
