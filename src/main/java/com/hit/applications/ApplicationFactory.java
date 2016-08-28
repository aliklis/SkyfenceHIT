package com.hit.applications;

public class ApplicationFactory {

	private ApplicationFactory() {
	}

	public static IApplication GetApplication(String appName) {
		if (appName == null) {
			return null;
		}

		switch (appName.toUpperCase()) {
		case "GOOGLE":
			return new GoogleApplicationImpl();
		case "OFFICE365":
			return new Office365ApplicationImpl();
		case "DROPBOX":
			return new DropBoxApplicationImpl();
		case "BOX":
			return new BoxApplicationImpl();
		}

		return null;
	}
}
