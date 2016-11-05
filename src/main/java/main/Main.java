package main;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.log4j.Logger;

import com.hit.scenarios.ScenarioManager;
import com.hit.util.*;

public class Main {
	private static Logger logger = Logger.getLogger(Main.class);
	
	/***
	 * Main entry point for the program
	 * @param args - the only argument to be taken is the directory name from which to read the XML files
	 */
	public static void main(String[] args) {
		logger.info("Main program is starting");
		try{
			String directory = null;
			List<Request> requestList = null;
	
			if (args.length > 0) {
				directory = args[0];
			} else {
				directory = GetProperties.getProp("defaultXmlDir");
			}
	        
			requestList = getRequestList(directory);
	
			if (requestList != null) {
				for (Request currRequest : requestList) {
					// if proxy is not requested then setting it to null
					if(currRequest.getProxies() == null){
						ScenarioManager.run(currRequest, null);
					}
					// iterate over the list of proxies and run the scenarios
					else{
						List<String> proxyList = currRequest.getProxies();
						for (String proxy : proxyList) {
							logger.info("This scenario will use the proxy " + proxy);
							ScenarioManager.run(currRequest, proxy);
						}
					}
	
				}
			}
		} catch(Exception e){ 
			logger.error("Main program finished with errors");
		}
		logger.info("Main program finished");
	}

	/***
	 * Iterate over the XML files in the given directory
	 * Returns null if couldn't read atleast one XML file
	 * @param directory path
	 * @return list of request objects
	 */
	public static List<Request> getRequestList(String directory) {
		logger.info("Started retrieval of scenarios XML files");
		List<Request> requestList = new ArrayList<Request>();
		File dir = new File(directory);
		if (dir.exists() && dir.isDirectory()) {
			File[] directoryListing = dir.listFiles();
			if (directoryListing != null) {
				for (File child : directoryListing) {
					String filename = child.getName();
					if (filename.endsWith(".xml") || filename.endsWith(".XML")) {
						try {
							logger.info("Trying to retrieve scenario of XML filename " + filename);
							Request request = getXMLRequest(child.getPath());
							requestList.add(request);
						} catch (JAXBException e) {
							logger.error("Retreiving scenario XML file for filename " + filename + " failed",e);
							return null;
						}
					}
				}
				logger.info("Retrieved all scenario xml files successfully");
				return requestList;
			}
		}
		return null;
	}

	/***
	 * Read XML files into request object with JAXB
	 * 
	 * @param fileName to be read
	 * @returns the request object that represents the XML file
	 * @throws JAXBException
	 */
	public static Request getXMLRequest(String fileName) throws JAXBException {
		JAXBContext jc = JAXBContext.newInstance(Request.class);

		Unmarshaller unmarshaller = jc.createUnmarshaller();
		Request resquest = (Request) unmarshaller.unmarshal(new File(fileName));
		return resquest;

	}

}
