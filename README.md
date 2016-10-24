# Build Metadata tool

To build Metadata tool:

    mvn clean package -DskipTests
    
The build will include the appropriate eclipse swt jar by detecting the operating system type.  If you would like to manually specify the eclipse swt jar, take a look at the pom.xml file to see a full list of available profiles.

    
# Execute Metadata tool 


    
To run Metadata tool from the command line, use the command:

    mvn exec:java -Dexec.mainClass=com.jmc.force.buildMetadata


