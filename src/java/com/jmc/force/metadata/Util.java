package com.jmc.force.metadata;

//import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.List;

import org.apache.log4j.Logger;

import com.sforce.soap.metadata.FileProperties;
import com.sforce.soap.metadata.ListMetadataQuery;
import com.sforce.soap.metadata.MetadataConnection;
import com.sforce.soap.metadata.PackageTypeMembers;
import com.sforce.ws.ConnectionException;

public class Util {     
	private static Logger logger = Logger.getLogger(Util.class);
	
	static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");		
	static SimpleDateFormat sdtf = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");	
	
	 public static PackageTypeMembers getPackageTypeMembers (MetadataConnection metadataConnection,ListMetadataQuery query, Calendar refDate, String dateType ,double versionApi, boolean managed, boolean withDate ) {
    	PackageTypeMembers packTypeMembers = new PackageTypeMembers() ;
		packTypeMembers.setName(query.getType());
		Calendar memberDate;
		
		
		FileProperties[] lmr;
		try {
			lmr = metadataConnection.listMetadata(new ListMetadataQuery[] {query },  versionApi);
		
			List<String> members = new ArrayList<String>();
			if (lmr != null) {
		    	for (FileProperties n : lmr) {
		    		switch (dateType ) {
		    		case "Updated":
		    			memberDate = n.getLastModifiedDate();
		    			break;
		    		case "New":
		    			memberDate = n.getCreatedDate();
		    			break;
		    		default:		
		    			memberDate =  new GregorianCalendar(2050,1,28,13,24,56);;
		    			break;		
		    		}
		    		logger.debug("FileProperties="+n.toString());
		    		
		    		if (memberDate.after(refDate) && ( managed || n.getManageableState() == com.sforce.soap.metadata.ManageableState.unmanaged  ||  n.getManageableState() == null ) ) {
		    			String member;
		    			member = n.getFullName();
		    			if (withDate) {
		    				member += "|" +sdtf.format(n.getCreatedDate().getTime()) + "|" +n.getCreatedByName(); 
		    				member += "|" +sdtf.format(n.getLastModifiedDate().getTime()) + "|" +n.getLastModifiedByName(); 
		    				//member= "<name>"+member+"</member>"+"<date>"+sdtf.format(n.getLastModifiedDate().getTime())+"</date>";
		    			}
		    			members.add(member);
		    		}
		    	}
		    	//+" "+ sdf.format(memberDate.getTime()
		    	
		    	Collections.sort(members);
		    	
		    	packTypeMembers.setMembers( members.toArray(new String[members.size()]));
			}
		} catch (ConnectionException e) {
			System.err.println("Unknow type :"+ query.getType());
			e.printStackTrace();
		}
		return packTypeMembers;
    	
    }
}