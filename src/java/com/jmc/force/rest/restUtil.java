package com.jmc.force.rest;

import java.io.IOException;
import java.net.URLEncoder;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;


public class restUtil {

	public static String restQuery(String sessionId, String baseUri, String query) {
		String response_string ="";
		try {
			
			Header authHeader;
			Header prettyPrintHeader;
			//Set up the HTTP objects needed to make the request.
			HttpClient client = HttpClientBuilder.create().build();
			String uri = baseUri + "/query/?q="+ URLEncoder.encode(query, "UTF-8");
			
			authHeader = new BasicHeader("Authorization", "Bearer  " + sessionId);
			prettyPrintHeader = new BasicHeader("X-PrettyPrint", "1");
			
			
			//System.out.println("Query URL: " + uri);
			HttpGet httpGet = new HttpGet(uri);
			httpGet.addHeader(authHeader);
			httpGet.addHeader(prettyPrintHeader);
			
			// Make the request.
			HttpResponse response = client.execute(httpGet);
			
			// Process the result
			int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode == 200) {
				response_string = EntityUtils.toString(response.getEntity());
			} else {
				System.err.println("Query was unsuccessful. Status code returned is " + statusCode);
				System.err.println(response.toString());
				System.err.println(EntityUtils.toString(response.getEntity()));
			}
		} catch (IOException ioe) {
			ioe.printStackTrace();
		} catch (NullPointerException npe) {
			npe.printStackTrace();
		}
		return  response_string;
	}
	public static String restGet(String sessionId, String uri) {
		String response_string ="";
		try {
			
			Header authHeader;
			Header prettyPrintHeader;
			//Set up the HTTP objects needed to make the request.
			HttpClient client = HttpClientBuilder.create().build();
			//String uri = baseUri + URLEncoder.encode(endUri, "UTF-8");
			
			authHeader = new BasicHeader("Authorization", "Bearer  " + sessionId);
			prettyPrintHeader = new BasicHeader("X-PrettyPrint", "1");
			
			
			//System.out.println("Query URL: " + uri);
			HttpGet httpGet = new HttpGet(uri);
			httpGet.addHeader(authHeader);
			httpGet.addHeader(prettyPrintHeader);
			
			// Make the request.
			HttpResponse response = client.execute(httpGet);
			
			// Process the result
			int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode == 200) {
				response_string = EntityUtils.toString(response.getEntity());
       	  
			} else {
				System.err.println("Query was unsuccessful. Status code returned is " + statusCode);
				System.err.println(response.toString());
				System.err.println(EntityUtils.toString(response.getEntity()));
			}
		} catch (IOException ioe) {
			ioe.printStackTrace();
		} catch (NullPointerException npe) {
			npe.printStackTrace();
		}
		return  response_string;
	}
}
