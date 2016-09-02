package com.hit.applications;

public interface IApplication {
	// Interface for an application
	// runs an action and returns true if the action succeeded
	public boolean doAction(ApplicationRequest applicationRequest);
}
