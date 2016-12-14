package com.jmc.force.metadata;

//import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.List;

import com.sforce.soap.metadata.FileProperties;
import com.sforce.soap.metadata.ListMetadataQuery;
import com.sforce.soap.metadata.MetadataConnection;
import com.sforce.soap.metadata.PackageTypeMembers;
import com.sforce.ws.ConnectionException;

public class Util {             
	static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");	 
	 public static PackageTypeMembers getPackageTypeMembers (MetadataConnection metadataConnection,ListMetadataQuery query, Calendar refDate, String dateType ,double versionApi ) {
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
		    		
		    		if (memberDate.after(refDate)) {
		    			members.add(n.getFullName());
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
