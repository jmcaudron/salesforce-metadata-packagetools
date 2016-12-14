package com.jmc.force.metadata;


import java.io.File;
//import java.io.FileOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.sforce.soap.metadata.MetadataConnection;
//import com.sforce.soap.metadata.*;

//import com.sforce.ws.parser.XmlOutputStream;
import com.jmc.force.login.loginUtil;
import com.jmc.force.rest.restUtil;
import com.jmc.param.ParamManagement;
import com.jmc.param.ParamManagement.sobject;
import com.salesforce.dataloader.dao.csv.CSVFileWriter;
import com.salesforce.dataloader.model.Row;


public class BuildSpecFile {
	
	private static Logger logger = Logger.getLogger(BuildSpecFile.class);
	
	private MetadataConnection metadataConnection;
    private ObjectMapper mapper;
    private String sessionId;
    private String APIVersion;
 

    private static final  String dateFormat = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
    private static final  DateFormat df = new SimpleDateFormat(dateFormat); 
    
   
	public static void main(String[] args) throws Exception {
		PropertyConfigurator.configure("./conf/log4j.properties");
    	BuildSpecFile sample = new BuildSpecFile();
    	logger.info("Start run");
        sample.run();
        logger.info("End run");
    }
    public BuildSpecFile() {
    	mapper = new ObjectMapper();
    	mapper.setDateFormat(df);
    	
    }
    
    
    private void run() throws Exception {
    	JSONObject json;
		//JSONObject record;
		JSONArray records;
		//int size;
		//String soqlQuery ;
		String soqlObjectQuery;

	   	ParamManagement pm1 = new ParamManagement();
	   	pm1.test();
	   	
    	ParamManagement pm = new ParamManagement("conf/objects.json"); 
    	Map<String, sobject> confObjects = pm.mapObjects;
    	logger.info("object conf:"+confObjects.toString());
    	logger.info("object conf:Account"+confObjects.get("Account") +confObjects.get("Account").getExternalId());
    	
    	
    	loginUtil lu = new loginUtil();
     	metadataConnection =  lu.getMetadataConnection("");
     	APIVersion = lu.getAPIVersion();
     	
     	List<Row> rowsObject;
     	//Row rowObject =new Row();
       	List<String> headerObjects=  Arrays.asList("label","name","keyPrefix","custom","createable","updateable","deletable","undeletable","mergeable","searchable","queryable","replicateable","activateable","deprecatedAndHidden","layoutable","feedEnabled","retrieveable","hasSubtypes","mruEnabled","customSetting","labelPlural","triggerable") ;
     	CSVFileWriter fileObjects;
     	
     	
     	List<String> headerQueries = Arrays.asList("Object","Query");
     	CSVFileWriter fileQueries;
     	Row rowQuery =new Row();


     	//List<Row> rowsDictionary;
     	//Row rowDictionary = new Row(9);
     	List<String> headerDictionary = Arrays.asList("ObjectApiName","keyPrefix","ObjectLabel","name","label","type","length","precision","scale","digits","updateable","createable","filterable","groupable","custom","aggregatable","externalId","idLookup","defaultValue","compoundFieldName","nameField","restrictedDelete","relationshipName","controllerName","namePointing","defaultValueFormula","calculated","writeRequiresMasterRead","inlineHelpText","maskType","queryByDistance","caseSensitive","soapType","unique","highScaleNumber","displayLocationInDecimal","defaultedOnCreate","deprecatedAndHidden","byteLength","nillable","htmlFormatted","referenceTargetField","cascadeDelete","mask","permissionable","relationshipOrder","dependentPicklist","sortable","autoNumber","filteredLookupInfo","restrictedPicklist","encrypted","extraTypeInfo","calculatedFormula");
     	CSVFileWriter fileDictionary;
     	
     	List<Row> rowsField;
     	//Row rowField =new Row();
     	List<String> headerField =  Arrays.asList("name","label","type","length","precision","scale","digits","updateable","createable","filterable","groupable","custom","aggregatable","externalId","idLookup","defaultValue","compoundFieldName","nameField","restrictedDelete","relationshipName","controllerName","namePointing","defaultValueFormula","calculated","writeRequiresMasterRead","inlineHelpText","maskType","queryByDistance","caseSensitive","soapType","unique","highScaleNumber","displayLocationInDecimal","defaultedOnCreate","deprecatedAndHidden","byteLength","nillable","htmlFormatted","referenceTargetField","cascadeDelete","mask","permissionable","relationshipOrder","dependentPicklist","sortable","autoNumber","filteredLookupInfo","restrictedPicklist","encrypted","extraTypeInfo","calculatedFormula") ;
     	CSVFileWriter fileFields;
     	
     	File folder;
    	
     	List<Row> rowsSpec;
     	//Row rowSpec =new Row();
     	List<String> headerSpec =Arrays.asList("Salesforce Field","Csv Header","Value","Hint");
     	CSVFileWriter fileSpec;

     	List<Row> rowsExternalId;
     	List<String> headerExternalId =Arrays.asList("ObjectApiName","name","ObjectLabel","label");
     	CSVFileWriter fileExternalId;

     	List<Row> rowsMap;
     	List<String> headerMap =Arrays.asList("#SourceFieldDataLoader","TargetFieldDataLoader");
     	CSVFileWriter fileMap;
  
     	List<Row> rowsPicklist;
     	//Row rowPicklist =new Row();
     	List<String> headerPicklist =  Arrays.asList("ObjectApiName","ObjectLabel","fieldName","fieldlabel","value","label","defaultValue","active","validFor") ;
     	CSVFileWriter filePicklists;
        

     	folder = new File("map/");
     	folder.mkdirs();     	
     	folder = new File("desc/");
     	folder.mkdirs();
     	folder = new File("spec/");
     	folder.mkdirs();	
     	folder = new File("conf/");
     	folder.mkdirs();   
     	folder = new File("dict/");
     	folder.mkdirs();   
     	
    	//Boolean isExternalId ;
		
    	//dictionaryFile = new File("map/dictionary.csv");
    	//dictionaryfos= new FileOutputStream(dictionaryFile);
    	//rowsField= new ArrayList<Row>();
      	//rowsDictionary= new ArrayList<Row>();

    	fileObjects = new CSVFileWriter("dict/Objects.csv");
    	fileObjects.open();
    	fileObjects.setColumnNames(new ArrayList<String>(headerObjects));
    	
    	fileDictionary = new CSVFileWriter("dict/Fields.csv");
    	fileDictionary.open();
    	fileDictionary.setColumnNames(new ArrayList<String>(headerDictionary));

    	fileExternalId = new CSVFileWriter("dict/ExternalId.csv");
    	fileExternalId.open();
    	fileExternalId.setColumnNames(new ArrayList<String>(headerExternalId));
    	
    	fileQueries =new CSVFileWriter("dict/queries.csv" );
    	fileQueries.open();
    	fileQueries.setColumnNames(new ArrayList<String>(headerQueries));
    	
    	filePicklists =new CSVFileWriter("dict/picklist.csv" );
    	filePicklists.open();
    	filePicklists.setColumnNames(new ArrayList<String>(headerPicklist));
    	
    	sessionId = metadataConnection.getConfig().getSessionId();
    	String uri=metadataConnection.getConfig().getRestEndpoint();
    	
    	//logger.info("username="+metadataConnection.getConfig().getUsername());
    	logger.info("sessionId="+sessionId);
    	logger.info("Version API="+APIVersion);
    	logger.info("Service Endpoint="+metadataConnection.getConfig().getServiceEndpoint());
    	logger.info("Rest Endpoint="+metadataConnection.getConfig().getRestEndpoint());
    	 
     	logger.info("Start : Create list of objects"); 
		rowsObject=new ArrayList<Row>();
		
    	json = new JSONObject (restUtil.restGet(sessionId, uri+"/sobjects"));
    	
    	records = json.getJSONArray("sobjects");
    	rowsObject = getRows(records);
    	
  
    	fileObjects.writeRowList(rowsObject);
		//System.err.println(rowsField.toString());
    	fileObjects.close();
    	//rowsObject.clear();
       	
    	logger.info("End : Create list of objects. Nb object=" + rowsObject.size()); 

    
			for (Row sObject : rowsObject) {
				logger.info("Extract Object<"+sObject.get("name")+">");
				//objectFile = new File("map/"+object+"_desc.csv");
				
				//objectfos=new FileOutputStream(objectFile);
				rowsField=new ArrayList<Row>();
				rowsMap= new ArrayList<Row>();
				rowsSpec= new ArrayList<Row>();
		     	rowsExternalId= new ArrayList<Row>();
		     	rowsPicklist= new ArrayList<Row>();
		     	
		     	soqlObjectQuery="select id";
		     	
		     	json = new JSONObject (restUtil.restGet(sessionId, uri+"/sobjects/"+sObject.get("name")+"/describe"));
		    	records = json.getJSONArray("fields");
		    	rowsField = getRows(records);
		    	
		    	for (Row rowField : rowsField) {
		    		logger.debug("row:"+ rowField.get("name"));
		    		JSONArray referenceTo ;
		    		JSONArray picklist ;
		    		
		    		referenceTo = new JSONArray (rowField.get("referenceTo").toString());
		    		picklist = new JSONArray (rowField.get("picklistValues").toString());
		    		
		    		if (! rowField.get("name").toString().equals("Id"))
		    			soqlObjectQuery+= "," + rowField.get("name");
		    		
		    		rowField.put("#ApiName", rowField.get("name"));
		    		rowField.put("ObjectApiName",sObject.get("name"));
		    		rowField.put("ObjectLabel",sObject.get("label"));
		    		rowField.put("keyPrefix",sObject.get("keyPrefix"));
		    		 
		    		//picklistValues,referenceTo
		    		if (rowField.get("type").toString().equals("picklist")) {
						//logger.info("picklist='"+record.getJSONObject("Metadata").getJSONObject("picklist").toString(2));
						//rowsPicklist= addPicklist( records.getJSONObject(index));	
		    			logger.debug("picklist='"+rowField.get("picklistValues"));
		    			rowsPicklist= getRows(picklist);
		    			//"ObjectApiName","ObjectLabel","fieldName","fieldlabel",
		    			for (Row row : rowsPicklist) {
		    				row.put("ObjectApiName",rowField.get("ObjectApiName"));
		    				row.put("ObjectLabel",rowField.get("ObjectLabel"));
		    				row.put("fieldName",rowField.get("name"));
		    				row.put("fieldlabel",rowField.get("label"));	
		    			}
		    			
					} 
					
		    		rowField.put("Salesforce Field", rowField.get("name"));
		    		rowField.put("Csv Header", rowField.get("name"));
		    		rowField.put("Value", "");
		    		rowField.put("Hint", "");

		    		rowField.put("#SourceFieldDataLoader",rowField.get("name"));
		    		rowField.put("TargetFieldDataLoader",rowField.get("name"));
		    		
		    		
					rowField.put("Salesforce Field",rowField.get("name"));
					rowField.put("TargetFieldDataLoader",rowField.get("name"));
		    		if (!referenceTo.join("|").equals("") && !rowField.get("name").toString().equals("OwnerId") ) {
					//if (!referenceTo.join("|").equals("")) {
						
						String externalId ;
						
						externalId="Id" ;
						logger.debug("lookup :"+referenceTo.join("|") + ":"+confObjects.containsKey(referenceTo.join("|")));

						logger.debug("lookup :"+referenceTo.get(0) + ":"+confObjects.containsKey(referenceTo.get(0).toString()));
						
						if (confObjects.containsKey(referenceTo.get(0).toString())){
							externalId=confObjects.get(referenceTo.get(0).toString()).getExternalId();

							logger.debug("lookup Add:"+externalId);
							rowField.put("Salesforce Field",rowField.get("relationshipName")+"."+externalId);
							rowField.put("TargetFieldDataLoader",rowField.get("relationshipName")+"\\:"+externalId);
						
						}
					} 
					
					
					if (rowField.get("externalId").toString().equals("true")) {
						logger.info("External Id found.");
						rowField.put("#SourceFieldDataLoader","Id");
						rowField.put("Csv Header", "Id");
						rowsExternalId.add(rowField);
					}
					
			   		if (rowField.get("createable").toString().equals("true") ) {
		    			logger.debug("row:"+ rowField.get("name")+"---New Updateable");
						rowsMap.add(rowField);
						rowsSpec.add(rowField);		
					}

		    	}
		    	
			
				//objectfos.close();
				//"Object","Query"
				soqlObjectQuery+= " from " + sObject.get("name");
				rowQuery.put("Object", sObject.get("name"));
				rowQuery.put("Query", soqlObjectQuery);
				fileQueries.writeRow(rowQuery);
				
				//logger.error("rowsField<"+sObject.get("name")+">=" +rowsField.toString());
				fileDictionary.writeRowList(rowsField); 
				fileExternalId.writeRowList(rowsExternalId);
				filePicklists.writeRowList(rowsPicklist);
				//logger.error("picklist<"+object+">=" +rowsPicklist.toString());
				
				fileFields =new CSVFileWriter("desc/"+sObject.get("name")+".csv" );
				fileFields.open();
				fileFields.setColumnNames(new ArrayList<String>(headerField));
				fileFields.writeRowList(rowsField);
				//System.err.println(rowsField.toString());
				fileFields.close();
				
				fileMap =new CSVFileWriter("map/"+sObject.get("name")+".sdl" );
				fileMap.open();
				fileMap.setColumnNames(new ArrayList<String>(headerMap));
				fileMap.writeRowList(rowsMap);
				//System.err.println(rowsMap.toString());
				fileMap.close();
				
				fileSpec =new CSVFileWriter("spec/"+sObject.get("name")+".csv" );
				fileSpec.open();
				fileSpec.setColumnNames(new ArrayList<String>(headerSpec));
				fileSpec.writeRowList(rowsSpec);
				//System.err.println(rowsField.toString());
				fileSpec.close();	
						
			}
			//dictionaryfos.close();
			fileDictionary.close();
			fileExternalId.close();
			fileQueries.close();
			filePicklists.close();
		//}
    	
   

    }
   /* private List<Row> addPicklist(JSONObject record) {
    	JSONObject jsonPicklist;
    	Row rowPicklist ;
    	List<Row> rowsPicklist = new ArrayList<Row>();
    	jsonPicklist = record.getJSONObject("Metadata").getJSONObject("picklist");
 
    	for (Object PicklistValue : jsonPicklist.getJSONArray("picklistValues")) {
    		JSONObject jsonPicklistValue = (JSONObject) PicklistValue;
    		rowPicklist = new Row(30);
    	   	rowPicklist.put("ObjectApiName",record.getJSONObject("EntityDefinition").get("QualifiedApiName"));
    		rowPicklist.put("ApiName", record.get("QualifiedApiName"));
    		rowPicklist.put("Label",record.get("Label"));
    		
        	rowPicklist.put("restrictedPicklist", jsonPicklist.get("restrictedPicklist"));
        	rowPicklist.put("sorted", jsonPicklist.get("sorted"));
        	rowPicklist.put("controllingField", jsonPicklist.get("controllingField"));   		
    		
    		Iterator<String> i= jsonPicklistValue.keys() ;
    		while(i.hasNext()) {
    			String key =i.next();
    			rowPicklist.put(key,jsonPicklistValue.get(key));
    			logger.debug(key +':'+jsonPicklistValue.get(key));
    		}
    		rowsPicklist.add(rowPicklist);
        	//"cssExposed", "color","valueName","allowEmail","probability","description","isActive","default","highPriority","urls","converted","reverseRole","controllingFieldValues","forecastCategory","won","closed","reviewed"    		
    	}
    	return rowsPicklist;
    }*/

    
    private List<Row> getRows(JSONArray jsonArr){
    	List<Row> rows = new ArrayList<Row>();
    	Row row;
    	JSONObject json;

   		logger.debug( "getRows:"+ jsonArr.toString(2));
   		
    	Iterator<Object> iJson = jsonArr.iterator() ;
       	while (iJson.hasNext()) {     		
       		json = (JSONObject) iJson.next();
       		row = getRow(json);
       		rows.add(row);
       	}
    	return rows;
    }   
    private Row getRow(JSONObject jsonObj){
    	Row row = new Row();

   		Iterator<String> objectKeys ;
   		
   		logger.debug( "getRow:"+ jsonObj.toString(2));
   		objectKeys = jsonObj.keys();
   		while (objectKeys.hasNext()) {
   			String key;
   			String value;
   			key =objectKeys.next();
   			value = jsonObj.optString(key);
   			logger.debug( key+":"+value);
   			row.put(key, value);
   		}
    	return row;	
    	
    }   
        
}