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
Use Selenium base image with Chrome already installed

#
# FROM selenium/standalone-chrome:119.0
#
# USER root
#
# # Install Java and Maven
# RUN apt-get update && apt-get install -y \
#     curl \
#     unzip \
#     maven \
#     openjdk-17-jdk \
#     --no-install-recommends && \
#     apt-get clean && \
#     rm -rf /var/lib/apt/lists/*
#
# # Set working directory
# WORKDIR /app
#
# # Copy source files into container
# COPY . .
#
# # Make Maven wrapper executable (if used)
# RUN chmod +x mvnw || true
#
# # Build project using Maven wrapper if present, fallback to system mvn
# RUN if [ -f "./mvnw" ]; then ./mvnw clean install -DskipTests; else mvn clean install -DskipTests; fi
#
# RUN echo "✅ All packages installed successfully!"
#
# # Define command to run the app
# CMD ["java", "-jar", "target/ScraperEndpoint-1.0-SNAPSHOT.jar"]
