# Getting Started

This is a spring boot application that exposes Rest endpoint /coding/exercise/quiz

## How to run?
	mvn spring-boot:run
    (OR)
    mvn spring-boot:run -Dspring-boot.run.arguments=--server.port=8085 (to customize port)
	
## Required softwares
	JDK 1.8+
	Maven

## Secuirty
  The endpoints are not secured at this point
  
## How to test?
	Open browser and enter http://localhost:8080/coding/exercise/quiz
	(OR)
	curl 'http://localhost:8080/coding/exercise/quiz'
	(OR)
	curl 'http://localhost:8080/coding/exercise/quiz' | jq  (formatted json)
  


### Reference Documentation
For further reference, please consider the following sections:

* [Official Apache Maven documentation](https://maven.apache.org/guides/index.html)
* [Spring Boot Maven Plugin Reference Guide](https://docs.spring.io/spring-boot/docs/2.5.2/maven-plugin/reference/html/)
* [Create an OCI image](https://docs.spring.io/spring-boot/docs/2.5.2/maven-plugin/reference/html/#build-image)

