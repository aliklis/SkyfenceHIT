package com.hit.util;

import java.io.File;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;

public class DriverUtils {
	private static Logger logger = Logger.getLogger(DriverUtils.class);
	
	// used to initialize startup options for chrome driver and firefox driver
	static {
		File gecko = new File(GetProperties.getProp("geckoLocation"));
		System.setProperty("webdriver.gecko.driver", gecko.getAbsolutePath());

		File chromeDriver = new File(GetProperties.getProp("chromeDriverLocation"));
		System.setProperty("webdriver.chrome.driver", chromeDriver.getAbsolutePath());
	}
	
	/***
	 * the web driver 
	 * @param incognito
	 * @param proxyAddr
	 * @return
	 */
	public static WebDriver getDriver(boolean incognito, String proxyAddr) {
		try{
			WebDriver driver;
			DesiredCapabilities cap = new DesiredCapabilities();
			ChromeOptions options = new ChromeOptions();
			Proxy proxy = new Proxy();
			
			options.addArguments("--start-maximized");
			// set incognito mode
			if (incognito) {
				options.addArguments("-incognito");
			}
			cap.setCapability(ChromeOptions.CAPABILITY, options);
			// set proxy settings at browser level
			if (proxyAddr != null) {
				proxy.setHttpProxy(proxyAddr).setFtpProxy(proxyAddr).setSslProxy(proxyAddr);
				cap.setCapability(CapabilityType.PROXY, proxy);
			}
			
			// initialize the new driver with the required capabilities
			driver = new ChromeDriver(cap);
			
			// configure 10 seconds timeout 'wait' for elements to load (while searching them on page)
			driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
			return driver;
		}
		catch(Exception e){
			logger.error("getting driver", e);
		}
		return null;
	}
	
	/***
	 * get web driver with tor
	 * @param incognito
	 * @return
	 */
	public static WebDriver getDriverWithTor(boolean incognito){
		try{
			WebDriver driver;
			DesiredCapabilities cap = new DesiredCapabilities();
			ChromeOptions options = new ChromeOptions();
			
			TorBrowser.Start();
			
			String proxyAddr = GetProperties.getProp("torProxy");
			String proxyPort = GetProperties.getProp("torProxyPort");
	
		    options.addArguments("--proxy-server=socks5://" + proxyAddr + ":" + proxyPort);
			if (incognito) {
				options.addArguments("-incognito");
			}
			options.addArguments("--start-maximized");
			cap.setCapability(ChromeOptions.CAPABILITY, options);
			driver = new ChromeDriver(cap);
			driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
			
			return driver;
		}catch(Exception e){
			logger.error("getting driver with tor", e);
		}
		return null;
	}
	
	/***
	 * stop tor session
	 */
	public static void endTorSession(){
		try{
		TorBrowser.Stop();
		}catch(Exception e){
			logger.error("stopping to session");
		}
	}
	
	/***
	 * get the last opened window
	 */
	public static void getLastOpenedWindow(WebDriver driver){
		try{
			//get the last opened window
			for(String handle : driver.getWindowHandles()) {
			    driver.switchTo().window(handle);
			}
		}catch(Exception e){
			logger.error("Driver: " + driver);	
		}
	}
	
	/***
	 * sleep
	 * @param milliseconds
	 */
	public static void sleep(int milliseconds){
		try {
			TimeUnit.MILLISECONDS.sleep(milliseconds);
		} catch (InterruptedException e) {
			logger.error("sleeping", e);		
		}
	}

	/***
	 * click on an element in the web
	 * @param name
	 */
	public static void clickOnElementByID(WebDriver driver, String name){
		try{
			WebElement element = driver.findElement(By.id(name));	
			if(element != null){
				element.click();
			}
		}catch(Exception e){
			logger.error("Driver: " + driver +" name: " + name);
		}
	}
	
	/***
	 * write to an element(form/text/input) in the web
	 * @param name
	 * @param text
	 */
	public static void writeToHTMLElement(WebDriver driver, String name, String text){
		try{
		WebElement element = driver.findElement(By.id(name));	
		if(element != null){
			element.sendKeys(text);
		}
		}catch(Exception e){
			logger.error("Driver: " + driver +" name: " + name + " text: " + text, e);
		}
	}

	/***
	 * click on an element by tagname with specified attribute and specified value
	 * @param tagName
	 * @param attributeName
	 * @param attributeValue
	 */
	public static void clickOnElementByTagNameAndAttribute(WebDriver driver, String tagName, String attributeName, String attributeValue, String text){
		try{
			List<WebElement> elementList = driver.findElements(By.tagName(tagName));
			String myElement = null;
			for (WebElement element : elementList){
				try{
					myElement = element.getAttribute(attributeName);
				}catch(Exception e){
					continue;
				}
				if(myElement != null){
					//check if the a tag is on word
					if(myElement.contains(attributeValue)){
						if(text != null){
							if(element.getText().equals(text)){
								element.click();
								break;
							}
						}else{
							element.click();
							break;
						}
						
					}
				}
			}
		}catch(Exception e){
			logger.error("Driver: " + driver +" tagName: " + tagName + " attributeName: " + attributeName + " attributeValue: " + attributeValue, e);
		}
	}
	
}
