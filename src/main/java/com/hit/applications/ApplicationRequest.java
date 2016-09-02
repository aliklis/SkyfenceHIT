package com.hit.applications;

import com.hit.util.User;

public class ApplicationRequest {
	private String action;
	private User user;

	
	public ApplicationRequest(String action){
		this.action = action;
	}

	public String getAction() {
		return action.toUpperCase();
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
	
}
