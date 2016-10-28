package com.jmc.force.metadata;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import javax.xml.namespace.QName;

import org.codehaus.jackson.map.ObjectMapper;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
//import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import com.jmc.force.rest.restUtil;
import com.sforce.soap.metadata.*;
import com.sforce.soap.metadata.Package;
//import com.sforce.soap.metadata.Connection;
import com.sforce.ws.ConnectionException;
import com.sforce.ws.bind.TypeMapper;
//import com.sforce.ws.parser.XmlInputStream;
import com.sforce.ws.parser.XmlOutputStream;

public class buildMetadata {
	
    private MetadataConnection metadataConnection;
    private ObjectMapper mapper;
    private TypeMapper typeMapper=new TypeMapper();
    private String sessionId;
    private String uri;
    private static metadataInfo metaInfo  ;
    private static final  String dateFormat = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
    private static final  DateFormat df = new SimpleDateFormat(dateFormat); 
    
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
    	buildMetadata sample = new buildMetadata();
        sample.run();
    }
    public buildMetadata() {
    	mapper = new ObjectMapper();
    	mapper.setDateFormat(df);
    	try {
			metaInfo= mapper.readValue(new File("metadataInfo.json"), metadataInfo.class);
			//mapper.writeValue(new File("metadataInfo2.json"), metaInfo);
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
    
    
    private void run() throws Exception {
     	this.metadataConnection = MetadataLoginUtil.login();
    	sessionId = metadataConnection.getConfig().getSessionId();
    	uri=metadataConnection.getConfig().getRestEndpoint();

    	System.out.println("Service Endpoint="+metadataConnection.getConfig().getServiceEndpoint());
    	System.out.println("Rest Endpoint="+metadataConnection.getConfig().getRestEndpoint());

    	//System.out.println("username="+metadataConnection.getConfig().getUsername());
    	System.out.println("sessionId="+sessionId);
    	
      	
    	List<PackageTypeMembers> PackageType = new ArrayList<PackageTypeMembers>();
    	for ( ListMetadataQuery query : metaInfo.queries) {
    		System.out.println("process query ="+query.getType() + " / " + query.getFolder());

    			List<String> members = new ArrayList<String>();

    			PackageTypeMembers packTypeMembers;
    			
    			switch (query.getType() ) {
    			case "Report" : 
    				//get Folder
    				packTypeMembers = new PackageTypeMembers() ;
    	    		packTypeMembers.setName(query.getType());
    				members = getMembers("SELECT DeveloperName,Type FROM Folder WHERE Type = 'Report' AND DeveloperName != null and LastModifiedDate > "+ metaInfo.getStringDate(), "DeveloperName","");
    	    		//members = getMembers("SELECT DeveloperName,Type FROM Folder WHERE Type = 'Report' AND DeveloperName != null  ", "DeveloperName","");
    				//System.out.println("members folder=" +members);
    				if (members.size() > 0) {
	    				packTypeMembers.setMembers( members.toArray(new String[members.size()]));
	    				PackageType.add(packTypeMembers);
    				}
    				
    				//getReport
    				packTypeMembers = new PackageTypeMembers() ;
    	    		packTypeMembers.setName(query.getType());
    				members = getMembers("SELECT DeveloperName,FolderName FROM Report where FolderName!= null and LastModifiedDate > "+ metaInfo.getStringDate(),"FolderName", "DeveloperName");
    				//System.out.println("members folder=" +members);
    				if (members.size() > 0) {
	    				packTypeMembers.setMembers( members.toArray(new String[members.size()]));
	    				PackageType.add(packTypeMembers);
    				}
    				break;
    				
    			case "EmailTemplate":

    				//get Folder
    				packTypeMembers = new PackageTypeMembers() ;
    	    		packTypeMembers.setName(query.getType());
    				members = getMembers("SELECT DeveloperName,Type FROM Folder WHERE Type = 'Email' AND DeveloperName != null and LastModifiedDate > "+ metaInfo.getStringDate(), "DeveloperName","");
    				//System.out.println("members folder Email=" +members);
    				if (members.size() > 0) {
	    				packTypeMembers.setMembers( members.toArray(new String[members.size()]));
	    				PackageType.add(packTypeMembers);
    				}
    				
    				//get EmailTemplate
    				packTypeMembers = new PackageTypeMembers() ;
    	    		packTypeMembers.setName(query.getType());
    				members = getMembers("SELECT DeveloperName,Folder.developerName FROM EmailTemplate where Folder.developerName!= null and LastModifiedDate > "+ metaInfo.getStringDate(),"DeveloperName", "DeveloperName","Folder");
    				//System.out.println("members  EmailTemplate=" +members);
    				if (members.size() > 0) {
	    				packTypeMembers.setMembers( members.toArray(new String[members.size()]));
	    				PackageType.add(packTypeMembers);
    				}
    				break;
    				
    			case "Dashboard":

    				//get Folder
    				packTypeMembers = new PackageTypeMembers() ;
    	    		packTypeMembers.setName(query.getType());
    				members = getMembers("SELECT DeveloperName,Type FROM Folder WHERE Type = 'Dashboard' AND DeveloperName != null and LastModifiedDate > "+ metaInfo.getStringDate(), "DeveloperName","");
    				//System.out.println("members folder Email=" +members);
    				if (members.size() > 0) {
	    				packTypeMembers.setMembers( members.toArray(new String[members.size()]));
	    				PackageType.add(packTypeMembers);
    				}
    				
    				//get EmailTemplate
    				packTypeMembers = new PackageTypeMembers() ;
    	    		packTypeMembers.setName(query.getType());
    				members = getMembers("SELECT DeveloperName,Folder.developerName FROM Dashboard where Folder.developerName!= null and LastModifiedDate > "+ metaInfo.getStringDate(),"DeveloperName", "DeveloperName","Folder");
    				//System.out.println("members  EmailTemplate=" +members);
    				if (members.size() > 0) {
	    				packTypeMembers.setMembers( members.toArray(new String[members.size()]));
	    				PackageType.add(packTypeMembers);
    				}
    				break;
    			default:
    				packTypeMembers = new PackageTypeMembers() ;
    				packTypeMembers=getPackageTypeMembers(query);
    				//System.out.println("size=" +packTypeMembers.getMembers().length);
    				if (packTypeMembers.getMembers().length >0) {
    					PackageType.add(packTypeMembers);
    				}
    				break;
    			}  		
    	}
    	
    	Package packagesf =new Package();
    	packagesf.setTypes(PackageType.toArray(new PackageTypeMembers[PackageType.size()]));
    	packagesf.setVersion(Double.toString(metaInfo.version));
    	packagesf.setFullName("fullName");
    	
    	File packagefile = new File("package.xml");
    	FileOutputStream fos=new FileOutputStream(packagefile);
    	XmlOutputStream xout=new XmlOutputStream(fos,true);
    	xout.setPrefix("", "http://soap.sforce.com/2006/04/metadata");
    	xout.setPrefix("xsi", "http://www.w3.org/2001/XMLSchema-instance");
    		
    	xout.startDocument();
    	packagesf.write(new QName("http://soap.sforce.com/2006/04/metadata","Package"),xout , typeMapper);
    	xout.endDocument();
        xout.close();
        fos.close();

    }
    
    private PackageTypeMembers getPackageTypeMembers (ListMetadataQuery query ) {
    	PackageTypeMembers packTypeMembers = new PackageTypeMembers() ;
		packTypeMembers.setName(query.getType());
		Calendar memberDate;
		
		FileProperties[] lmr;
		try {
			lmr = metadataConnection.listMetadata(new ListMetadataQuery[] {query },  metaInfo.version);
		
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
    	
    }
    
    private List<String> getMembers(String soqlQuery, String folderField, String nameField){
    	return getMembers( soqlQuery,  folderField,  nameField,"");
    }
    
    private List<String> getMembers(String soqlQuery, String folderField, String nameField, String folderObject){
    	List<String> members = new ArrayList<String>();
		JSONObject json;
		JSONArray records;
		String folder;
		String name ;

    	json = new JSONObject (restUtil.restGet(sessionId, uri, soqlQuery));
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

    
}   
    
 
