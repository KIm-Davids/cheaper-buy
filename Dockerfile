# Start from a Maven image to build the app
FROM maven:3.9.6-eclipse-temurin-21 AS builder

WORKDIR /app

# Copy the project files
COPY pom.xml .
COPY src ./src

# Build the application
RUN mvn clean install -DskipTests

# --- Production image ---
FROM eclipse-temurin:21-jdk

WORKDIR /app

# Copy only the jar from the previous build
COPY --from=builder /app/target/ScraperEndpoint-0.0.1-SNAPSHOT.jar app.jar

# Run the jar
CMD ["java", "-jar", "app.jar"]
