package com.ericsson.ept.test;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.ericsson.ept.Configuration;
import com.ericsson.ept.EPTParameters;
import com.ericsson.ept.connection.BISConnection;
import com.ericsson.ept.connection.CommonUtils;
import com.ericsson.ept.execution.ExecutorThread;
import com.ericsson.ept.test.report.TestReport;
import com.ericsson.ept.test.report.ZIPFile;

public class TestExecution implements ExecutorThread{

	private Configuration config;
	private Configuration testspec;
	private String testspecname;
	private String docid;
	private String docname;
	private CommonUtils commonUtils;
	private TestUtilities testutils;
	private BISConnection bis;
	private Document requestTemplate;
	private String starttimeString;
	private TestReport report;
	
	public void init(Configuration config, Configuration testspec, String testspecname, String docid, String docname){
		this.config = config;
		this.testspec = testspec;
		this.testspecname = testspecname;
		this.docid = docid;
		this.docname = docname;
		this.commonUtils = new CommonUtils();
		this.testutils = new TestUtilities();
		setup();
	}
	
	public void setup(){
		try{
			this.bis = new BISConnection(config);
			this.report = TestReport.getInstance(config);
			
			//Get the parameters of the report
			String requestString = EPTParameters.DOCUMENTPARAMS;
			requestString = requestString.replace("$id", docid);
			
			//Get the document parameters
			String response = bis.sendRequest(requestString, "GET", null, "application/xml");
			Document responseDoc = commonUtils.transformXmlStringToDocument(response);
			
			//Parse the document parameters to get the available values
			HashMap<String, ArrayList<String>> promptValues = testutils.parsePromptValues(responseDoc);
			
			//Create the request template and remove the unnecessary XML structure
			requestTemplate = testutils.createRequestTemplate(responseDoc);
			
			//Find the prompt values from the testspec
			ArrayList<String> promptPropNames = findPromptValues();
			
			//Create the set of values for the request XML
			HashMap<String, ArrayList<String>> requestvalues = getRequestValues(promptValues, promptPropNames);
			
			//Populate the request XML with the values
			requestTemplate = testutils.populateRequestContent(requestTemplate, requestvalues);
		} catch (Exception e) {
			System.err.println("Test execution set up error for \"" + docname + "\" using test spec " + testspecname);
			System.err.println(e.getMessage());
			
		}
	}
	
	@Override
	public void run() {
		try {
			System.out.println("Test execution started for \"" + docname + "\" using test spec " + testspecname);
			
			String interval = testspec.getParameter(EPTParameters.REFRESHINTERVAL);
		    String hours = testspec.getParameter(EPTParameters.ENDTIMEHOUR);
		    String mins = testspec.getParameter(EPTParameters.ENDTIMEMINS);
		
			while(calculateRemainingTime(hours, mins) > 0){
				String requestString = EPTParameters.DOCUMENTPARAMS;
				requestString = requestString.replace("$id", docid);
				
				Calendar starttime = Calendar.getInstance();
				SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
				starttimeString = format.format(starttime.getTime());
				long starttimemillis = System.currentTimeMillis();
				
				String xmlContent = commonUtils.transformXmlDocumentToString(requestTemplate);
				String response = bis.sendRequest(requestString, "PUT", xmlContent, "application/xml");		
				
				if(response.contains("<error_code>")){
					throw new Exception(response);
				}
				long endtime = System.currentTimeMillis();
				
				String outputDir = config.getParameter(EPTParameters.TESTREPORTPATH);
				ZIPFile zip = new ZIPFile(outputDir, testspecname);
							

				//Get the list of dataproviders in the document
				requestString = EPTParameters.LISTDATAPROVIDERS;
				requestString = requestString.replace("$id", docid);
				response = bis.sendRequest(requestString, "GET", null, "application/xml");
				HashMap<String, String> results = testutils.parseResponse(commonUtils.transformXmlStringToDocument(response));
				
				long elapsedTime = TimeUnit.MILLISECONDS.toSeconds(endtime - starttimemillis);
				System.out.println(testspecname + "," + docname + "," + elapsedTime + "," + calculateDPRows(results, zip));
				report.writeRow(starttimeString+";"+testspecname + ";" + docname + ";" + elapsedTime + ";" + calculateDPRows(results, zip));
				
				
				//If the flag is set to dump the data, request the data from each report and add it to the zip file
				String configwriteData = config.getParameter(EPTParameters.WRITEDATA);
				String testspecwriteData = testspec.getParameter(EPTParameters.WRITEDATA);
				if(configwriteData.equalsIgnoreCase("TRUE") || testspecwriteData.equalsIgnoreCase("TRUE")){
					//Get the list of reports in the document
					requestString = EPTParameters.LISTREPORTS;
					requestString = requestString.replace("$id", docid);
					response = bis.sendRequest(requestString, "GET", null, "application/xml");
					results = testutils.parseResponse(commonUtils.transformXmlStringToDocument(response));
					dumpReportData(results, zip);
				
				}
				
				
				Thread.sleep(TimeUnit.MINUTES.toMillis(Integer.valueOf(interval)));
			}
		} catch (Exception e) {
			System.err.println("Test execution error for \"" + docname + "\" using test spec " + testspecname);
			System.err.println(e.getMessage());
			e.printStackTrace();
			
		}	
		System.out.println("Test execution completed for \"" + docname + "\" using test spec " + testspecname);
	}
	
	public int calculateDPRows(HashMap<String, String> dataprovidors, ZIPFile zip){
		int numberofrows = 0;
		for (Map.Entry<String, String> innerentry : dataprovidors.entrySet()) {
		    String id = innerentry.getKey();
		    String name = innerentry.getValue();
		    
		    String requestString = EPTParameters.DATAPROVIDER;
			requestString = requestString.replace("$id", docid);
			requestString = requestString.replace("$DP", id);
			String response = bis.sendRequest(requestString, "GET", null, "text/csv");
		    
			numberofrows = numberofrows + response.split("\n").length -1;
			
			String configwriteData = config.getParameter(EPTParameters.WRITEDATA);
			String testspecwriteData = testspec.getParameter(EPTParameters.WRITEDATA);
			if(configwriteData.equalsIgnoreCase("TRUE") || testspecwriteData.equalsIgnoreCase("TRUE")){
				zip.writeToZip(docname+"/"+starttimeString+"/dataprovidors", name, response);
			}
			
		}
		return numberofrows;
	}
	
	public void dumpReportData(HashMap<String, String> reports, ZIPFile zip){
		for (Map.Entry<String, String> innerentry : reports.entrySet()) {
		    String id = innerentry.getKey();
		    String name = innerentry.getValue();
		    
		    String requestString = EPTParameters.REPORTDATA;
			requestString = requestString.replace("$id", docid);
			requestString = requestString.replace("$reportid", id);
			String response = bis.sendRequest(requestString, "GET", null, "text/csv");
			
			if(response.contains(";")){
				zip.writeToZip(docname+"/"+starttimeString+"/reports", name, response);
			}

			
		}
	}
	
	
	public long calculateRemainingTime(String endhours, String endmins){
		Calendar cal = Calendar.getInstance();
		long now = cal.getTimeInMillis();
		
		cal.set(Calendar.HOUR_OF_DAY, Integer.valueOf(endhours));
		cal.set(Calendar.MINUTE, Integer.valueOf(endmins));
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		long delay = cal.getTimeInMillis() - now;
		
		return delay;
	}

	public ArrayList<String> findPromptValues(){
		ArrayList<String> promptPropNames = new ArrayList<String>();
		ArrayList<Object> testspecprops = testspec.getKeySet();
		for(Object propName : testspecprops){
			String propertyName = (String) propName;
			
			if(propertyName.endsWith(EPTParameters.PROMPTNAME)){
				promptPropNames.add(propertyName.replace(EPTParameters.PROMPTNAME, ""));
			}
			
		}
		return promptPropNames;
	}
	

	public HashMap<String, ArrayList<String>> getRequestValues(HashMap<String, ArrayList<String>> promptValues, ArrayList<String> promptPropNames){
		HashMap<String, ArrayList<String>> requestValues = new HashMap<String, ArrayList<String>>();
		
		for (Map.Entry<String, ArrayList<String>> innerentry : promptValues.entrySet()) {
		    String promptName = innerentry.getKey();
		    ArrayList<String> values = innerentry.getValue();
		    
		    for(String promptPropName: promptPropNames){
		    	String testpromptname = testspec.getParameter(promptPropName + EPTParameters.PROMPTNAME); 
		    	String promptnamematchType = testspec.getParameter(promptPropName + EPTParameters.PROMPTNAMEMATCHTYPE); 
		    	
		    	if(testutils.compare(promptName, testpromptname, promptnamematchType)){
		    		String prompt_value = testspec.getParameter(promptPropName + EPTParameters.PROMPTVALUE); 
		    		String prompt_value_type = testspec.getParameter(promptPropName + EPTParameters.PROMPTVALUETYPE); 

		    		requestValues.put(promptName, testutils.getRequestValues(values, prompt_value, prompt_value_type));
		    		
		    	}
		    	
		    }
		    
		}
		return requestValues;
		
		
	}
	

}
