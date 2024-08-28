package testing;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;

import com.ericsson.ept.Configuration;
import com.ericsson.ept.EPTParameters;
import com.ericsson.ept.connection.BISConnection;
import com.ericsson.ept.connection.CommonUtils;
import com.ericsson.ept.test.TestExecution;
import com.ericsson.ept.test.TestUtilities;

public class TESTER {

	public void run(){
		try{
			String configFilePath = "C:\\Users\\ebrifol\\Documents\\Projects\\EPT\\configuration\\config.properties";
			Configuration config = new Configuration(configFilePath);
			BISConnection bis = new BISConnection(config);
			CommonUtils commonUtils = new CommonUtils();
			TestUtilities testutils = new TestUtilities();
			
			String listDocs = EPTParameters.REPORTDATA;
			listDocs = listDocs.replace("$offset", "100");
			listDocs = listDocs.replace("$limit", "50");
			listDocs = listDocs.replace("$id", "6311");
			listDocs = listDocs.replace("$reportid", "5");
			
			
			String response = bis.sendRequest(listDocs, "GET", null, "text/csv");
			
//			try(  PrintWriter out = new PrintWriter( "C:\\Users\\ebrifol\\Documents\\Projects\\EPT\\KPIs.xls" )  ){
//			    out.println( response );
//			    out.close();
//			}
		
//			
			//String response = bis.sendRequest(listDocs, "GET", null, "application/xml");
			
			System.out.println(response);
			
//			Document responseDoc = commonUtils.transformXmlStringToDocument(response);
//			HashMap<String, ArrayList<String>> values = testutils.parsePromptValues(responseDoc);
//			
//			for (Map.Entry<String, ArrayList<String>> innerentry : values.entrySet()) {
//			    String docid = innerentry.getKey();
//			    ArrayList<String> docname = innerentry.getValue();
//			    
//			    System.out.println(docid);
//			    for(String value : docname){
//			    	System.out.println("\t" + value);
//			    }
//			    
//			}
//			
//			Document requestTemplate = testutils.createRequestTemplate(responseDoc);
//			
//			
//			TransformerFactory tf = TransformerFactory.newInstance();
//			Transformer transformer = tf.newTransformer();
//			transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
//			StringWriter writer = new StringWriter();
//			transformer.transform(new DOMSource(requestTemplate), new StreamResult(writer));
//			String output = writer.getBuffer().toString();
//			
//			
//			System.out.println(output);
		
		}catch(Exception e){
			e.printStackTrace();
		}
		
		
		
	}
	
	public static void main(String[] args) {
		TESTER t = new TESTER();
		t.run();
	}

}
