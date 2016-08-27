package com.hit.util;

import com.hit.applications.ApplicationRequest;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import java.io.File;

public class XMLToRequest {

	private XMLToRequest() {
	}

	public static ApplicationRequest parseXML(String fileName) {
		ApplicationRequest.Builder reqBuilder = null;
		ApplicationRequest req = null;
		
		try {
		// preparing the XML file for reading
		File fXmlFile = new File(fileName);
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(fXmlFile);
		
		// checking for application name
		//application = doc.getDocumentElement().getElementsByTagName("application");
		if(doc.getDocumentElement().getElementsByTagName("application").getLength() == 0)
			throw new NullPointerException("Application has not been set");
	
		// checking for action name
		//action = doc.getDocumentElement().getElementsByTagName("action");
		if(doc.getDocumentElement().getElementsByTagName("action").getLength() == 0)
			throw new NullPointerException("Action has not been set");
		
		// initializing a new request builder
		reqBuilder = new ApplicationRequest.Builder(
				doc.getDocumentElement().getElementsByTagName("application").item(0).getTextContent(),
				doc.getDocumentElement().getElementsByTagName("action").item(0).getTextContent()
				);
		
		// getting the parameters
		NodeList nList = doc.getElementsByTagName("parameters");
		
		for (int temp = 0; temp < nList.getLength(); temp++) {

			Node nNode = nList.item(temp);

			if (nNode.getNodeType() == Node.ELEMENT_NODE) {
				
				Element eElement = (Element) nNode;
				
				if(eElement.getElementsByTagName("username").getLength() != 0){
					reqBuilder.username
					(eElement.getElementsByTagName("username").item(0).getTextContent());
				}
				if(eElement.getElementsByTagName("password").getLength() != 0) {
					reqBuilder.password
					(eElement.getElementsByTagName("password").item(0).getTextContent());
				}
				if(eElement.getElementsByTagName("numOfRuns").getLength() != 0) {
					try{
						reqBuilder.numberOfRuns
						(Integer.parseInt(eElement.getElementsByTagName("numOfRuns").item(0).getTextContent()));
					}
					catch(NumberFormatException e) {
						LogMain.Log("Must enter a valid integer for number of runs", 2, "XMLToRequest.parseXML()");
					}
				}
				if(eElement.getElementsByTagName("useTor").getLength() != 0) {
					reqBuilder.useTor
					(Boolean.valueOf(eElement.getElementsByTagName("useTor").item(0).getTextContent()));
				}
				if(eElement.getElementsByTagName("proxyAddr").getLength() != 0) {
					reqBuilder.proxyAddr
					(eElement.getElementsByTagName("proxyAddr").item(0).getTextContent());
				}
			}
		}
		
		req = reqBuilder.build();
		
	    } catch (Exception e) {
	    	e.printStackTrace();
	    }
		
		return req;
}

}
