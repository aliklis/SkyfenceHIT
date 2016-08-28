package com.hit.applications;

public class ApplicationRequest {
	private final String application;
	private final String username;
	private final String password;
	private final String action;
	private final int numberOfRuns;
	private final boolean useTor;
	private final boolean useIncognito;
	private final String proxyAddr;
	
	public static class Builder {
		
		// required parameter
		private final String action;
		private final String application;
		// optional parameters using default values
		private String username = null;
		private String password = null;
		private int numberOfRuns = 1;
		private boolean useTor = false;
		private boolean useIncognito = false;
		private String proxyAddr = null;
		
		public Builder(String application, String action) {
			this.application = application;
			this.action = action;
		}
		
		public Builder username(String val) {
			username = val;
			return this;
		}

		public Builder password(String val) {
			password = val;
			return this;
		}

		public Builder numberOfRuns(int val) {
			numberOfRuns = val;
			return this;
		}
		
		public Builder useTor(boolean val) {
			useTor = val;
			return this;
		}
		
		public Builder useIncognito(boolean val) {
			useIncognito = val;
			return this;
		}
		
		public Builder proxyAddr(String val) {
			proxyAddr = val;
			return this;
		}

		public ApplicationRequest build() {
			return new ApplicationRequest(this);
		}
	}
	
	private ApplicationRequest(Builder builder) {
		this.username = builder.username;
		this.password = builder.password;
		this.numberOfRuns = builder.numberOfRuns;
		this.action = builder.action;
		this.application = builder.application;
		this.useTor = builder.useTor;
		this.useIncognito = builder.useIncognito;
		this.proxyAddr = builder.proxyAddr;
	}
	
	public boolean getUseIncognito() {
		return useIncognito;
	}

	public String getUsername() {
		return username;
	}
	
	public String getPassword() {
		return password;
	}
	
	public int getNumberOfRuns() {
		return numberOfRuns;
	}
	
	public String getAction() {
		return action;
	}
	
	public String getApplication() {
		return application;
	}
	
	public boolean getUseTor() {
		return useTor;
	}
	
	public String getProxyAddr() {
		return proxyAddr;
	}
}
