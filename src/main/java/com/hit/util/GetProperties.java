package com.hit.util;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Properties;

public class GetProperties {
	private final static String propFileName = "config.properties";

	public static String getProp(String key) {
		String result = "";
		try (InputStream inputStream = GetProperties.class.getClassLoader().getResourceAsStream(propFileName)) {
			Properties prop = new Properties();

			if (inputStream != null) {
				prop.load(inputStream);
			} else {
				throw new FileNotFoundException("property file '" + propFileName + "' not found in the classpath");
			}
			
			result = prop.getProperty(key);
			
		} catch (Exception e) {
			System.out.println("Exception: " + e);
		}
		return result;
	}
}
