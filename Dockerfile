FROM openjdk:19
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar
ENV TZ=Asia/Ho_Chi_Minh
ENTRYPOINT ["java","-jar","/app.jar"]
