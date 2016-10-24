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
//import org.json.JSONException;
//import org.json.JSONObject;

public class restUtil {

	public static String restGet(String sessionId, String baseUri, String query) {
		//System.out.println("\n_______________  QUERY _______________");
		String response_string ="";
		try {
			
			Header authHeader;
			Header prettyPrintHeader;
			//Set up the HTTP objects needed to make the request.
			HttpClient client = HttpClientBuilder.create().build();
			String uri = baseUri + "/query?q="+ URLEncoder.encode(query, "UTF-8");
			
			authHeader = new BasicHeader("Authorization", "Bearer  " + sessionId);
			prettyPrintHeader = new BasicHeader("X-PrettyPrint", "1");
			
			
			System.out.println("Query URL: " + uri);
			HttpGet httpGet = new HttpGet(uri);
			httpGet.addHeader(authHeader);
			httpGet.addHeader(prettyPrintHeader);
			
			// Make the request.
			HttpResponse response = client.execute(httpGet);
			
			// Process the result
			int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode == 200) {
				response_string = EntityUtils.toString(response.getEntity());
				/*try {
					JSONObject json = new JSONObject(response_string);
					//System.out.println("JSON result of Query:\n" + json.toString(1));					
		
				} catch (JSONException je) {
					je.printStackTrace();
				} */       	  
			} else {
				System.out.println("Query was unsuccessful. Status code returned is " + statusCode);
				System.out.println(response.toString());
			}
		} catch (IOException ioe) {
			ioe.printStackTrace();
		} catch (NullPointerException npe) {
			npe.printStackTrace();
		}
		return  response_string;
	}
	
}
