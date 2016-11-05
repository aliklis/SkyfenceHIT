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
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.hit.util.GetProperties;

import com.hit.util.DriverUtils;

public class Office365ApplicationImpl extends AbstractApplication {
	private static Logger logger = Logger.getLogger(Office365ApplicationImpl.class);

	public Office365ApplicationImpl(WebDriver driver) {
		super(driver);
	}

	@Override
	public boolean doAction(ApplicationRequest applicationRequest) {
		logger.info("Application : Office365");

		if (applicationRequest == null) {
			logger.error("The application request object is not valid");
			throw new NullPointerException("The application request object is not valid");
		}
		if (driver == null) {
			logger.error("The driver object is not valid");
			throw new NullPointerException("The driver object is not valid");
		}

		this.applicationRequest = applicationRequest;
		logger.info("Action requested : " + applicationRequest.getAction());
		switch (applicationRequest.getAction()) {
		case "LOGIN":
			return login(true);
		case "MULTIPLE_ACTIONS":
			return MultipleActions();
		case "UPLOAD_FILES":
			return uploadFiles();
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
			return renameRandomFile();
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
		case "OPEN_MAILBOX":
			return openMailBox();
		case "OFFLINE_SETTINGS":
			return offlineSettings();
		case "LOGOUT":
			return logout();
		default:
			logger.error("The requested action is not available");
			throw new UnsupportedOperationException("The requested action is not available");
		}
	}

	/***
	 * Log in to office365
	 * 
	 * @param logoutAtEnd
	 * @return
	 */
	private boolean login(boolean logoutAtEnd) {
		logger.info("Trying to log in to office365");
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
			logger.error("Could not log in to office 365", e);
			return false;
		}

		this.loggedIn = true;
		if (logoutAtEnd)
			logout();
		return true;
	}

	/***
	 * Run multiple different actions
	 * 
	 * @return a boolean indicating that the run succeeded
	 */
	private boolean MultipleActions() {
		if (login(false)) {
			SendFeedBack();
			OpenWordTemplate();
		}
		return true;
	}

	/***
	 * Click on the word icon
	 * 
	 * @throws InterruptedException
	 */
	private void OpenWordTemplate() {
		logger.info("Opening word template");
		// go to last opened window in the web
		DriverUtils.getLastOpenedWindow(driver);

		// click on the element for word template
		DriverUtils.clickOnElementByTagNameAndAttribute(driver, "a", "ng-href",
				"https://office.live.com/start/Word.aspx?auth=2", null, -1, 1);

		DriverUtils.sleep(2000);

		// go to last opened window in the web
		DriverUtils.getLastOpenedWindow(driver);

		// click on feedback from help menu
		DriverUtils.clickOnElementByID(driver, "template_TM00002003", -1);

	}

	/***
	 * Send feedback
	 * 
	 * @throws InterruptedException
	 */
	private void SendFeedBack() {
		logger.info("Sending feedback");
		try {
			// click on the help menu
			DriverUtils.clickOnElementByID(driver, "O365_MainLink_Help", -1);

			// click on feedback from help menu
			DriverUtils.clickOnElementByID(driver, "O365_SubLink_ShellFeedback", -1);

			// go to last opened window in the web
			DriverUtils.getLastOpenedWindow(driver);

			// insert comment in feedback

			DriverUtils.writeToHTMLElement(driver, "txtFeedbackComment", "nice service, thank you!!!", -1);
			// send feedback
			DriverUtils.clickOnElementByID(driver, "btnFeedbackSubmit", -1);
			// sleep for a second, let the feedback to be sent
			DriverUtils.sleep(1000);
			// close feedBack windows
			DriverUtils.clickOnElementByID(driver, "btnFeedbackClose", -1);
		} catch (Exception e) {
			logger.error("Could not send feedback", e);
		}
	}

	/***
	 * Log out from office365
	 * 
	 * @return
	 */
	private boolean logout() {
		logger.info("Trying to log out from office365");
		if (this.loggedIn) {
			try {
				DriverUtils.clickOnElementByTagNameAndAttribute(driver, "button", "aria-label",
						"Use the down arrow to use the Me Control pane", null, -1, 1);
				DriverUtils.clickOnElementByID(driver, "O365_SubLink_ShellSignout", -1);
				driver.manage().deleteAllCookies();
			} catch (Exception e) {
				logger.error("Could not log out from office365", e);
				return false;
			}
		}
		this.loggedIn = false;
		return true;
	}

	/***
	 * Download the first file from the list in OneDrive
	 * 
	 * @return
	 */
	private boolean downloadFile() {
		if (login(false)) {
			try {
				logger.info("Trying to download the first file in OneDrive");
				// open OneDrive
				goToOneDrive();
				// gets the first file
				WebElement element = getFirstFileInOneDrive();
				if (element == null) {
					return false;
				}
				element.click();
				openActionOneDriveMenu("Download");
			} catch (Exception e) {
				logger.error("Could not download the first file in OneDrive", e);
				return false;
			}
		}
		// sleep during download time
		DriverUtils.sleep(Integer.parseInt(GetProperties.getProp("downloadTimeSleep")));
		return true;
	}

	/***
	 * download all the files from OneDrive
	 * 
	 * @return
	 */
	private boolean downloadAll() {
		if (login(false)) {
			try {
				logger.info("Trying to download all the files from OneDrive");
				// open OneDrive
				goToOneDrive();
				// Get a list of all the files
				List<WebElement> elementFiles = getFilesInOneDrive();
				if (elementFiles == null | elementFiles.size() == 0) {
					return false;
				}
				for (WebElement element : elementFiles) {
					// click on the file
					element.click();

					// click on download icon in top bar
					openActionOneDriveMenu("Download");
				}
			} catch (Exception e) {
				logger.error("Could not download all the files in OneDrive", e);
				return false;
			}
		}
		// sleep during download time
		DriverUtils.sleep(Integer.parseInt(GetProperties.getProp("downloadTimeSleep")));
		return true;
	}

	/***
	 * download a random file from the list
	 * 
	 * @return
	 */
	private boolean downloadRandomFile() {
		if (login(false)) {
			try {
				logger.info("Download a random file from OneDrive");
				// open OneDrive
				goToOneDrive();
				// get list of all the files
				List<WebElement> elementFiles = getFilesInOneDrive();
				if (elementFiles == null | elementFiles.size() == 0) {
					return false;
				}
				// Randomly choose a file to download from the list
				Random rand = new Random();
				int n = rand.nextInt(elementFiles.size());

				// Click on the file
				elementFiles.get(n).click();
				DriverUtils.sleep(200);
				// Click on download icon in top bar
				openActionOneDriveMenu("Download");
			} catch (Exception e) {
				logger.error("Could not download a random file", e);
				return false;
			}
		}
		// sleep during download time
		DriverUtils.sleep(Integer.parseInt(GetProperties.getProp("downloadTimeSleep")));
		return true;

	}

	/***
	 * Upload all files from a given folder path which is set in the properties
	 * file pay attention that files that have no content will be rejected by
	 * OneDrive
	 * 
	 * @return
	 */
	private boolean uploadFiles() {
		if (login(false)) {
			try {
				logger.info("Trying to upload files to OneDrive");
				logger.info("Pay attention that files that have no content will be rejected by OneDrive");

				// open OneDrive
				goToOneDrive();

				// get the directory path
				String filesDir = GetProperties.getProp("uploadFilesDir");

				// get list of all the file names
				List<String> fileNamesList = getListFilesNames(filesDir);

				// check if there are files in the folder
				if (fileNamesList.size() > 0) {
					// Upload the files
					for (int i = 0; i < fileNamesList.size(); i++) {
						WebDriverWait wait = new WebDriverWait(driver, 10);
						openActionOneDriveMenu("Upload");
						// a.click();
						WebElement element = wait.until(ExpectedConditions
								.elementToBeClickable(By.xpath("//input[@class='ContextualMenu-fileInput']")));
						element.sendKeys(fileNamesList.get(i));
						// TODO constant that will be "file wait upload"
						DriverUtils.sleep(2000);
					}
				} else {
					logger.warn("No files were found in the folder " + filesDir);
				}
			} catch (Exception e) {
				logger.error("Could not upload files to OneDrive", e);
				return false;
			}
		}
		return true;
	}

	/***
	 * Delete action, can delete a file or a folder from OneDrive
	 * 
	 * @param type
	 *            - a file or a folder
	 * @return
	 */
	public boolean delete(String type) {
		if (login(false)) {
			logger.info("Trying to delete " + type);
			try {
				// open OneDrive
				goToOneDrive();
				if (type.equals("file")) {
					if (deleteFile()) {
						return true;
					} else {
						return false;
					}
				} else if (type.equals("folder")) {
					if (deleteFolder()) {
						return true;
					} else {
						return false;
					}
				}
			} catch (Exception e) {
				logger.error("Could not delete " + type, e);
				return false;
			}
		}
		return true;
	}

	/***
	 * Delete the first file in OneDrive
	 */
	private boolean deleteFile() {
		// Click on file row
		WebElement firstFileElement = getFirstFileInOneDrive();
		if (firstFileElement == null) {
			logger.warn("No files were found in OneDrive");
			return false;
		}
		firstFileElement.click();
		clickDelete();
		return true;
	}

	/***
	 * Delete the first folder in OneDrive
	 */
	private boolean deleteFolder() {
		// Click on file row
		DriverUtils.clickOnElementByTagNameAndAttribute(driver, "span", "aria-label", "Folder", null, -1, 1);
		clickDelete();
		return true;
	}

	/***
	 * Renames the first file in OneDrive
	 * 
	 * @return
	 */
	private boolean renameFile() {
		if (login(false)) {
			try {
				logger.info("Trying to rename the first file in OneDrive");
				// open oneDrive
				goToOneDrive();

				WebElement firstFileElement = getFirstFileInOneDrive();
				if (firstFileElement == null) {
					return false;
				}
				firstFileElement.click();
				// click on rename icon in top bar
				openActionOneDriveMenu("Rename");

				// generate random string for renaming
				SecureRandom random = new SecureRandom();
				String newName = new BigInteger(130, random).toString(32);

				// write the renamed name to the file

				DriverUtils.writeToHTMLElement(driver, "ItemNameEditor-input", newName, -1);

				// click on the save button in dialog box
				DriverUtils.clickOnElementByTagNameAndAttribute(driver, "span", "class", "ms-Button-label", "Save", -1,
						1);
			} catch (Exception e) {
				logger.error("Could not rename the first file in OneDrive", e);
				return false;
			}
		}
		return true;
	}

	/***
	 * Renames all the files in OneDrive Because every rename causes the HTML
	 * document to change - first getting all the file names and then finding
	 * each one and changing its name
	 * 
	 * @return
	 */
	private boolean renameAll() {
		if (login(false)) {
			try {
				logger.info("Trying to rename all the files in OneDrive");
				// open oneDrive
				goToOneDrive();

				// get a list of all file names
				List<WebElement> elements = driver.findElements(By.xpath(
						"(//span[@class='DetailsRow-cell name']/child::a[not(contains(@aria-label,'Folder'))])"));
				if (elements == null || elements.size() == 0) {
					logger.warn("No files were found in OneDrive");
					return false;
				}

				WebElement clickableElement = null;
				SecureRandom random = new SecureRandom();
				String newName;

				List<String> fileNames = new ArrayList<String>();

				for (WebElement fileNameElement : elements) {
					fileNames.add(fileNameElement.getText());
				}
				for (String fileName : fileNames) {
					clickableElement = getFileElement(fileName);
					clickableElement.click();
					openActionOneDriveMenu("Rename");
					newName = new BigInteger(130, random).toString(32);
					// write the renamed name to the file
					DriverUtils.writeToHTMLElement(driver, "ItemNameEditor-input", newName, -1);

					// click on the save button in dialog box
					DriverUtils.clickOnElementByTagNameAndAttribute(driver, "span", "class", "ms-Button-label", "Save",
							-1, 1);
					DriverUtils.sleep(2000);
				}
			} catch (Exception e) {
				logger.error("Could not rename all the files in OneDrive", e);
				return false;
			}
		}
		return true;
	}

	/***
	 * Renames a random file in OneDrive
	 * 
	 * @return
	 */
	private boolean renameRandomFile() {
		if (login(false)) {
			try {
				logger.info("Trying to rename a random file in OneDrive");
				// open oneDrive
				goToOneDrive();

				List<WebElement> elementFiles = getFilesInOneDrive();

				if (elementFiles == null | elementFiles.size() == 0) {
					return false;
				}
				// Randomly choose a file to download from the list
				Random rand = new Random();
				int n = rand.nextInt(elementFiles.size());

				// Click on the file
				elementFiles.get(n).click();

				DriverUtils.sleep(200);

				openActionOneDriveMenu("Rename");
				SecureRandom random = new SecureRandom();
				String newName = new BigInteger(130, random).toString(32);

				// write the renamed name to the file
				DriverUtils.writeToHTMLElement(driver, "ItemNameEditor-input", newName, -1);

				// click on the save button in dialog box
				DriverUtils.clickOnElementByTagNameAndAttribute(driver, "span", "class", "ms-Button-label", "Save", -1,
						1);
				DriverUtils.sleep(2000);
			} catch (Exception e) {
				logger.error("Could not rename a random file in OneDrive", e);
				return false;
			}
		}
		return true;
	}

	/***
	 * Creates a folder in OneDrive
	 * 
	 * @return
	 */
	private boolean createFolder() {
		if (login(false)) {
			try {
				logger.info("Trying to create a folder in OneDrive");
				// open oneDrive
				goToOneDrive();

				WebDriverWait wait = new WebDriverWait(driver, 10);
				openActionOneDriveMenu("New");
				WebElement element = wait.until(ExpectedConditions.elementToBeClickable(By
						.xpath("//span[contains(@class,'ContextualMenu-commandText') and contains(text(),'Folder')]")));
				
				element.click();
				WebElement createFolderTextBox = driver.findElement(By.className("od-FolderBuilder-nameInput"));
				SecureRandom random = new SecureRandom();
				String folderName = new BigInteger(130, random).toString(32);
				createFolderTextBox.sendKeys(folderName);
				DriverUtils.sleep(300);
				
				// click on the create button
				DriverUtils.clickOnElementByTagNameAndAttribute(driver, "span", "class", "ms-Button-label", "Create",
						-1, 1);
			} catch (Exception e) {
				logger.error("Could not create a folder in OneDrive", e);
				return false;
			}
		}
		return true;
	}

	/***
	 * Move x files to folder (X is based on a configuration parameter)
	 * 
	 * @return
	 */
	private boolean moveXFilesToFolder() {
		if (login(false)) {
			try {
				logger.info("Trying to move files to folder in OneDrive");

				// open oneDrive
				goToOneDrive();
				// closeTeachingBubbleDiv();
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
						if (myElement.contains("DetailsRow-cell name")) {
							WebElement aElement = element.findElements(By.tagName("a")).get(0);
							String myElementAttribute = null;
							try {
								myElementAttribute = aElement.getAttribute("aria-label");
							} catch (Exception e) {
								continue;
							}
							if (myElementAttribute != null) {
								// check if the a tag is on word
								if (!myElementAttribute.contains("Folder")) {
									fileNames.add(aElement.getText());
								}
							}
						}
					}
				}

				int numberOfFiles = 0;
				// get number of files to move to folder from config file
				int numberOfFileToMove = Integer.parseInt(GetProperties.getProp("numberOfFilesToMove"));
				// if the input of files to move bigger then the files you can
				// move i take until the number of files, else i take the x
				// files
				if (numberOfFileToMove < fileNames.size()) {
					numberOfFiles = numberOfFileToMove;
				} else {
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
								// closeTeachingBubbleDiv();
								// click on move to icon in top bar
								openActionOneDriveMenu("Move to");
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
											DriverUtils.clickOnElementByTagNameAndAttribute(driver, "span", "class",
													"ms-Button-label", "Move here", -1, 1);
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
				logger.error("Could not move files to folder in OneDrive", e);
				return false;
			}
		}
		return true;
	}

	/***
	 * Empty OneDrive recycle bin
	 * 
	 * @return
	 */
	private boolean emptyRecycleBin() {
		if (login(false)) {
			try {
				logger.info("Trying to empty OneDrive recycle bin");
				// open oneDrive
				goToOneDrive();

				DriverUtils.clickOnElementByTagNameAndAttribute(driver, "span", "class", "LeftNav-linkText",
						"Recycle bin", -1, 1);
				// DriverUtils.sleep(1000);
				DriverUtils.clickOnElementByTagNameAndAttribute(driver, "span", "class", "CommandBarItem-commandText",
						"Empty recycle bin", -1, 1);
				// DriverUtils.sleep(1000);
				DriverUtils.clickOnElementByTagNameAndAttribute(driver, "span", "class", "ms-Button-label", "Delete",
						-1, 1);
			} catch (Exception e) {
				logger.error("Could not empty OneDrive recycle bin", e);
				return false;
			}
		}
		return true;
	}

	/***
	 * Share the first file in OneDrive
	 * 
	 * @return
	 */
	private boolean shareFile() {
		if (login(false)) {
			try {
				logger.info("Share a file in OneDrive");
				// open oneDrive
				goToOneDrive();
				WebElement firstFileElement = getFirstFileInOneDrive();
				if (firstFileElement == null) {
					return false;
				}
				firstFileElement.click();
				openActionOneDriveMenu("Share");
				DriverUtils.writeToHTMLElement(driver, "PeoplePicker-textBox", GetProperties.getProp("shareAddress"),
						-1);

				// move mouse to center of screen
				Robot robot = new Robot();
				robot.mouseMove((int) Toolkit.getDefaultToolkit().getScreenSize().getWidth() / 2,
						(int) Toolkit.getDefaultToolkit().getScreenSize().getHeight() / 2);
				// click in the middle to enter the suggests mail
				robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
				DriverUtils.sleep(500);

				// share the folder
				DriverUtils.clickOnElementByTagNameAndAttribute(driver, "span", "class", "ms-Button-label", "Share", -1,
						1);
				return true;
			} catch (Exception e) {
				logger.error("Could not share a file in OneDrive", e);
				return false;
			}
		}
		return false;
	}

	/***
	 * Share a folder in OneDrive
	 * 
	 * @return
	 */
	private boolean shareFolder() {
		if (login(false)) {
			try {
				logger.info("Trying to share a folder in OneDrive");
				// open oneDrive
				goToOneDrive();

				DriverUtils.clickOnElementByTagNameAndAttribute(driver, "span", "aria-label", "Folder", null, -1, 1);
				DriverUtils.sleep(300);
				openActionOneDriveMenu("Share");
				DriverUtils.sleep(300);
				DriverUtils.writeToHTMLElement(driver, "PeoplePicker-textBox", GetProperties.getProp("shareAddress"),
						-1);
				DriverUtils.sleep(500);

				// move mouse to center of screen
				Robot robot = new Robot();
				robot.mouseMove((int) Toolkit.getDefaultToolkit().getScreenSize().getWidth() / 2,
						(int) Toolkit.getDefaultToolkit().getScreenSize().getHeight() / 2);
				// click in the middle to enter the suggests mail
				robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
				DriverUtils.sleep(500);
				// share the folder
				DriverUtils.clickOnElementByTagNameAndAttribute(driver, "span", "class", "ms-Button-label", "Share", -1,
						1);

			} catch (Exception e) {
				logger.error("Could not share a folder in OneDrive", e);
				return false;
			}
		}
		return true;
	}

	/***
	 * Export contacts of outlook
	 * 
	 * @return
	 */
	private boolean exportContacts() {
		if (login(false)) {
			try {
				logger.info("Trying to export contacts");
				// open people
				goToPeople();

				// click on the second manage button because the first one is
				// not visible
				DriverUtils.clickOnElementByTagNameAndAttribute(driver, "button", "title", "Manage", null, -1, 2);

				// click on the Export contacts
				DriverUtils.clickOnElementByTagNameAndAttribute(driver, "span", "aria-label", "Export contacts",
						"Export contacts", -1, 1);
				// click on the button to export the contacts
				DriverUtils.clickOnElementByTagNameAndAttribute(driver, "span", "class", "o365buttonLabel _fce_r",
						"Export", -1, 1);

				// sleep during download time
				DriverUtils.sleep(Integer.parseInt(GetProperties.getProp("downloadTimeSleep")));
			} catch (Exception e) {
				logger.error("Could not export contacts", e);
				return false;
			}
		}
		return true;
	}

	/***
	 * Open another mailbox
	 * 
	 * @return
	 */
	private boolean openMailBox() {
		if (login(false)) {
			try {
				logger.info("Trying to open another mailbox");
				// open people
				goToPeople();

				DriverUtils.sleep(12000);
				// click on the icon in the top right corner
				DriverUtils.clickOnElementByTagNameAndAttribute(driver, "div", "class", "ms-Icon--person", null, -1, 1);

				DriverUtils.sleep(3000);

				// click on the "Open another mailbox..." option
				DriverUtils.clickOnElementByTagNameAndAttribute(driver, "span", "aria-label", "Open another mailbox...",
						"Open another mailbox...", -1, 1);

				// get the mailbox address to open
				String mailBox = GetProperties.getProp("mailboxToOpen");

				// write the address to the text box
				WebElement element = DriverUtils.findElementByXPathExpression(driver,
						"//form[@class='_fp_3']/input[@role='textbox']", -1);

				element.sendKeys(mailBox);

				DriverUtils.sleep(2000);
				// click enter for the first choice
				element.sendKeys(Keys.ENTER);

				// finds and clicks the open button
				WebElement openButton = DriverUtils.findElementByXPathExpression(driver,
						"//span[contains(text(), 'Open')]/parent::button", -1);
				openButton.click();

				DriverUtils.sleep(5000);

			} catch (Exception e) {
				logger.error("Could not open another mailbox", e);
				return false;
			}
		}
		return true;
	}

	/***
	 * Change offline settings
	 * 
	 * @return
	 */
	private boolean offlineSettings() {
		if (login(false)) {
			try {
				logger.info("Trying to change offline settings");
				// open people
				goToPeople();
				// wait for page to completely load
				DriverUtils.sleep(Integer.parseInt(GetProperties.getProp("peoplePageLoad")));

				// click on the setting button
				DriverUtils.clickOnElementByID(driver, "O365_MainLink_Settings", -1);

				// click on the offline setting button
				DriverUtils.clickOnElementByID(driver, "offlinesettings_People", -1);

				// check the turn on offline access check box
				DriverUtils.clickOnElementByTagNameAndAttribute(driver, "div", "class", "_opc_N", null, -1, 1);
				(DriverUtils.findElementByXPathExpression(driver, "//div[@class='_opc_S']/div/button[@type='button']",
						-1)).click();

				// get the elements to click on
				WebElement element = DriverUtils.findElementByXPathExpression(driver,
						"//div[@class='conductorContent']/div[contains(@class,'_op_w3')]/div[@class='_op_x3']/div[@class='_opc_m']/div[@class='_opc_o']/button[@autoid='_opc_1']",
						-1);
				WebElement element2 = DriverUtils.findElementByXPathExpression(driver,
						"//div[@class='conductorContent']/div[contains(@class,'_op_w3')]/div[@class='_op_x3']/div[@class='_opc_m']/div[@class='_opc_o']/button[@autoid='_opc_2']",
						-1);
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
				logger.error("Could not change offline settings", e);
				return false;
			}
		}
		return true;
	}

	/**
	 * Utility functions
	 */

	/***
	 * Press on delete icon and then on delete dialog box
	 */
	private void clickDelete() {
		// click on delete icon in top bar
		openActionOneDriveMenu("Delete");

		// click on delete button in dialog box
		DriverUtils.clickOnElementByTagNameAndAttribute(driver, "span", "class", "ms-Button-label", "Delete", -1, 1);
	}

	/**
	 * Closes the "Discover" pop up if it exists
	 */
	private void closeTeachingBubbleDiv() {
		try {
			DriverUtils.clickOnElementByTagNameAndAttribute(driver, "span", "class", "od-TeachingBubble-closeButton",
					null, 2, 1);
		} catch (NoSuchElementException | TimeoutException e) {
			logger.warn(
					"Could not close discover popup, it might have been completely removed so this function is irrelevant");
		} catch (Exception e) {
			logger.error(
					"Failed to close discover popup, it might have been completely removed so this function is irrelevant");
		}
	}

	/***
	 * Redirects to oneDrive
	 */
	private void goToOneDrive() {
		try {
			// if got here - expects to already be in login state in office365
			driver.get(GetProperties.getProp("office365Portal"));
			WebElement OneDriveForwardURLElem = driver.findElement(By.id("ShellDocuments_link"));
			String URL = OneDriveForwardURLElem.getAttribute("href");
			driver.get(URL);
			// DriverUtils.sleep(8000);
			closeTeachingBubbleDiv();
		} catch (Exception e) {
			logger.error("could not log into oneDrive", e);
		}
	}

	/**
	 * Redirects to people
	 */
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
	 * Get file list for uploading
	 * 
	 * @param filesDir
	 *            directory path
	 * @return the list of file names
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
			logger.error("Could not get the list of file names", e);
		}
		return null;
	}

	/**
	 * This function assumes that the driver is currently at OneDrive and looks
	 * for the files elements in it
	 * 
	 * @return the file elements in OneDrive
	 */
	private List<WebElement> getFilesInOneDrive() {
		try {
			List<WebElement> elementList = driver.findElements(By.tagName("div"));
			List<WebElement> elementFiles = new ArrayList<WebElement>();
			String elementClass = null;
			String elementLabel = null;
			for (WebElement element : elementList) {
				try {
					elementClass = element.getAttribute("class");
					elementLabel = element.getAttribute("aria-label");
				} catch (Exception e) {
					continue;
				}
				if (elementClass != null && elementLabel != null) {
					if (elementClass.contains("DetailsRow can-select") && !elementLabel.contains("Folder")) {
						elementFiles.add(element);
					}
				}
			}
			if (elementFiles.size() == 0) {
				logger.warn("No files were found in OneDrive");
				return null;
			}
			return elementFiles;
		} catch (Exception e) {
			logger.error("Could not retrieve list of file elements in OneDrive", e);
		}
		return null;
	}

	private WebElement getFileElement(String fileName) {
		try {
			WebDriverWait wait = new WebDriverWait(driver, 15);
			WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(
					"//div[contains(@class, 'DetailsRow can-select') and contains(@aria-label,'" + fileName + "')]")));
			return element;
		} catch (TimeoutException e) {
			logger.error("The file with name " + fileName + " was not found");
			throw e;
		}
	}

	/**
	 * This function assumes that the driver is currently at OneDrive
	 * 
	 * @return Returns the first file element in OneDrive
	 */
	private WebElement getFirstFileInOneDrive() {
		try {
			logger.info("Trying to retrieve the first file element in OneDrive");
			List<WebElement> elementList = driver.findElements(By.tagName("div"));
			String elementClass = null;
			String elementLabel = null;
			for (WebElement element : elementList) {
				try {
					elementClass = element.getAttribute("class");
					elementLabel = element.getAttribute("aria-label");
				} catch (Exception e) {
					continue;
				}
				if (elementClass != null && elementLabel != null) {
					if (elementClass.contains("DetailsRow can-select") && !elementLabel.contains("Folder")) {
						return element;
					}
				}
			}
		} catch (Exception e) {
			logger.error("Could not retrieve the first file element in OneDrive");
		}
		logger.warn("No files were found in OneDrive");
		return null;
	}

	/***
	 * Open an action from the menu bar in OneDrive will first try to open the
	 * action from the menu. it is not in the menu, it assumes it is in the 3
	 * dots (...) and tries to click on it there
	 */
	private void openActionOneDriveMenu(String actionName) {
		try {
			DriverUtils.clickOnElementByTagNameAndAttribute(driver, "span", "class", "commandText", actionName, 7, 1);
		} catch (TimeoutException | NoSuchElementException e) {
			// the element wasn't found so trying to open the three dots
			WebDriverWait wait = new WebDriverWait(driver, 10);
			DriverUtils.clickOnElementByTagNameAndAttribute(driver, "div", "title",
					"Other things you can do with the selected items", null, -1, 1);
			WebElement element = wait.until(ExpectedConditions.elementToBeClickable(
					By.xpath("//span[contains(@class,'ContextualMenu-commandText') and contains(text(),'" + actionName
							+ "')]")));
			element.click();
		}
	}
}