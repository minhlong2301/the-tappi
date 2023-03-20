FROM openjdk:19
ADD target/spring-boot-nfc.jar spring-boot-nfc.jar
ENTRYPOINT ["java","-jar","/spring-boot-nfc.jar"]
