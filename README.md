# Build Metadata tool

To build Metadata tool:

    mvn clean package -DskipTests
    
The build will include the appropriate eclipse swt jar by detecting the operating system type.  If you would like to manually specify the eclipse swt jar, take a look at the pom.xml file to see a full list of available profiles.

    
# Execute Metadata tool 


This tools generates package.xml file with metadata component modified since a reference date.

Configuration files :
    loginInfo.json : this file contains information about your salesforce organisation 
        {
            "username":"yourusername",
            "password":"yourpassword",
            "apiVersion":"38.0",
            "url":"https://test.salesforce.com/services/Soap/u/38.0"
         } 
     metadataInfo.json : this file contains information about metadata you need extract in package.xml :
        queries= list of component you need to extract,
            [
                {"type":"CustomField"},
                {"type":"CustomLabel"},
                {"type":"CustomObject"},
                {"type":"WorkflowRule"},
                {"folder":"*","type":"Report"},
                {"folder":"*","type":"Dashboard"}
            ]
         fromDate = Reference date. All metadata components change since this date are extracted in package.xml
         version = Salesforce API version 

To run Metadata tool from the command line, use the command from target folder:

    java -jar salesforceMetadata-0.1.1-shade.jar

    mvn exec:java -Dexec.mainClass=com.jmc.force.metadata.BuildPackage
    
    
    java -jar target/salesforceMetadata-0.1.1-shade.jar 
    
    java -cp salesforceMetadata-0.1.1-shade.jar com.jmc.force.metadata.BuildPackage
    
To build Data Dictionary : 
    
    java -cp salesforceMetadata-0.1.1-shade.jar com.jmc.force.metadata.BuildSpecFile


