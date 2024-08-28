package com.ericsson.ept.test;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class TestUtilities {

	public Boolean compare(String testString, String comparisonString, String comparisonType){
		if(comparisonType.equalsIgnoreCase("EQUALS")){
			return testString.equals(comparisonString);
		}
		else if(comparisonType.equalsIgnoreCase("CONTAINS")){
			return testString.contains(comparisonString);
		}
		else if(comparisonType.equalsIgnoreCase("STARTSWITH")){
			return testString.startsWith(comparisonString);
		}
		else if(comparisonType.equalsIgnoreCase("ENDSWITH")){
			return testString.endsWith(comparisonString);
		}
		else if(comparisonType.equalsIgnoreCase("REGEX")){
			
			Pattern pattern = Pattern.compile(comparisonString);
			Matcher matcher = pattern.matcher(testString);
			return matcher.find();
		}
		return false;
	}
	
	public HashMap<String, ArrayList<String>> parsePromptValues(Document responseDoc){
		HashMap<String, ArrayList<String>> promptValues = new HashMap<String, ArrayList<String>>();
		
		NodeList names=responseDoc.getElementsByTagName("name");
		NodeList answers=responseDoc.getElementsByTagName("answer");
		for(int i=0;i<names.getLength();i++){
	    	Node n=names.item(i);
	    	Node x=answers.item(i);
	    	n.getTextContent();
	    	
	    	ArrayList<String> value = new ArrayList<String>();
	    	if(x instanceof Element){
	    		Element answer = (Element) x;
	    		NodeList values=answer.getElementsByTagName("value");
	    		for(int a=0;a<values.getLength();a++){
	    			Node node = values.item(a);
	    			value.add(node.getTextContent());
	    		}
	    	}
	    	
	    	promptValues.put(n.getTextContent(), value);
	    	
		}
		return promptValues;
		
	}
	
	public ArrayList<String> getRequestValues(ArrayList<String> values, String prompt_value, String prompt_value_type){
		ArrayList<String> requestValues = new ArrayList<String>();
		
		Calendar starttime = Calendar.getInstance();
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
	String	starttimeString = format.format(starttime.getTime());
		if(prompt_value_type.equalsIgnoreCase("EQUALS")){

			if(prompt_value.equalsIgnoreCase("TODAY")){
				prompt_value=starttimeString;
			}
	
			requestValues.add(prompt_value);
		}
		else if(prompt_value_type.equalsIgnoreCase("RANDOM")){
			int numberofvalues = Integer.valueOf(prompt_value);
			for(int count = 0; count < numberofvalues; count++){
				int randomNum = ThreadLocalRandom.current().nextInt(0, values.size());
				requestValues.add(values.get(randomNum));	
			}

		}
		
		return requestValues;
		
	}
	
	public Document populateRequestContent(Document requestTemplate, HashMap<String, ArrayList<String>> requestvalues){
		NodeList answers=requestTemplate.getElementsByTagName("answer");
		NodeList names=requestTemplate.getElementsByTagName("name");
		
		for(int i=0;i<answers.getLength();i++){
			Node answer = answers.item(i);
			String parameterName = names.item(i).getTextContent();
			
			if(requestvalues.containsKey(parameterName)){
				Element valuesElement = requestTemplate.createElement("values");
				ArrayList<String> values = requestvalues.get(parameterName);
				
				Element valueentry = null;
				for(String value: values){
					valueentry = requestTemplate.createElement("value");
					valueentry.setTextContent(value);
					valuesElement.appendChild(valueentry);
				}
				
				if(valueentry != null){
					answer.appendChild(valuesElement);
				}
				
			}
			
		}
		return requestTemplate;
	}
	
	
	public Document createRequestTemplate(Document requestDoc){
		NodeList answers=requestDoc.getElementsByTagName("answer");
		for(int i=0;i<answers.getLength();i++){
			Node answer = answers.item(i);
			removeChildren(answer);
		}		
		return requestDoc;
	}
	
	
	public static void removeChildren(Node node) {
	    while (node.hasChildNodes())
	        node.removeChild(node.getFirstChild());
	}
	
	public HashMap<String, String> parseResponse(Document response){
		HashMap<String, String> reports = new HashMap<String, String>();
		
		NodeList values=response.getElementsByTagName("id");
	    NodeList values1=response.getElementsByTagName("name");
	    for(int i=0;i<values.getLength();i++){
	    	Node n=values.item(i);
	    	Node n1=values1.item(i);
	    	reports.put(n.getTextContent(), n1.getTextContent());
	    }
	    return reports;
	}

	
}
