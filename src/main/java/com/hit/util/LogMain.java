package com.hit.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogMain {
	private static final Logger log = LoggerFactory.getLogger(LogMain.class.getName());

	public static void Log(String message, int state, String caller) {
		switch (state) {
		case 1:
			log.info(caller + " " + message);
			break;
		case 2:
			log.error(caller + " " + message);
			break;
		default:
			log.info(caller + " " + message);
		}
	}
}
