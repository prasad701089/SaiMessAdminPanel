# =========================
# 1️⃣ Build Stage
# =========================
FROM maven:3.9.6-eclipse-temurin-17 AS build

WORKDIR /app

# Copy pom.xml and download dependencies
COPY pom.xml .
RUN mvn dependency:go-offline

# Copy source code
COPY src ./src

# Build the application
RUN mvn clean package -DskipTests


# =========================
# 2️⃣ Runtime Stage
# =========================
FROM eclipse-temurin:17-jre

WORKDIR /app

# Copy jar from build stage
COPY --from=build /app/target/*.jar app.jar

# Expose Render default port
EXPOSE 8080

# Run the app
CMD ["java", "-jar", "app.jar"]
