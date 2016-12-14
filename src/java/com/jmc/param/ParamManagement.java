package com.jmc.param;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;


public class ParamManagement {
	
	public static class sobject {
		
		private String name;
		private String externalId;
		
		public sobject() {
			this.name="Account";
			this.externalId="Id";
		}
		public sobject(String name) {
			this.name=name;
			this.externalId="Id";
		}	
		public sobject(String name, String externalId) {
			this.name=name;
			this.externalId=externalId;
		}
		public String getName() {
			return name;
		}
		public String getExternalId() {
			return externalId;
		}
		private sobject getObject() {
			return this;
		}
		
	}
	
	public static  class parameters  {
		 public List<sobject> listObjects;
		 public parameters() {
			 //mapObjects = new HashMap<String, sobject>();
			 listObjects= new ArrayList<sobject>(0);
			 //mapObjects = listObjects.stream().collect( Collectors.toMap(sobject::getName, sobject::getObject));
		}
	}

	
	public parameters params ;
	public Map<String, sobject> mapObjects;
	
	public ParamManagement() throws JsonParseException, JsonMappingException, IOException {
		super();
		//loadParameters("");
	}

	public ParamManagement(String configFile) throws JsonParseException, JsonMappingException, IOException  {
		super();
		loadParameters(configFile);
	}
	
	private void loadParameters(String configFile) throws JsonParseException, JsonMappingException, IOException {
		if (configFile == "") 
	   		configFile= "params.json";
	    ObjectMapper mapper = new ObjectMapper();
	    //mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES,false);
	    File config =new File(configFile);
	    params= new parameters();
	    if (config.exists()) {
	    	//params= mapper.readValue(new File(configFile), new TypeReference<Map<String, sobject>>(){});
	    	System.out.println(params.getClass());
	    	System.out.println(configFile.toString());
	    	params= mapper.readValue(new File(configFile),params.getClass() );
	    }  else
	    	params= new parameters();
	    mapObjects = params.listObjects.stream().collect( Collectors.toMap(sobject::getName, sobject::getObject)); 
		
	}	
	public void test () {
		System.out.println("Start test");
		params =new parameters();
		sobject o = new sobject("Account", "ExternalId__c");
		params.listObjects.add( o);
		params.listObjects.add(new sobject("Contact", "ExternalId__c"));
		mapObjects = params.listObjects.stream().collect( Collectors.toMap(sobject::getName, sobject::getObject));
		
	    ObjectMapper mapper = new ObjectMapper();
		try {
			System.out.println(mapper.writeValueAsString(params));
			System.out.println(mapper.writeValueAsString(mapObjects));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("End test");
	}


}
