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
	 * main function
	 * @param args
	 */
	public static void main(String[] args) {
		logger.info("starting...");
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
					//if not using proxy set proxy null
					if(currRequest.getProxies() == null){
						ScenarioManager.run(currRequest, null);
					}
					//run on list of proxies
					else{
						List<String> proxyList = currRequest.getProxies();
						for (String proxy : proxyList) {
							ScenarioManager.run(currRequest, proxy);
						}
					}
	
				}
			}
		}catch(Exception e){
			logger.error("finished with errors");
		}
		logger.info("finished successfully :-); :-) :-)");
	}

	/***
	 * iterate over the files list from given directory(only XML) if no files or
	 * get an error in process return null else return list of requests
	 * 
	 * @param directory
	 * @return List<Request>
	 */
	public static List<Request> getRequestList(String directory) {
		logger.info("Get scenarios xml files");
		List<Request> requestList = new ArrayList<Request>();
		File dir = new File(directory);
		if (dir.exists() && dir.isDirectory()) {
			File[] directoryListing = dir.listFiles();
			if (directoryListing != null) {
				for (File child : directoryListing) {
					String filename = child.getName();
					if (filename.endsWith(".xml") || filename.endsWith(".XML")) {
						try {
							Request request = getXMLRequest(child.getPath());
							requestList.add(request);
						} catch (JAXBException e) {
							logger.error("Getting scenario files",e);
							return null;
						}
					}
				}
				logger.info("finished getting scenario xml files");
				return requestList;
			}
		}
        
		return null;
	}

	/***
	 * read xml files into request object with JAXB
	 * 
	 * @param fileName
	 * @return
	 * @throws JAXBException
	 */
	public static Request getXMLRequest(String fileName) throws JAXBException {
		JAXBContext jc = JAXBContext.newInstance(Request.class);

		Unmarshaller unmarshaller = jc.createUnmarshaller();
		Request resquest = (Request) unmarshaller.unmarshal(new File(fileName));
		return resquest;

	}

}
