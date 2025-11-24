FROM eclipse-temurin:21-jdk-jammy
WORKDIR /app
# Copy the built JAR from the host machine to the image
COPY target/restapi-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]