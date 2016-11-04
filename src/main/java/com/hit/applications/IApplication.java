package com.hit.applications;

/**
 * Application interface
 */
public interface IApplication {

	/***
	 * doAction method determines the action to be ran and runs the requested method accordingly
	 */
	public boolean doAction(ApplicationRequest applicationRequest);
}
