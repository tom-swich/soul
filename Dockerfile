FROM openjdk:24-jdk-slim
LABEL authors="vinny"

WORKDIR /app
COPY target/monolito-0.0.1-SNAPSHOT.jar .
EXPOSE 8080

CMD ["java", "-Dspring.profiles.active=prod", "-jar", "monolito-0.0.1-SNAPSHOT.jar"]