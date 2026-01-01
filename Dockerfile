FROM openjdk:17-jdk-alpine

WORKDIR /app

# Copy pom.xml and download dependencies
COPY target/SaiMessAdminPanel.jar app.jar

EXPOSE 8080

# Run the app
CMD ["java", "-jar", "app.jar"]
