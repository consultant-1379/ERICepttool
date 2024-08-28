package com.ericsson.ept.connection;

import com.ericsson.ept.Configuration;
import com.ericsson.ept.EPTParameters;

public class BISConnection {

	private Configuration config;
	private Authentication authentication;
	private Request request;
	private String CMS_Name;
	
	public BISConnection(Configuration config) throws Exception{
		this.config = config;
		authentication = new Authentication();
		getLogonToken();
	}
	
	public void getLogonToken() throws Exception{
		CMS_Name = config.getParameter(EPTParameters.CMSNAME);
		
		authentication.setCMSServerURL(CMS_Name);
		authentication.setCMSUser(config.getParameter(EPTParameters.CMSUSER));
		authentication.setCMSPassword(config.getParameter(EPTParameters.CMSPASSWD));
		authentication.setCMSAuthType(config.getParameter(EPTParameters.CMSAUTHTYPE));
		
		authentication.logon();
		
		request = new Request(authentication.getLogonToken());
		
	}
	
//	public void logoff() throws Exception{
//		authentication.logoff();
//	}
	
	public String sendRequest(String url, String method, String xmlContent, String accept){
		try {
			url = authentication.getBipURL() + url;
			request.send(url, method, xmlContent, accept);
			return request.getResponseContent();
			
		} catch (Exception e) {
			System.err.println("Exception while sending request. " + e.getMessage());
		}
		
		return "";
	}
	
	
}
