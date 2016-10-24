package com.jmc.force.metadata;


import java.io.File;
import java.io.IOException;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;


import com.sforce.ws.ConnectionException;
import com.sforce.ws.ConnectorConfig;

import com.sforce.soap.metadata.MetadataConnection;
import com.sforce.soap.partner.PartnerConnection;
import com.sforce.soap.partner.LoginResult;



/**
 * Login utility.
 */
public class MetadataLoginUtil {
   
	private static loginInfo logInfo  ;

	
    public static class loginInfo  {
    	public String username;
    	public String password;
    	public String apiVersion;
    	public String url;
    
    	public loginInfo() {
    		super();
    		username=password=apiVersion=url="";
    	}
    	
    	
    	public loginInfo (String userame, String password, String apiversion, String url) {
    		this.username = userame;
    		this.apiVersion = apiversion;
    		this.password = password;
    		this.url = url;
    	}
    }

    public static MetadataConnection login() throws ConnectionException, JsonParseException, JsonMappingException, IOException {
    	ObjectMapper mapper = new ObjectMapper();
    	  	
		logInfo= mapper.readValue(new File("loginInfo.json"), loginInfo.class);
		
    	//System.out.println("username:"+logInfo.username);
        
        final LoginResult loginResult = loginToSalesforce(logInfo.username, logInfo.password, logInfo.url);
        return createMetadataConnection(loginResult);
    }

    private static MetadataConnection createMetadataConnection(
            final LoginResult loginResult) throws ConnectionException {
        final ConnectorConfig config = new ConnectorConfig();
        config.setServiceEndpoint(loginResult.getMetadataServerUrl());
        config.setSessionId(loginResult.getSessionId());
        //config.setPrettyPrintXml(true);
        String soapEndpoint = config.getServiceEndpoint();
        String restEndpoint = soapEndpoint.substring(0, soapEndpoint.indexOf("Soap/"))+ "data/v" + logInfo.apiVersion;
        config.setRestEndpoint(restEndpoint);
        return new MetadataConnection(config);
    }

    private static LoginResult loginToSalesforce(
            final String username,
            final String password,
            final String loginUrl) throws ConnectionException {
        final ConnectorConfig config = new ConnectorConfig();
        config.setAuthEndpoint(loginUrl);
        config.setServiceEndpoint(loginUrl);
        config.setManualLogin(true);
        config.setPrettyPrintXml(true);
        //config.setUsername(username);
        //config.setPassword(password);
        return (new PartnerConnection(config)).login(username, password);
    }
}
