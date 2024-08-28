package com.ericsson.ept.connection;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.Document;

public class Authentication {
	
	private String CMSServerURL;

	private String CMSUser;

	private String CMSPassword;

    private String CMSAuthType;

	private String bipURL;

	private String logonToken;
    
    private CommonUtils commonUtils;
    
    public String getCMSServerURL() {
		return CMSServerURL;
	}

	public void setCMSServerURL(String CMSServerURL) {
		this.CMSServerURL = "http://" + CMSServerURL;
		
		setBipURL(getCMSServerURL() + "/biprws");
	}

	public String getCMSUser() {
		return CMSUser;
	}

	public void setCMSUser(String CMSUser) {
		this.CMSUser = CMSUser;
	}

	public String getCMSPassword() {
		return CMSPassword;
	}

	public void setCMSPassword(String CMSPassword) {
		this.CMSPassword = CMSPassword;
	}

	public String getCMSAuthType() {
		return CMSAuthType;
	}

	public void setCMSAuthType(String CMSAuthType) {
		this.CMSAuthType = CMSAuthType;
	}
	
	public String getBipURL() {
		return bipURL;
	}

	public void setBipURL(String bipURL) {
		this.bipURL = bipURL;
	}
	
	public String getLogonToken() {
		return logonToken;
	}

	public void setLogonToken(String logonToken) {
		this.logonToken = logonToken;
	}

	public Authentication() {
		
		this.setCMSServerURL("");
    	this.setCMSUser("");
    	this.setCMSPassword("");
    	this.setCMSAuthType("");
    	this.setBipURL("");
    	this.setLogonToken("");
    	
		commonUtils = new CommonUtils();
    }
    
    public Authentication(String CMSServerURL, String CMSUser, String CMSPassword, String CMSAuthType) {
    	
    	this.setCMSServerURL(CMSServerURL);
    	this.setCMSUser(CMSUser);
    	this.setCMSPassword(CMSPassword);
    	this.setCMSAuthType(CMSAuthType);
    	
    	commonUtils = new CommonUtils();
    }

	/**
     * Logs in to the CMS repository.
     * 
     * @throws Exception
     */
    public String logon() throws Exception {
        // Checks if a port has been set, otherwise default it to 6405.
        URL url = new URL(CMSServerURL);
        if (url.getPort() == -1) {
            throw new RuntimeException("The server port has not been correctly set.");
        }

        // Sends the logon request
        // This gets the basic logon message structure.
        Request request = new Request();
        request.send(bipURL + "/logon/long", "GET", null);

        // Sets logon information
        Map<String, String> map = new HashMap<String, String>();
        map.put("//attr[@name='userName']", CMSUser);
        map.put("//attr[@name='password']", CMSPassword);
        map.put("//attr[@name='auth']", CMSAuthType);
        
        String filledLogonResponse = commonUtils.fillXml(request.getResponseContent(), map);
      //  System.out.println("filled logon response  "+filledLogonResponse);
        // Posts logon information
        String trace = request.send(bipURL + "/logon/long", "POST", filledLogonResponse);
    // System.out.println("login trace "+trace);
        logonToken = request.getResponseHeaders().get("X-SAP-LogonToken").get(0);
        
        return trace;
    }
    
    /**
     * Logs off from the CMS.
     */
    public String logoff() throws Exception {
        String trace ="";
    	
    	if (logonToken != null) {
            Request request = new Request(logonToken);
            trace = request.send(bipURL + "/logoff", "POST", null);
            logonToken = "";
        }
    	
        return trace;
    }
    
    /**
     * Logs in to the CMS repository and create the request and response
     * XMLs and store them in the resourcesFolder/traceflows/ folder.
     * 
     * @throws Exception
     */
    public String logonCreateXMLs(String resourcesFolder) throws Exception {
        // Checks if a port has been set, otherwise default it to 6405.
        URL url = new URL(CMSServerURL);
        if (url.getPort() == -1) {
            throw new RuntimeException("The server port has not been correctly set.");
        }

        // Sends the logon request
        // This gets the basic logon message structure.
        Request request = new Request();
        request.send(bipURL + "/logon/long", "GET", null);

        // Sets logon information
        Map<String, String> map = new HashMap<String, String>();
        map.put("//attr[@name='userName']", CMSUser);
        map.put("//attr[@name='password']", CMSPassword);
        map.put("//attr[@name='auth']", CMSAuthType);
        
        String filledLogonResponse = commonUtils.fillXml(request.getResponseContent(), map);
        // Posts logon information
        String trace = request.send(bipURL + "/logon/long", "POST", filledLogonResponse);
        		    		        
        // Retrieve the request.
 		String requestContent =  request.getRequestContent();
	    
	    if (requestContent != null) {
	    	
	    	// Transform the request String to a Document.
	    	Document requestDoc = commonUtils.transformXmlStringToDocument(requestContent);
		    
		    // Create the request message as a file.
		    commonUtils.createFileFromDocument(requestDoc, resourcesFolder + "traceflows/", "logon_request.xml");
	    }
        	    
	    // Retrieve the response.
	    String responseContent = request.getResponseContent();
	    
	    // Transform the response String to a Document.
	    Document responseDoc = commonUtils.transformXmlStringToDocument(responseContent);
	    
	    // Create the response message as a file.
	    commonUtils.createFileFromDocument(responseDoc, resourcesFolder + "traceflows/", "logon_response.xml");
        	    
        logonToken = request.getResponseHeaders().get("X-SAP-LogonToken").get(0);
        
        return trace;
    }
}

