package com.hit.tests;

import com.hit.scenarios.*;
import com.hit.util.*;
import com.hit.applications.*;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxBinary;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.Proxy;

@SuppressWarnings("unused")
public class TestScenario1 {
	public static void main(String[] args) {
		
		//LoginScenarioImpl login = new LoginScenarioImpl(5, false, null);
		//TORLoginScenarioImpl login = new TORLoginScenarioImpl(false, 5, false);
		// LogMain.Log("test15", 1, "TestScenario1");
		//for (int i = 0; i < 5; i++) {
			//login.run();
		//}
		//test1();
		ScenarioManager.run();
	}
	public static void test1() {
		WebDriver driver = DriverUtils.getDriverWithTor(true);
		driver.get("http://www.walla.co.il");
      try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}       
		driver.quit();
		DriverUtils.endTorSession();
	}
}
