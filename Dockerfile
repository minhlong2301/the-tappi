#FROM openjdk:19
#ADD target/spring-boot-nfc.jar spring-boot-nfc.jar
#ENTRYPOINT ["java","-jar","/spring-boot-nfc.jar"]

FROM openjdk:19
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
