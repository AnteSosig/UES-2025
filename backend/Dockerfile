# Multi-stage build for Spring Boot application
FROM maven:3.8.7-eclipse-temurin-17 AS build

WORKDIR /app

# Copy pom.xml and download dependencies
COPY projekt/pom.xml .
RUN mvn dependency:go-offline -B

# Copy source code and build
COPY projekt/src ./src
RUN mvn clean package -DskipTests

# Runtime stage
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Copy the built JAR file from build stage
COPY --from=build /app/target/*.jar app.jar

# Expose the application port
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
