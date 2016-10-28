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
     metadataInfo.json : this file contains informaiton about metadata you need extract in package.xml
         queries= list of component you need to extract,
         fromDate = Reference date. All metadata component change since this date are extracted in package.xml
         version = Salesforce API version 

To run Metadata tool from the command line, use the command:

    mvn exec:java -Dexec.mainClass=com.jmc.force.metadata.buildMetadata


