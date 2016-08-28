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
		case "LOGOUT":
			logout();
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
		
		//go to last opened window in the web
		getLastOpenedWindow();
		
		//click on an element by tagname
		clickOnElementByTagNameAndAttribute("a", "ng-href", "https://office.live.com/start/Word.aspx?auth=2");
		
		sleep(2);
		
		//go to last opened window in the web
		getLastOpenedWindow();
			
		//click on feedback from help menu
		clickOnElementByID("template_TM00002003");
		

		
	}
	
	//2)
	/***
	 * send feedback
	 * @throws InterruptedException 
	 */
	private void SendFeedBack(){
		
		//click on the help menu
		clickOnElementByID("O365_MainLink_Help");
		
		//click on feedback from help menu
		clickOnElementByID("O365_SubLink_ShellFeedback");
		
		//go to last opened window in the web
		getLastOpenedWindow();
		
		//insert comment in feedback
		writeToHTMLElement("txtFeedbackComment","nice service, thank you!!!");
	
		//send feedback
		clickOnElementByID("btnFeedbackSubmit");
		//sleep for a second, let the feedback to be sent
		sleep(1);
		//close feedBack windows
		clickOnElementByID("btnFeedbackClose");
						
	}
	
	
	
	//3)
	private void logout(){
		if (loggedIn) {
			// click on element by className
			WebElement element = driver.findElement(By.className("o365cs-me-tile-nophoto-username-container"));
			if (element != null) {
				element.click();
			}
			// click on logout button
			clickOnElementByID("O365_SubLink_ShellSignout");
		}
		setLoggedIn(false);
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

	/***
	 * click on an element in the web
	 * @param name
	 */
	private void clickOnElementByID(String name){
		WebElement element = driver.findElement(By.id(name));	
		if(element != null){
			element.click();
		}
	}
	
	/***
	 * write to an element(form/text/input) in the web
	 * @param name
	 * @param text
	 */
	private void writeToHTMLElement(String name, String text){
		
		WebElement element = driver.findElement(By.id(name));	
		if(element != null){
			element.sendKeys(text);
		}
	}

	/***
	 * click on an element by tagname with specified attribute and specified value
	 * @param tagName
	 * @param attributeName
	 * @param attributeValue
	 */
	private void clickOnElementByTagNameAndAttribute(String tagName, String attributeName, String attributeValue){
		List<WebElement> elementList = driver.findElements(By.tagName(tagName));	
		for (WebElement element : elementList){
			String myElement = element.getAttribute("ng-href");
			if(myElement != null){
				//check if the a tag is on word
				if(myElement.equals(attributeValue)){
					element.click();
					break;
				}
			}
		}
	}
}
