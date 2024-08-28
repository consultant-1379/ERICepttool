package com.ericsson.ept.test.report;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import com.ericsson.ept.Configuration;
import com.ericsson.ept.EPTParameters;

public class TestReport {

	private static TestReport obj;
	private static int instance;
	private String outputDir;
	private File report;
	
	private TestReport(Configuration config) throws IOException{
		obj = this;
		setup(config);
	}
	
	private void setup(Configuration config) throws IOException{
		outputDir = config.getParameter(EPTParameters.TESTREPORTPATH);
		report = new File(outputDir, "EPTReport.txt");
		if(!report.exists()){
			report.createNewFile();
			writeRow("Time;TestSpecName;DocumentName;RefreshTime;NoOfRowsRetrieved");
		}
	}
	
	public synchronized void writeRow(String line){
		FileWriter fw = null;
		try{
			fw = new FileWriter(report, true);
			fw.write(line+"\n");
			fw.flush();
			fw.close();
		}catch(Exception e){
			System.err.println("Error while writing row to the report");
			System.err.println(e.getMessage());
		}
		
	}
	
	public static TestReport getInstance(Configuration config) throws IOException{
		if(instance ==0){
			instance = 1;
			return new TestReport(config);
		}
		if(instance == 1){
			return obj;
		}
		
		return null;
	}
	
}
