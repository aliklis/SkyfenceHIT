package main;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.hit.applications.ApplicationRequest;
import com.hit.scenarios.ScenarioManager;
import com.hit.util.*;

public class Main {

	public static void main(String[] args) {
		String directory = null;
		List<ApplicationRequest> reqList = new ArrayList<ApplicationRequest>();
		if (args.length > 0) {
			directory = args[0];
		} else {
			directory = GetProperties.getProp("defaultXmlDir");
		}
		
		File dir = new File(directory);
		if (dir.exists() && dir.isDirectory()) {
			File[] directoryListing = dir.listFiles();
			if (directoryListing != null) {
				for (File child : directoryListing) {
					reqList.add(XMLToRequest.parseXML(child.getPath()));
				}
			}
		}
		for(int i=0;i<reqList.size();i++){
			ScenarioManager.run(reqList.get(i));
		}
	}

}
