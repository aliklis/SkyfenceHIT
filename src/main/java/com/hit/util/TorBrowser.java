package com.hit.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import org.apache.log4j.Logger;
import org.openqa.selenium.firefox.FirefoxBinary;
import org.openqa.selenium.firefox.FirefoxProfile;

public final class TorBrowser {
	private static Logger logger = Logger.getLogger(TorBrowser.class);
	
	/***
	 * ctor
	 */
	private TorBrowser(){}
	
	/***
	 * start tor browser
	 */
	static void Start(){
		File torProfileDir = new File(
				GetProperties.getProp("torProfileDir"));
		FirefoxBinary binary = new FirefoxBinary(new File(
				GetProperties.getProp("torBinary")));
		FirefoxProfile torProfile = new FirefoxProfile(torProfileDir);
		torProfile.setPreference("webdriver.load.strategy", "unstable");
		try {
		    binary.startProfile(torProfile, torProfileDir, "");
		} catch (IOException e) {
			logger.error("failed starting tor",e);
		}
		// sleep for 10 seconds while tor browser is starting
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			logger.error("In command Thread.sleep",e);
		}
	}
	
	/***
	 * stop tor browser
	 */
	static void Stop(){
	    Runtime rt = Runtime.getRuntime();

	    try {
	        rt.exec("taskkill /F /IM firefox.exe");
	        while (processIsRunning("firefox.exe")) {
	            Thread.sleep(100);
	        }
	    } catch (Exception e) {
	    	logger.error("stoping browser",e);
	    }
	}
	
	/***
	 * run processes
	 * @param process
	 * @return
	 */
	private static boolean processIsRunning(String process) {
	    boolean processIsRunning = false;
	    String line;
	    try {
	        Process proc = Runtime.getRuntime().exec("wmic.exe");
	        BufferedReader input = new BufferedReader(new InputStreamReader(proc.getInputStream()));
	        OutputStreamWriter oStream = new OutputStreamWriter(proc.getOutputStream());
	        oStream.write("process where name='" + process + "'");
	        oStream.flush();
	        oStream.close();
	        while ((line = input.readLine()) != null) {
	            if (line.toLowerCase().contains("caption")) {
	                processIsRunning = true;
	                break;
	            }
	        }
	        input.close();
	    } catch (IOException e) {
	    	logger.error("running process",e);
	    }
	    return processIsRunning;
	}
}
