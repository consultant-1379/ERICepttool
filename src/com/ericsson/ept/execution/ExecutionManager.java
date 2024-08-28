package com.ericsson.ept.execution;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import com.ericsson.ept.Configuration;
import com.ericsson.ept.EPTParameters;

public class ExecutionManager {

	private ArrayList<TestExecutionPool> Executors;
	private Configuration config;
	private TestSpecExecutionPool testspecpool;
	
	public ExecutionManager(Configuration config, int numberofTestSpecs){
		Executors = new ArrayList<TestExecutionPool>();
		this.config = config;
		this.testspecpool = new TestSpecExecutionPool(numberofTestSpecs);
		initializeExecutors();
	}
	
	public void addTestSpecToExecution(ExecutorThread test, long delayToStart, long interval, TimeUnit timeunit){
		testspecpool.addToExecutor(test, delayToStart, interval, timeunit);
	}
	
	public void addTestToExecution(ExecutorThread test){
		//Find the least loaded executor from the collection
		TestExecutionPool executor = null;
		for(TestExecutionPool es : Executors){
			if(executor == null){
				executor = es;
			}else if(es.getQueuedCount() < executor.getQueuedCount()){
				executor = es;
			}
		}

		executor.addToExecutor(test);
	}
	
	public void shutdown(){
		for(TestExecutionPool es : Executors){
			es.shutdown();
		}
		testspecpool.shutdown();
	}
	
	
	private void initializeExecutors(){
		System.out.println("Initializing Executors");		
		int numberofExecs = validateParameters(EPTParameters.NUMBEROFEXECUTORS, config.getParameter(EPTParameters.NUMBEROFEXECUTORS));
		int activeThreadsPerExec = validateParameters(EPTParameters.ACTIVETHREADSPEREXEC, config.getParameter(EPTParameters.ACTIVETHREADSPEREXEC));
		
		for(int count = 0; count<numberofExecs; count++){
			Executors.add(new TestExecutionPool(activeThreadsPerExec));
		}
		
		System.out.println("Initialized " + numberofExecs + " executors.");
	}	
	
	private int validateParameters(String param, String value){
		if(value.equals("")){
			System.err.println("Invalid value for " + param + " provided. Default value of 1 used");
			return 1;
		}
		
		try{
			return Integer.parseInt(value);
		}catch(Exception e){
			System.err.println("Unable to parse " + param + " to an Integer. Default value of 1 used");
			return 1;
		}
		
	}
	
}
