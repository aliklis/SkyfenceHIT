package com.hit.util;

import java.util.List;

import javax.xml.bind.annotation.*;

@XmlRootElement(name = "request")
@XmlAccessorType(XmlAccessType.FIELD)
public class Request {

	@XmlElement(name = "application")
	private String application;

	@XmlElement(name = "action")
	private String action;

	@XmlElement(name = "numOfRuns")
	private int numOfRuns;

	@XmlElement(name = "useTor")
	private boolean useTor;

	@XmlElement(name = "useIncognito")
	private boolean useIncognito;

	@XmlElementWrapper(name = "users")
	@XmlElement(name = "user")
	private List<User> users;

	@XmlElementWrapper(name = "proxies")
	@XmlElement(name = "proxy")
	private List<String> proxies;

	public String getApplication() {
		return application;
	}

	public String getAction() {
		return action;
	}

	public int getNumOfRuns() {
		return numOfRuns;
	}

	public boolean isUseTor() {
		return useTor;
	}

	public boolean isUseIncognito() {
		return useIncognito;
	}

	public List<User> getUsers() {
		return users;
	}

	public List<String> getProxies() {
		return proxies;
	}

}