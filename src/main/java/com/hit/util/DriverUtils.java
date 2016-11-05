package com.hit.util;

import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.NoSuchElementException;

public class DriverUtils {
	private static Logger logger = Logger.getLogger(DriverUtils.class);

	// used to initialize startup options for chrome web driver
	static {
		File chromeDriver = new File(GetProperties.getProp("chromeDriverLocation"));
		System.setProperty("webdriver.chrome.driver", chromeDriver.getAbsolutePath());
	}

	/***
	 * Initialize a new webdriver
	 * 
	 * @param incognito
	 *            if the driver is needed to be in incognito mode
	 * @param proxyAddr
	 *            proxy to be set
	 * @return
	 */
	public static WebDriver getDriver(boolean incognito, String proxyAddr) {
		try {
			logger.info("Trying to initialize a new webdriver");
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

			// configure 10 seconds timeout 'wait' for elements to load (while
			// searching them on page)
			driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
			return driver;
		} catch (Exception e) {
			logger.error("Failed to initialize a web driver", e);
		}
		return null;
	}

	/***
	 * Initialize a web driver with Tor
	 * 
	 * @param incognito
	 *            if needed to be in incognito mode
	 * @return
	 */
	public static WebDriver getDriverWithTor(boolean incognito) {
		try {
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
		} catch (Exception e) {
			logger.error("Failed to initialize a driver with Tor", e);
		}
		return null;
	}

	/***
	 * Stop Tor session
	 */
	public static void endTorSession() {
		try {
			TorBrowser.Stop();
		} catch (Exception e) {
			logger.error("Failed to stop Tor session");
		}
	}

	/***
	 * Get the last opened window
	 */
	public static void getLastOpenedWindow(WebDriver driver) {
		try {
			// get the last opened window
			for (String handle : driver.getWindowHandles()) {
				driver.switchTo().window(handle);
			}
		} catch (Exception e) {
			logger.error("Failed to get the last opened window", e);
		}
	}

	/***
	 * A sleep function
	 * 
	 * @param milliseconds
	 */
	public static void sleep(int milliseconds) {
		try {
			TimeUnit.MILLISECONDS.sleep(milliseconds);
		} catch (InterruptedException e) {
			logger.error("sleeping", e);
		}
	}

	/***
	 * Click on an element in the web
	 * 
	 * @param name
	 */
	public static void clickOnElementByID(WebDriver driver, String name) {
		try {
			WebElement element = driver.findElement(By.id(name));
			if (element != null) {
				element.click();
			}
		} catch (Exception e) {
			logger.error("Could not click on the element name: " + name, e);
		}
	}

	public static void clickOnElementByID2(WebDriver driver, String id, int optionalTimeOut) {
		logger.info("Trying to click on element with id " + id);
		int waitTimeOut;
		if(optionalTimeOut < 0)
			waitTimeOut = Integer.parseInt(GetProperties.getProp("webDriverWaitTimeOutPageLoad"));
		else
			waitTimeOut = optionalTimeOut;
		WebDriverWait wait = new WebDriverWait(driver, waitTimeOut);
		try {
			WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id(id)));
			element.click();
			logger.info("Successfully clicked on element with id " + id);
		} catch (NoSuchElementException e) {
			logger.error("Could not find the element with id : " + id, e);
			throw e;
		} catch (TimeoutException e) {
			logger.error("Could not find the element with id : " + id, e);
			throw e;
		}
	}

	/***
	 * Write to an element(form/text/input) in the web
	 * 
	 * @param name
	 * @param text
	 */
	public static void writeToHTMLElement(WebDriver driver, String name, String text) {
		try {
			WebElement element = driver.findElement(By.id(name));
			if (element != null) {
				element.sendKeys(text);
			}
		} catch (Exception e) {
			logger.error("Could not write to the element " + name, e);
		}
	}
	
	public static void writeToHTMLElement2(WebDriver driver, String id, String text, int optionalTimeOut) {
		logger.info("Trying to write to element with id " + id);
		try {
			WebElement element = findElementById(driver, id, optionalTimeOut);
			element.sendKeys(text);
		} catch (Exception e) {
			logger.error("Could not write to the element with id " + id, e);
		}
	}
	
	public static WebElement findElementById(WebDriver driver, String id, int optionalTimeOut) {
		logger.info("Trying to find element with id : " + id);
		int waitTimeOut;
		if(optionalTimeOut < 0)
			waitTimeOut = Integer.parseInt(GetProperties.getProp("webDriverWaitTimeOutPageLoad"));
		else
			waitTimeOut = optionalTimeOut;
		WebDriverWait wait = new WebDriverWait(driver, waitTimeOut);
		try {
			WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id(id)));
			logger.info("Successfully found element with id : " + id);
			return element;
		} catch (NoSuchElementException e) {
			logger.error("Could not find the element with id : " + id, e);
			throw e;
		} catch (TimeoutException e) {
			logger.error("Could not find the element with id : " + id, e);
			throw e;
		}
	}

	/***
	 * Click on an element by tagname with specified attribute and specified
	 * value
	 * 
	 * @param tagName
	 * @param attributeName
	 * @param attributeValue
	 */
	public static void clickOnElementByTagNameAndAttribute(WebDriver driver, String tagName, String attributeName,
			String attributeValue, String text) {
		logger.info("Trying to click on element with tag name " + tagName + " and attribute " + attributeName
				+ " and attribute value " + attributeValue + " and text " + text);
		boolean didFind = false;
		try {
			List<WebElement> elementList = driver.findElements(By.tagName(tagName));
			String myElement = null;
			for (WebElement element : elementList) {
				try {
					myElement = element.getAttribute(attributeName);
				} catch (Exception e) {
					continue;
				}
				if (myElement != null) {
					// check if the a tag is on word
					if (myElement.contains(attributeValue)) {
						if (text != null) {
							if (element.getText().equals(text)) {
								element.click();
								didFind = true;
								break;
							}
						} else {
							element.click();
							didFind = true;
							break;
						}
					}
				}
			}
			if (!didFind) {
				logger.warn("The element with tag name " + tagName + " and attributeName " + attributeName
						+ " and attributeValue " + attributeValue + " and text " + text + " was not found");
			}
		} catch (Exception e) {
			logger.error("Could not write to the element with tag name: " + tagName + " and attributeName: "
					+ attributeName + " and attributeValue: " + attributeValue + " and text " + text, e);
		}
	}

	public static void clickOnElementByTagNameAndAttribute2(WebDriver driver, String tagName, String attributeName,
			String attributeValue, String text, int optionalTimeOut) {
		int waitTimeOut;
		if(optionalTimeOut < 0)
			waitTimeOut = Integer.parseInt(GetProperties.getProp("webDriverWaitTimeOutPageLoad"));
		else
			waitTimeOut = optionalTimeOut;
		WebDriverWait wait = new WebDriverWait(driver, waitTimeOut);
		try {
			WebElement element = null;
			if (text == null) {
				element = wait.until(ExpectedConditions.visibilityOfElementLocated(
						By.xpath("//" + tagName + "[@" + attributeName + "='" + attributeValue + "']")));
			} else {
				element = wait.until(ExpectedConditions.visibilityOfElementLocated(
						By.xpath("//" + tagName + "[@" + attributeName + "='" + attributeValue + "' and contains(text(), '" + text + "')]")));
			}

			// if got here - element is not null, otherwise the 'until' will
			// throw a timeout exception
			
			element.click();
			// if (flag) {
			// logger.info("The element with tag name " + tagName + " and
			// attributeName " + attributeName
			// + " and attributeValue " + attributeValue + " and text " + text +
			// " has been clicked");
			// } else {
			// logger.info("The element with tag name " + tagName + " and
			// attributeName " + attributeName
			// + " and attributeValue " + attributeValue + " and text " + text +
			// " has not been clicked");
			// }
		} catch (NoSuchElementException e) {
			logger.error("The element with tag name " + tagName + " and attributeName " + attributeName
					+ " and attributeValue " + attributeValue + " and text " + text + " was not found");
			throw e;
		} catch (TimeoutException e) {
			logger.error("The element with tag name " + tagName + " and attributeName " + attributeName
					+ " and attributeValue " + attributeValue + " and text " + text + " was not found");
			throw e;
		}
	}

	/***
	 * Trigger a robot action
	 * 
	 * @param robot
	 * @throws InterruptedException
	 */
	public static void doRobot(Robot robot) throws InterruptedException {
		Thread.sleep(2000);
		// PASTE, ENTER
		robot.keyPress(KeyEvent.VK_CONTROL);
		robot.keyPress(KeyEvent.VK_V);
		robot.keyRelease(KeyEvent.VK_V);
		robot.keyRelease(KeyEvent.VK_CONTROL);
		robot.keyPress(KeyEvent.VK_ENTER);
		robot.keyRelease(KeyEvent.VK_ENTER);
		Thread.sleep(3000);
	}

}
