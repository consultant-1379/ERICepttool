package com.ericsson.ept.execution;


import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class TestSpecExecutionPool {

	private ScheduledExecutorService executor = null;
	private int queuedCount=0;
	
	public TestSpecExecutionPool(int noOfexecutionSlots){
		executor = Executors.newScheduledThreadPool(noOfexecutionSlots);
	}
	
	public void addToExecutor(ExecutorThread thread, long delayToStart, long interval, TimeUnit timeunit){
		executor.scheduleAtFixedRate(thread, delayToStart, interval, timeunit);
		queuedCount++;
	}
	
	public void shutdown(){
		executor.shutdown();
	}
	
	public int getQueuedCount(){
		return queuedCount;
	}
}
