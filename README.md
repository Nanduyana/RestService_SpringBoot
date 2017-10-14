# Version ```0.0.1```

# Version ```0.0.2-SNAPSHOT``` In_Progress

# Spring "Restservice"

This is a sample Java / Maven / Spring Boot (version 1.5.6) application that can be used as a starter for creating a restservice.

this service also tells us the ```time it takes to process the request```

## How to Run 

This application is packaged as a war which has Tomcat 8 embedded. No Tomcat or JBoss installation is necessary. You can run it using the ```java -jar``` command by passing the war file
 
* Make sure you are using JDK 1.8 and Maven 3.x
* You can build the project and run the tests by running ```mvn clean package``` / ```mvn clean install```
* Once successfully built, you can run the service by one of these two methods:
```
        java -jar -Dspring.profiles.active=dev target/search-directories.war
or
        mvn spring-boot:run -Pdev - for dev env (for prod: -PProd)
        

## About the Service
this service returns you the list of filenames and paths that are available in the given Directory.

### Get information about service.

```
http://localhost:8090/welcome
http://localhost:8091/search/rest/directory?word=Nandu - searches the word Nandu in the given directory
http://localhost:8091/search/rest/directory?word=Test_Nandu - this searches the words Test and Nandu in the given directory.
```
for any changes in the searching with respect to words from ```_``` to ``` ``` (space) we can change in application.properties file 

# Attaching to the app remotely from your IDE

Run the service with these command line options:

```
mvn spring-boot:run -Drun.jvmArguments="-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=5005"
or
java -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005 -Dspring.profiles.active=test -Ddebug -jar target/search-directories.war
```

# Logging LogBack is used for this
logs will be rolled out based on the size of the file given in logback.xml

# https://github.com/Nanduyana/Spring-Boot.git - GitRepository

# run for site generation 

mvn site - this generates with test coverate report in target folder, we can deploy the same else where for remote access





