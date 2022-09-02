FROM openjdk:12
VOLUME /tmp
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} Events.jar
ENTRYPOINT ["java","-jar","/Events.jar"]