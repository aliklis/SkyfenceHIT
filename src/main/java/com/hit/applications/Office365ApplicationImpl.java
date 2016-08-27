package com.hit.applications;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.hit.util.GetProperties;

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
			// TODO Auto-generated catch block
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
			sleep(3);
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
		
		getLastOpenedWindow();
		
		List<WebElement> aList = driver.findElements(By.tagName("a"));	
		for (WebElement a : aList){
			String link = a.getAttribute("ng-href");
			if(link != null){
				//check if the a tag is on word
				if(link.equals("https://office.live.com/start/Word.aspx?auth=2")){
					a.click();
					break;
				}
			}
		}
		
		sleep(2);
		getLastOpenedWindow();
			
		//click on feedback from help menu
		WebElement wordTemplate = driver.findElement(By.id("template_TM00002003"));	
		if(wordTemplate != null){
			wordTemplate.click();
		}
		
	}
	
	//2)
	/***
	 * send feedback
	 * @throws InterruptedException 
	 */
	private void SendFeedBack(){
		//click on the help menu
		WebElement helpButton = driver.findElement(By.id("O365_MainLink_Help"));	
		if(helpButton != null){
			helpButton.click();
		}
		//click on feedback from help menu
		WebElement feedBackButton = driver.findElement(By.id("O365_SubLink_ShellFeedback"));	
		if(feedBackButton != null){
			feedBackButton.click();
		}
		
		getLastOpenedWindow();
		
		//insert comment in feedback
		WebElement textAreaFeedBack = driver.findElement(By.id("txtFeedbackComment"));	
		if(textAreaFeedBack != null){
			textAreaFeedBack.sendKeys("nice service, thank you!!!");;
		}
		//send feedback
		WebElement textAreaFeedBackSubmit = driver.findElement(By.id("btnFeedbackSubmit"));	
		if(textAreaFeedBackSubmit != null){
			textAreaFeedBackSubmit.click();
		}
		
		//sleep for a second, let the feedback to be sent
		sleep(1);
		//close feedBack windows
		WebElement textAreaFeedBackClose = driver.findElement(By.id("btnFeedbackClose"));	
		if(textAreaFeedBackClose != null){
			textAreaFeedBackClose.click();
		}
						
	}
	
	
	
	
	/***
	 * get the last opened window
	 */
	private void getLastOpenedWindow(){
		//get the last opened window
		for(String handle : driver.getWindowHandles()) {
		    driver.switchTo().window(handle);
		}
	}
	
	/***
	 * sleep
	 * @param seconds
	 */
	private void sleep(int seconds){
		try {
			TimeUnit.SECONDS.sleep(seconds);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
