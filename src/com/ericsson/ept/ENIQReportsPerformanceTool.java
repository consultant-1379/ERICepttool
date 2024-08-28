package com.ericsson.ept;

import java.io.File;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.ericsson.ept.execution.ExecutionManager;
import com.ericsson.ept.test.TestExecution;
import com.ericsson.ept.test.TestSceduling;

public class ENIQReportsPerformanceTool {

	private Configuration config;
	
	public void start(String configFilePath){
		
		//Load the configuration properties for the application
		try{
			System.out.println("Loading configuration from " + configFilePath);
			config = new Configuration(configFilePath);
		}catch(Exception e){
			System.err.println("Unable to load configuration file " + configFilePath );
			System.err.println("System will exit");
			System.exit(0);
		}
		
		//Load the test specs. If no path is given then there is nothing to test. 
		String testspecpath = config.getParameter(EPTParameters.TESTSPECPATH);
		HashMap<String, Configuration> testspecs = new HashMap<String, Configuration>();
		if(!testspecpath.equals("")){
			System.out.println("Loading test specs " + testspecpath);
			//Load each of the test specs. If any fail then disregard it and carry on
			File path = new File(testspecpath);
			for(File testspec : path.listFiles()){
				try{
					testspecs.put(testspec.getName(), new Configuration(testspec.getAbsolutePath()));
				}catch(Exception e){
					System.err.println("Unable to load test spec " + testspec.getAbsolutePath());
				}
			}
		}else{
			System.err.println("A directory path containing reports test spec's has not been defined");
			System.err.println("System will exit");
			System.exit(0);
		}
		
		//Schedule each test spec to run each day
		ExecutionManager execmanager = new ExecutionManager(config, testspecs.size());
		for (Map.Entry<String, Configuration> entry : testspecs.entrySet()) {
			String testspecname = entry.getKey();
		    Configuration testspec = entry.getValue();
		    
		    
		    String interval = testspec.getParameter(EPTParameters.TESTSPECINTERVAL);
		    String hours = testspec.getParameter(EPTParameters.STARTTIMEHOUR);
		    String mins = testspec.getParameter(EPTParameters.STARTTIMEMINS);
		    
		    long initialDelay = calculateInitialDelay(hours, mins);
		    long testinterval = TimeUnit.HOURS.toSeconds(Long.valueOf(interval));
		    		
		    TestSceduling test = new TestSceduling();
	    	test.init(config, testspecname, testspec, execmanager);
	    	
		    execmanager.addTestSpecToExecution(test, initialDelay, testinterval, TimeUnit.SECONDS);
		    
		}
		
	}
	
	public long calculateInitialDelay(String starthours, String startmins){
		Calendar cal = Calendar.getInstance();
		long now = cal.getTimeInMillis();
		
		cal.set(Calendar.HOUR_OF_DAY, Integer.valueOf(starthours));
		cal.set(Calendar.MINUTE, Integer.valueOf(startmins));
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		long delay = cal.getTimeInMillis() - now;
		
		//If the start time is in the past then increment the date to get the time tomorrow
		if (delay < 0){
			cal.set(Calendar.DATE, cal.get(Calendar.DATE) + 1);
			delay = cal.getTimeInMillis() - now;
		}
		delay = delay / 1000;
		
		return delay;
	}
	
	
	public static void main(String[] args) {
		ENIQReportsPerformanceTool erpt = new ENIQReportsPerformanceTool();
	
		erpt.start("C:\\Users\\uday\\Desktop\\ERPT\\config.properties");
		//erpt.start(args[0]);
	}

}
