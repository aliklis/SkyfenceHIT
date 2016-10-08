package com.hit.applications;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.event.KeyEvent;

import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.hit.util.DriverUtils;
import com.hit.util.GetProperties;

public class DropBoxApplicationImpl extends AbstractApplication {
	private static Logger logger = Logger.getLogger(DropBoxApplicationImpl.class);
	
	/***
	 * ctor
	 */
	public DropBoxApplicationImpl(WebDriver driver) {
		super(driver);
	}
	
	/***
	 * override method from IApplication
	 */
	@Override
	public boolean doAction(ApplicationRequest applicationRequest) throws NullPointerException, UnsupportedOperationException {
		logger.info("Application: DropBox");
		
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
		case "UPLOAD":
			return upload();
		case "DOWNLOAD":
			return download();
		default:
			logger.error("the requested action is not available");
			throw new UnsupportedOperationException("the requested action is not available");
		}
	}
	
	/***
	 * login to DropBox
	 * @param logoutAtEnd
	 * @return
	 */
	private boolean login(boolean logoutAtEnd) {
		logger.info("logging to dropbox");
		if(this.loggedIn)
			return true;
		try{
			String dropboxLoginURL = GetProperties.getProp("dropboxLoginURL");
			String dropboxUserTextbox = GetProperties.getProp("dropboxUserTextbox");
			String dropboxPasswordTextbox = GetProperties.getProp("dropboxPasswordTextbox");
			boolean dropboxDoubleSubmit = Boolean.parseBoolean(GetProperties.getProp("dropboxDoubleSubmit"));
			driver.get(dropboxLoginURL);
			
			// submit user name
			//By byXpathUN = By.xpath("//input[(@name='login_email') and (@type = 'email')]");
			By byXpathUN = By.xpath("//input[(@name='"+dropboxUserTextbox+"') and (@type = 'email')]");
			WebElement username = driver.findElement(byXpathUN);
			username.sendKeys(applicationRequest.getUser().getUsername());
			
			if(dropboxDoubleSubmit){
				username.submit();
			}
			
			By byXpathPW = By.xpath("//input[(@name='"+dropboxPasswordTextbox+"') and (@type = 'password')]");
			WebElement password = driver.findElement(byXpathPW);
			// submit password
			password.sendKeys(applicationRequest.getUser().getPassword());
			password.submit();
		}catch(Exception e){
			logger.error("could not login to google", e);
		}
		
		try{
		Thread.sleep(3000);
		}
		catch(Exception ex){
			
		}
		
		this.loggedIn = true;
		if (logoutAtEnd)
			logout();
		return true;
	}
	
	/***
	 * upload file
	 * @return
	 */
	private boolean upload(){
		if(login(false)){
			try{
				logger.info("uploading file");
				int uploadTimes = Integer.parseInt(GetProperties.getProp("uploadTimes"));
				//click on upload button
				DriverUtils.clickOnElementByTagNameAndAttribute(driver, "img", "class", "s_web_upload_16");
				DriverUtils.getLastOpenedWindow(driver);
				DriverUtils.clickOnElementByTagNameAndAttribute(driver, "button", "class", "c-btn--primary");
	
				//stimulate copy to clipboard
				StringSelection str = new StringSelection("ArikChampion.png");
				Toolkit.getDefaultToolkit().getSystemClipboard().setContents(str, null);
				
				//create robot that perform human actions
				Robot robot = null;
				try {
					robot = new Robot();
				} catch (AWTException e) {
					logger.error("Initialization robot", e);
				}
				
				//run actions of robot
				try {
					doRobot(robot);
				} catch (InterruptedException e) {
					logger.error("robot action", e);
				}
				
				for (int i = 1; i < uploadTimes; i++) {
					DriverUtils.clickOnElementByTagNameAndAttribute(driver, "button", "class", "c-btn--secondary");
					try {
						doRobot(robot);
					} catch (InterruptedException e) {
						logger.error("robot action", e);
					}
				}
			}catch(Exception e){
				logger.error("uploading image", e);
			}
		}
		
		try{
			DriverUtils.clickOnElementByTagNameAndAttribute(driver, "button", "class", "c-btn--primary");
		}catch(Exception e){
			System.out.println(e);
		}
		


		return true;
	}
	
	/***
	 * trigger robot action
	 * @param robot
	 * @throws InterruptedException
	 */
	private void doRobot(Robot robot) throws InterruptedException{
		
		Thread.sleep(2000);

		//PASTE, ENTER
		robot.keyPress(KeyEvent.VK_CONTROL);
		robot.keyPress(KeyEvent.VK_V);
		robot.keyRelease(KeyEvent.VK_V);
		robot.keyRelease(KeyEvent.VK_CONTROL);
		robot.keyPress(KeyEvent.VK_ENTER);
		robot.keyRelease(KeyEvent.VK_ENTER);
		
		Thread.sleep(3000);
	}
	
	/***
	 * download file
	 * @return
	 */
	private boolean download(){
		
		if(login(false)){
			try{
				logger.info("downloading file");
				int downloadTimes = Integer.parseInt(GetProperties.getProp("downloadTimes"));
				//click on the first file in the list
				DriverUtils.clickOnElementByTagNameAndAttribute(driver, "li", "class", "o-grid--no-gutter");
				for (int i = 0; i < downloadTimes; i++) {
					try {
						Thread.sleep(3000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					//click on the download button
					DriverUtils.clickOnElementByTagNameAndAttribute(driver, "img", "class", "s_web_download");
				}
			}catch(Exception e){
				logger.info("could not upload file", e);
			}
		}
		return true;
		
	}
	
	/***
	 * logout from dropbox
	 * @return
	 */
	private boolean logout() {
		logger.info("logging out from dropbox");
		if (!this.loggedIn)
			return true;
		try{
			driver.get("https://www.dropbox.com/logout");
			driver.manage().deleteAllCookies();
		}
		catch(Exception e){
			logger.error("could not log out from dropbox", e);
		}
		this.loggedIn = false;
		return true;
	}

}
