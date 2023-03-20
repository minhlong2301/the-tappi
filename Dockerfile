FROM openjdk:19
EXPOSE 8080
ADD target/spring-boot-nfc.jar spring-boot-nfc.jar
ENTRYPOINT ["java","-jar","/spring-boot-nfc.jar"]
