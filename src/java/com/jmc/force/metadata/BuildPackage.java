package com.jmc.force.metadata;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import javax.xml.namespace.QName;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import org.codehaus.jackson.map.ObjectMapper;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import com.sforce.soap.metadata.DescribeMetadataResult;
import com.sforce.soap.metadata.ListMetadataQuery;
import com.sforce.soap.metadata.MetadataConnection;
//import com.sforce.soap.metadata.*;
import com.sforce.soap.metadata.Package;
import com.sforce.soap.metadata.PackageTypeMembers;
import com.sforce.ws.bind.TypeMapper;
import com.sforce.ws.parser.XmlOutputStream;
import com.jmc.force.login.loginUtil;
import com.jmc.force.rest.restUtil;

public class BuildPackage {
	
	private static Logger logger = Logger.getLogger(BuildPackage.class);
	
	private MetadataConnection metadataConnection;
    private ObjectMapper mapper;
    private TypeMapper typeMapper=new TypeMapper();
    private String sessionId;
    private String uri;
    private static metadataInfo metaInfo  ;
    private static final  String dateFormat = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
    private static final  DateFormat df = new SimpleDateFormat(dateFormat); 
    private enum packageType { All, New, Updated };
    
    public static class metadataInfo  {
    	public ListMetadataQuery[] queries;
    	public Calendar fromDate;
    	public double version;
    
    	public metadataInfo() {
    		//super();
    		queries = new ListMetadataQuery[0]; 
    	}
    	
    	public metadataInfo (ListMetadataQuery[] q) {
    		this.queries = q;
    		version = 38;
    		fromDate = new GregorianCalendar(2010,1,23,13,00,00);
    	}
    	public metadataInfo (ListMetadataQuery[] q, Calendar d, double v) {
    		queries = q;
    		version = v;
    		fromDate = d;
    	}
        private String getStringDate(/*Calendar dt*/) {
        	return df.format(fromDate.getTime());
        }
    }

	public static void main(String[] args) throws Exception {

    	String envFile ="conf/loginInfo.json";
    	String metaFile ="conf/metadataInfo.json";
    	
		PropertyConfigurator.configure("./conf/log4j.properties");
		
		Map<String,String> argMap = getArgMap(args);
		
    	BuildPackage sample = new BuildPackage(metaFile);
    	
    	if (argMap.containsKey("envFile")) {
    		envFile=argMap.get("envFile");
    		logger.info("Param=envFile");
    	}
    	
    	if (argMap.containsKey("metaFile"))
    		envFile=argMap.get("metaFile");    	
    	
		logger.info("Metadata Configuration file="+metaFile);
		logger.info("Environment Configuration file="+envFile);
		
        sample.run(envFile);
    }
    public BuildPackage( String metaFile) {
    	mapper = new ObjectMapper();
    	mapper.setDateFormat(df);
    	try {
			metaInfo= mapper.readValue(new File(metaFile), metadataInfo.class);
			//mapper.writeValue(new File("metadataInfo2.json"), metaInfo);
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
    
    private void run(String envFile) throws Exception {
    	File folder;
    	String outputDirectory;
    	
    	logger.info("Start");
    	loginUtil lu = new loginUtil();
	
     	metadataConnection =  lu.getMetadataConnection(envFile);

     	outputDirectory=lu.getOutputDirectory();  
    	sessionId = metadataConnection.getConfig().getSessionId();
    	uri=metadataConnection.getConfig().getRestEndpoint();

    	logger.info("Service Endpoint="+metadataConnection.getConfig().getServiceEndpoint());
    	logger.info("Rest Endpoint="+metadataConnection.getConfig().getRestEndpoint());

    	logger.debug("username="+metadataConnection.getConfig().getUsername());
    	logger.info("sessionId="+sessionId);
    	folder = new File(outputDirectory+"/package/");
     	folder.mkdirs();     	
    	
    	DescribeMetadataResult metadataresult = metadataConnection.describeMetadata((Double.valueOf(lu.getAPIVersion())));
    	logger.debug("metadataresult="+metadataresult);
    	
    	File DescribeMetadatafile = new File("package/DescribeMetadata.xml");
    	FileOutputStream metadatadescribefos=new FileOutputStream(DescribeMetadatafile);
    	XmlOutputStream metadatadescribexout=new XmlOutputStream(metadatadescribefos,true);
    	metadatadescribexout.setPrefix("", "http://soap.sforce.com/2006/04/metadata");
    	metadatadescribexout.setPrefix("xsi", "http://www.w3.org/2001/XMLSchema-instance");
    		
    	metadatadescribexout.startDocument();
    	metadataresult.write(new QName("http://soap.sforce.com/2006/04/metadata","Metadata"),metadatadescribexout , typeMapper);

    	metadatadescribexout.endDocument();
    	metadatadescribexout.close();
    	metadatadescribefos.close();
    	
    	writeMetadata(outputDirectory+"/package/package.xml",packageType.Updated);
    	writeMetadata(outputDirectory+"/package/packageAll.xml",packageType.All); 
    	writeMetadata(outputDirectory+"/package/packageNew.xml",packageType.New);            
        logger.info("End");
    }
    
    /*private PackageTypeMembers getPackageTypeMembers (MetadataConnection metadataConnection, ListMetadataQuery query, Calendar fromDate, double versionApi ) {
    	PackageTypeMembers packTypeMembers = new PackageTypeMembers() ;
		packTypeMembers.setName(query.getType());
		Calendar memberDate;
		
		FileProperties[] lmr;
		try {
			lmr = metadataConnection.listMetadata(new ListMetadataQuery[] {query }, versionApi);
		
			List<String> members = new ArrayList<String>();
			if (lmr != null) {
		    	for (FileProperties n : lmr) {
		    		memberDate = n.getLastModifiedDate();
		    		if (memberDate.after(metaInfo.fromDate)) {
		    			members.add(n.getFullName());
		    		}
		    	}
		    	packTypeMembers.setMembers( members.toArray(new String[members.size()]));
			}
		} catch (ConnectionException e) {
			System.err.println("Unknow type :"+ query.getType());
			e.printStackTrace();
		}
		return packTypeMembers;
    	
    }*/
    
    private List<String> getMembers(String soqlQuery, String folderField, String nameField){
    	return getMembers( soqlQuery,  folderField,  nameField,"");
    }
    
    private List<String> getMembers(String soqlQuery, String folderField, String nameField, String folderObject){
    	List<String> members = new ArrayList<String>();
		JSONObject json;
		JSONArray records;
		String folder;
		String name ;

    	json = new JSONObject (restUtil.restQuery(sessionId, uri, soqlQuery));
		records =json.getJSONArray("records");
		
		for ( Object record : records) {
			JSONObject jrecord = (JSONObject) record;
			String member;
			if (folderObject != "") { 
				folder = ((JSONObject) jrecord.get(folderObject)).getString(folderField).replace(" ", "_");
			}else {
				folder = jrecord.getString(folderField).replace(" ", "_");
			}
			
			name = (nameField != "")?"/"+jrecord.getString(nameField):"";
			member = folder + name;
			members.add(member);				
		}
		return members;
    }
    
    private static Map<String, String> getArgMap(String[] args) {
        //every arg is a name=value config setting, save it in a map of name/value pairs
    	
        Map<String,String> argMap = new HashMap<String,String>();
        for (int i = 0; i < args.length; i++) {
        	logger.info("arg["+i+"]="+args[i]);
            String[] argArray = args[i].split("="); //$NON-NLS-1$

            if (argArray.length == 2) {
                argMap.put(argArray[0], argArray[1]);
            }
        }
        return argMap;
    }
    private void  writeMetadata (String packageFileName, packageType type) throws IOException  {
    	logger.info("writeMetadata("+packageFileName+","+type+"): Start");
    	
    	List<PackageTypeMembers> PackageType = new ArrayList<PackageTypeMembers>();
    	
    	String queryClause ="";
    	
    	
    	switch (type){
    	case All :
    		break;
    	case New:
    		queryClause = " and CreatedDate > "+ metaInfo.getStringDate();
    		break;
    	case Updated:
    		queryClause = " and LastModifiedDate > "+ metaInfo.getStringDate();
    		break;
    	}
    	
    	
    	for ( ListMetadataQuery query : metaInfo.queries) {
    		
    		logger.info("process query ="+query.getType() + " / " + query.getFolder());

    			List<String> members = new ArrayList<String>();

    			PackageTypeMembers packTypeMembers;
    			switch (query.getType() ) {
    			case "Report" : 
    				//get Folder
    				packTypeMembers = new PackageTypeMembers() ;
    	    		packTypeMembers.setName(query.getType());
    				members = getMembers("SELECT DeveloperName,Type FROM Folder WHERE Type = 'Report' AND DeveloperName != null " + queryClause, "DeveloperName","");
    	    		//members = getMembers("SELECT DeveloperName,Type FROM Folder WHERE Type = 'Report' AND DeveloperName != null  ", "DeveloperName","");
    				logger.debug("members folder=" +members);
    				if (members.size() > 0) {
	    				packTypeMembers.setMembers( members.toArray(new String[members.size()]));
	    				PackageType.add(packTypeMembers);
    				}
    				
    				//getReport
    				packTypeMembers = new PackageTypeMembers() ;
    	    		packTypeMembers.setName(query.getType());
    				members = getMembers("SELECT DeveloperName,FolderName FROM Report where FolderName!= null"  + queryClause,"FolderName", "DeveloperName");
    				logger.debug("members folder=" +members);
    				if (members.size() > 0) {
	    				packTypeMembers.setMembers( members.toArray(new String[members.size()]));
	    				PackageType.add(packTypeMembers);
    				}
    				break;
    				
    			case "EmailTemplate":

    				//get Folder
    				packTypeMembers = new PackageTypeMembers() ;
    	    		packTypeMembers.setName(query.getType());
    				members = getMembers("SELECT DeveloperName,Type FROM Folder WHERE Type = 'Email' AND DeveloperName != null "+ queryClause, "DeveloperName","");
    				logger.debug("members folder Email=" +members);
    				if (members.size() > 0) {
	    				packTypeMembers.setMembers( members.toArray(new String[members.size()]));
	    				PackageType.add(packTypeMembers);
    				}
    				
    				//get EmailTemplate
    				packTypeMembers = new PackageTypeMembers() ;
    	    		packTypeMembers.setName(query.getType());
    				members = getMembers("SELECT DeveloperName,Folder.developerName FROM EmailTemplate where Folder.developerName!= null"+ queryClause,"DeveloperName", "DeveloperName","Folder");
    				logger.debug("members  EmailTemplate=" +members);
    				if (members.size() > 0) {
	    				packTypeMembers.setMembers( members.toArray(new String[members.size()]));
	    				PackageType.add(packTypeMembers);
    				}
    				break;
    				
    			case "Dashboard":

    				//get Folder
    				packTypeMembers = new PackageTypeMembers() ;
    	    		packTypeMembers.setName(query.getType());
    				members = getMembers("SELECT DeveloperName,Type FROM Folder WHERE Type = 'Dashboard' AND DeveloperName != null "+ queryClause, "DeveloperName","");
    				logger.debug("members folder Email=" +members);
    				if (members.size() > 0) {
	    				packTypeMembers.setMembers( members.toArray(new String[members.size()]));
	    				PackageType.add(packTypeMembers);
    				}
    				
    				//get EmailTemplate
    				packTypeMembers = new PackageTypeMembers() ;
    	    		packTypeMembers.setName(query.getType());
    				members = getMembers("SELECT DeveloperName,Folder.developerName FROM Dashboard where Folder.developerName!= null "+ queryClause,"DeveloperName", "DeveloperName","Folder");
    				logger.debug("members  EmailTemplate=" +members);
    				if (members.size() > 0) {
	    				packTypeMembers.setMembers( members.toArray(new String[members.size()]));
	    				PackageType.add(packTypeMembers);
    				}
    				break;
    			default:
    				packTypeMembers = new PackageTypeMembers() ;
    				packTypeMembers=Util.getPackageTypeMembers(metadataConnection,query,metaInfo.fromDate,type.toString(), metaInfo.version);
    				logger.debug("size=" +packTypeMembers.getMembers().length);
    				if (packTypeMembers.getMembers().length >0) {
    					PackageType.add(packTypeMembers);
    				}
    				break;
    			}  		
    	}
    	
    	Package packagesf =new Package();
    	packagesf.setTypes(PackageType.toArray(new PackageTypeMembers[PackageType.size()]));
    	packagesf.setVersion(Double.toString(metaInfo.version));
    	packagesf.setFullName("Metadata for type "+type.toString() +" since " +metaInfo.getStringDate());
    	
    	File packagefile = new File(packageFileName);
    	FileOutputStream fos=new FileOutputStream(packagefile);
    	XmlOutputStream xout=new XmlOutputStream(fos,true);
    	xout.setPrefix("", "http://soap.sforce.com/2006/04/metadata");
    	xout.setPrefix("xsi", "http://www.w3.org/2001/XMLSchema-instance");
    		
    	xout.startDocument();
    	packagesf.write(new QName("http://soap.sforce.com/2006/04/metadata","Package"),xout , typeMapper);
    	xout.endDocument();
        xout.close();
        fos.close();

    	logger.info("writeMetadata("+packageFileName+","+type+"): End");
    }
    
}   
    
 
