package com.hit.applications;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.event.KeyEvent;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.hit.util.DriverUtils;
import com.hit.util.GetProperties;

public class DropBoxApplicationImpl extends AbstractApplication {

	public DropBoxApplicationImpl(WebDriver driver) {
		super(driver);
	}
	
	@Override
	public boolean doAction(ApplicationRequest applicationRequest) throws NullPointerException, UnsupportedOperationException {
		if (applicationRequest == null) {
			throw new NullPointerException("the application request object is not valid");
		}
		if (driver == null) {
			throw new NullPointerException("the driver object is not valid");
		}
		this.applicationRequest = applicationRequest;
		
		switch (applicationRequest.getAction()) {
		case "LOGIN":
			return login(true);
		case "UPLOAD":
			return upload();
		case "DOWNLOAD":
			return download();
		default:
			throw new UnsupportedOperationException("the requested action is not available");
		}
	}
	
	
	private boolean login(boolean logoutAtEnd) {
		if(this.loggedIn)
			return true;
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
	
	private boolean upload(){
		if(login(false)){
			int uploadTimes = Integer.parseInt(GetProperties.getProp("uploadTimes"));
			//click on upload button
			DriverUtils.clickOnElementByTagNameAndAttribute(driver, "img", "class", "s_web_upload_16");
			DriverUtils.getLastOpenedWindow(driver);
			DriverUtils.clickOnElementByTagNameAndAttribute(driver, "button", "class", "c-btn--primary");

			//stimulate copy to clipboard
			StringSelection str = new StringSelection("SfalimDetails.png");
			Toolkit.getDefaultToolkit().getSystemClipboard().setContents(str, null);
			
			//create robot that perform human actions
			Robot robot = null;
			try {
				robot = new Robot();
			} catch (AWTException e) {
				e.printStackTrace();
			}
			
			//run actions of robot
			try {
				doRobot(robot);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			for (int i = 1; i < uploadTimes; i++) {
				DriverUtils.clickOnElementByTagNameAndAttribute(driver, "button", "class", "c-btn--secondary");
				try {
					doRobot(robot);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		return true;
	}
	
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
	
	private boolean download(){
		if(login(false)){
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
		}
		return true;
		
	}
	
	private boolean logout() {
		if (!this.loggedIn)
			return true;
		driver.get("https://www.dropbox.com/logout");
		driver.manage().deleteAllCookies();
		this.loggedIn = false;
		return true;
	}

}
