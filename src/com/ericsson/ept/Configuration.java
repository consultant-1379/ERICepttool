package com.ericsson.ept;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;

public class Configuration {

	private Properties config;
	private String configFilePath; 
	
	public Configuration(String configFilePath) throws IOException{
		config = new Properties();
		this.configFilePath = configFilePath;
		loadConfigFile();
	}
	
	public String getParameter(String keyname){
		String property = config.getProperty(keyname);
		if(property == null){
			return "";
		}
		return property;
	}
	
	
	private void loadConfigFile() throws IOException{
		FileInputStream loadProps = new FileInputStream(configFilePath);
		config.load(loadProps);
		
	}
	
	public ArrayList<Object> getKeySet(){
		return new ArrayList<Object>(config.keySet());
	}
	
}
