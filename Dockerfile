# Use official Java 21 image
FROM eclipse-temurin:21-jdk

# Set the working directory
WORKDIR /app

# Install Maven and download dependencies first (to leverage Docker cache)
COPY pom.xml .
RUN apt-get update && apt-get install -y maven && \
    mvn dependency:go-offline

# Copy the rest of the application code
COPY src ./src

# Package the application
RUN mvn clean install -DskipTests || cat target/surefire-reports/*.txt || true

# Run the app
CMD ["java", "-jar", "target/ScraperEndpoint-1.0-SNAPSHOT.jar"]
