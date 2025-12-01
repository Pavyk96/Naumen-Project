FROM eclipse-temurin:21-jdk-alpine

WORKDIR /target

COPY target/project-0.0.1-SNAPSHOT.jar /target/project.jar

ENTRYPOINT ["java", "-jar", "/target/project.jar"]
