package com.ericsson.ept.test;

import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.ericsson.ept.Configuration;
import com.ericsson.ept.EPTParameters;
import com.ericsson.ept.connection.BISConnection;
import com.ericsson.ept.connection.CommonUtils;
import com.ericsson.ept.execution.ExecutionManager;
import com.ericsson.ept.execution.ExecutorThread;


public class TestSceduling implements ExecutorThread{

	private Configuration config;
	private String testspecname;
	private Configuration testspec;
	private ExecutionManager execmanager;
	private CommonUtils commonUtils;
	private TestUtilities testutils;
	
	public void init(Configuration config, String testspecname, Configuration testspec, ExecutionManager execmanager){
		this.config = config;
		this.testspecname = testspecname;
		this.testspec = testspec;
		this.execmanager = execmanager;
		this.commonUtils = new CommonUtils();
		this.testutils = new TestUtilities();
	}
	
	@Override
	public void run(){
		try{
			System.out.println("Running " + testspecname);
			BISConnection bis = new BISConnection(config);
			
			//Get the complete list of reports available on the BIS
			HashMap<String, String> availableReports = requestlist(bis, "0", "50"); 
			
			//Compare reports names against defined test specs
	
			String reportname = testspec.getParameter(EPTParameters.REPORTNAME);
			String matchType = testspec.getParameter(EPTParameters.MATCHTYPE);
			
			// Test each report against the criteria set in the test spec
			for (Map.Entry<String, String> innerentry : availableReports.entrySet()) {
			    String docid = innerentry.getKey();
			    String docname = innerentry.getValue();
			    
			    if (testutils.compare(docname, reportname, matchType)){
			    	//The report meets the criteria so add the test to the executor
			    	
			    	TestExecution test = new TestExecution();
			    	test.init(config, testspec, testspecname, docid, docname);
			    
			    	execmanager.addTestToExecution(test);
			    	
			    }
			    
			}
	
		}catch(Exception e){
			System.err.println("Error occurred while scheduling test for test spec " + testspecname);
			System.err.println(e.getMessage());
		}
		
	}
	
	
	private HashMap<String, String> requestlist(BISConnection bis, String offset, String limit) throws Exception{
		String listDocs = EPTParameters.LISTDOCUMENTS;
		listDocs = listDocs.replace("$offset", offset);
		listDocs = listDocs.replace("$limit", limit);
		
		String response = bis.sendRequest(listDocs, "GET", null, "application/xml");
		
		Document responseDoc = commonUtils.transformXmlStringToDocument(response);
		
		HashMap<String, String> results = testutils.parseResponse(responseDoc);
		if(results.size() == 50){
			int currentoffset = Integer.parseInt(offset) + results.size();
			results.putAll(requestlist(bis, String.valueOf(currentoffset), limit) );
		}
		
		return results;
	}
	
	
	
	
	
}
