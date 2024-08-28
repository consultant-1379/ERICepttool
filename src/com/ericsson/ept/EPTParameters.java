package com.ericsson.ept;

public class EPTParameters {

	//Sets the directory of where to find the Test Specs
	public static final String TESTSPECPATH = System.getProperty("TESTSPECPATH", "Test_Spec_Path");
	
	//Number of thread executors to create
	public static final String NUMBEROFEXECUTORS = System.getProperty("NUMBEROFEXECUTORS", "Number_Of_Executors");
	
	//Number of active threads per executor
	public static final String ACTIVETHREADSPEREXEC = System.getProperty("ACTIVETHREADSPEREXEC", "Active_Threads_Per_Executor");
	
	//CMS details
	public static final String CMSNAME = System.getProperty("CMSNAME", "CMS_Name");
	public static final String CMSUSER = System.getProperty("CMSUSER", "CMS_Username");
	public static final String CMSPASSWD = System.getProperty("CMSPASSWD", "CMS_Password");
	public static final String CMSAUTHTYPE = System.getProperty("CMSAUTHTYPE", "CMS_Auth_Type");
	
	//Output properties
	public static final String TESTREPORTPATH = System.getProperty("TESTREPORTPATH", "Test_Report_Path");
	public static final String WRITEDATA = System.getProperty("WRITEDATA", "Write_Data_To_Zip");
	
	//Test Spec Properties
	public static final String REPORTNAME = System.getProperty("REPORTNAME", "Report_Name");
	public static final String MATCHTYPE = System.getProperty("MATCHTYPE", "Match_Type");
	public static final String TESTSPECINTERVAL = System.getProperty("TESTSPECINTERVAL", "Test_Spec_Interval");
	public static final String STARTTIMEHOUR = System.getProperty("STARTTIMEHOUR", "Start_Time_Hour");
	public static final String STARTTIMEMINS = System.getProperty("STARTTIMEMINS", "Start_Time_Mins");
	public static final String ENDTIMEHOUR = System.getProperty("ENDTIMEHOUR", "End_Time_Hour");
	public static final String ENDTIMEMINS = System.getProperty("ENDTIMEMINS", "End_Time_Mins");
	public static final String REFRESHINTERVAL = System.getProperty("REFRESHINTERVAL", "Refresh_Interval");
	public static final String PROMPTNAME = System.getProperty("PROMPTNAME", "_Name");
	public static final String PROMPTNAMEMATCHTYPE = System.getProperty("PROMPTNAMEMATCHTYPE", "_Name_Match_Type");
	public static final String PROMPTVALUE = System.getProperty("PROMPTVALUE", "_Value");
	public static final String PROMPTVALUETYPE = System.getProperty("PROMPTVALUETYPE", "_Value_Type");
	
	//BIS REST COMMANDS
	public static final String LISTDOCUMENTS = "/raylight/v1/documents?offset=$offset&limit=$limit";
	public static final String DOCUMENTPARAMS = "/raylight/v1/documents/$id/parameters";
	public static final String LISTREPORTS = "/raylight/v1/documents/$id/reports";
	public static final String REPORTDATA = "/raylight/v1/documents/$id/reports/$reportid";
	public static final String LISTDATAPROVIDERS = "/raylight/v1/documents/$id/dataproviders";
	public static final String DATAPROVIDER = "/raylight/v1/documents/$id/dataproviders/$DP/flows/0";
	
	
}
