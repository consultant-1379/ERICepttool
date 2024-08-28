package com.ericsson.ept.connection;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Request {

	private static boolean isTrace = true;

	private String requestUrl, requestMethod, requestContent, responseContent, responseMessage;
	private int responseCode;
	private Map<String, List<String>> responseHeaders;
	private String logonToken;

	public int getResponseCode() {
		return responseCode;
	}

	public String getResponseMessage() {
		return responseMessage;
	}

	public String getRequestContent() {
		return requestContent;
	}

	public String getResponseContent() {
		return responseContent;
	}

	public Map<String, List<String>> getResponseHeaders() {
		return responseHeaders;
	}

	public static boolean getIsTrace() {
		return isTrace;
	}

	public static void setIsTrace(boolean trace) {
		Request.isTrace = trace;
	}

	// Constructor
	public Request() {
	}

	// Constructor
	public Request(String logonToken) {
		this.logonToken = logonToken;
	}

	/**
	 * @see #send(String, String, String, String)
	 */
	public String send(String url, String method, String xmlContent) throws Exception {
		String trace = send(url, method, xmlContent, "application/xml");

		return trace;
	}

	/**
	 * Utility method to send HTTP requests.
	 * <p>
	 * It provides the following features:
	 * <ul>
	 * <li>Handling SAP-specific headers, such as X-SAP-LogonToken</li>
	 * <li>Allowing to add XML content to the request</li>
	 * <li>Reading the server response, available via the members
	 * <i>responseContent</i>, <i>responseCode</i> and
	 * <i>responseMessage</i></li>
	 * <li>Showing the request and response in the console by setting the static
	 * member <i>trace</i> value to <i>true</i></li>
	 * </ul>
	 * </p>
	 * 
	 * @param url
	 *            The URL to send
	 * @param method
	 *            The HTTP request method (GET, POST, PUT or DELETE)
	 * @param xmlContent
	 *            The XML content to send along with the request, or <i>null</i>
	 *            if no content has to be sent
	 * @param accept
	 *            Accept header value, corresponding to the requested format for
	 *            the response
	 * @throws Exception
	 */
	public String send(String url, String method, String xmlContent, String accept) throws Exception {

		this.reset();
		HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
		connection.setRequestMethod(method);

		if (accept != null) {
			connection.setRequestProperty("Accept", accept);
		}

		if (this.logonToken != null) {
			connection.setRequestProperty("X-SAP-LogonToken", this.logonToken);
		}

		if (xmlContent != null) {
			connection.setDoOutput(true);
			connection.setRequestProperty("Content-Type", "application/xml");
			connection.setRequestProperty("Content-Length", String.valueOf(xmlContent.getBytes().length));
			DataOutputStream out = new DataOutputStream(connection.getOutputStream());
			out.writeBytes(xmlContent);
			out.flush();
			out.close();
		}

		// Reads response
		InputStream in;
		try {
			in = (InputStream) connection.getContent();

		} catch (IOException e) {
			in = connection.getErrorStream();
		}
		if (in == null)
			throw new Exception("Connection to " + url + " failed");

		Scanner scanner = new Scanner(in).useDelimiter("\\A");

		requestUrl = url;
		requestMethod = method;
		requestContent = xmlContent;

		responseContent = scanner.hasNext() ? scanner.next() : "";
		// System.out.println("innnnnnnnnnnnnn "+scanner.next());
		responseCode = connection.getResponseCode();
		responseMessage = connection.getResponseMessage();
		responseHeaders = connection.getHeaderFields();

		String trace = "";
		if (isTrace)
			trace = trace(connection);

		in.close();
		connection.disconnect();

		return trace;
	}

	// Private

	/**
	 * Clears the request information, response content and SAP-specific
	 * headers.
	 */
	private void reset() {
		requestUrl = null;
		requestMethod = null;
		requestContent = null;
		responseContent = null;
		responseMessage = null;
		responseHeaders = null;
		responseCode = 0;
	}

	/**
	 * Traces the HTTP URL connection.
	 * 
	 * @param connection
	 * @throws Exception
	 */
	private String trace(HttpURLConnection connection) throws Exception {
		String trace = "\n" + toString() + "\n";
		trace += "=== Headers ===\n";
		for (String key : connection.getHeaderFields().keySet()) {
			trace += key + ": " + connection.getHeaderFields().get(key) + "\n";
		}
		// Adds a leading | for console readability
		trace = trace.replaceAll("\r\n", "\n");
		trace = trace.replaceAll("\n", "\n| ");
		trace = trace.replaceAll("\n\\| $", "");

		return trace;
	}

	public String send1(String url, String method, String xmlContent, String accept) throws Exception {
		// System.out.println("url "+url);
		this.reset();
		HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
		connection.setRequestMethod(method);

		if (accept != null) {
			connection.setRequestProperty("Accept", accept);
		}

		if (this.logonToken != null) {
			connection.setRequestProperty("X-SAP-LogonToken", this.logonToken);
		}

		if (xmlContent != null) {
			connection.setDoOutput(true);
			connection.setRequestProperty("Content-Type", "application/xml");
			connection.setRequestProperty("Content-Length", String.valueOf(xmlContent.getBytes().length));
			DataOutputStream out = new DataOutputStream(connection.getOutputStream());
			out.writeBytes(xmlContent);
			out.flush();
			out.close();
		}

		// Reads response
		InputStream in;
		try {
			in = (InputStream) connection.getContent();
		} catch (IOException e) {
			in = connection.getErrorStream();
		}
		if (in == null)
			throw new Exception("Connection to " + url + " failed");

		Scanner scanner = new Scanner(in).useDelimiter("\\A");

		requestUrl = url;
		requestMethod = method;
		requestContent = xmlContent;
		responseContent = scanner.hasNext() ? scanner.next() : "";

		responseCode = connection.getResponseCode();
		responseMessage = connection.getResponseMessage();
		responseHeaders = connection.getHeaderFields();

		String trace = "";
		if (isTrace)
			trace = trace(connection);

		in.close();
		connection.disconnect();

		return trace;
	}

	/**
	 * Displays the request and the response information.
	 */
	@Override
	public String toString() {
		String message = "[%s] %s\n" + "=== Request content ===\n" + "%s\n" + "=== Response code ===\n" + "%d\n"
				+ "=== Response message ===\n" + "%s\n" + "=== Response content ===\n" + "%s";
		return String.format(message, requestMethod, requestUrl, requestContent, responseCode, responseMessage,
				responseContent);
	}
}

