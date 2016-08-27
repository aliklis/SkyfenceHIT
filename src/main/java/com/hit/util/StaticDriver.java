package com.hit.util;

import java.io.File;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;

public class StaticDriver {
	
	// used to initialize startup options for chrome driver and firefox driver
	static {
		File gecko = new File(GetProperties.getProp("geckoLocation"));
		System.setProperty("webdriver.gecko.driver", gecko.getAbsolutePath());

		File chromeDriver = new File(GetProperties.getProp("chromeDriverLocation"));
		System.setProperty("webdriver.chrome.driver", chromeDriver.getAbsolutePath());
	}
	
	public static WebDriver getDriver(boolean incognito, String proxyAddr) {
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
	
	public static WebDriver getDriverWithTor(boolean incognito){
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
	}
	
	public static void endTorSession(){
		TorBrowser.Stop();
	}
}
