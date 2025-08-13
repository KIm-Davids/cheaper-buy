# # Use official Java 21 image
# FROM eclipse-temurin:21-jdk
#
# # Set the working directory
# WORKDIR /app
#
# # Install Maven and download dependencies first (to leverage Docker cache)
# COPY pom.xml .
# RUN apt-get update && apt-get install -y maven && \
#    mvn dependency:go-offline
#
# # Copy the rest of the application code
# COPY src ./src
#
# # Package the application
# RUN mvn clean install -DskipTests || cat target/surefire-reports/*.txt || true
#
# # Run the app
# CMD ["java", "-jar", "target/ScraperEndpoint-1.0-SNAPSHOT.jar"]
# Use Selenium base image with Chrome already installed

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

# 1️⃣ Start from Debian (or Ubuntu) base
FROM debian:bullseye-slim

# 2️⃣ Install Java, Maven, Firefox, and dependencies
RUN apt-get update && apt-get install -y \
    openjdk-21-jdk \
    maven \
    firefox-esr \
    wget \
    tar \
    --no-install-recommends && \
    apt-get clean && rm -rf /var/lib/apt/lists/*

# 3️⃣ Install Geckodriver (for Firefox Selenium)
RUN wget https://github.com/mozilla/geckodriver/releases/download/v0.35.0/geckodriver-v0.35.0-linux64.tar.gz && \
    tar -xvzf geckodriver-v0.35.0-linux64.tar.gz && \
    mv geckodriver /usr/local/bin/ && \
    rm geckodriver-v0.35.0-linux64.tar.gz

# 4️⃣ Set working directory
WORKDIR /app

# 5️⃣ Copy Maven config and pre-fetch dependencies
COPY pom.xml .
RUN mvn dependency:go-offline

# 6️⃣ Copy source code
COPY src ./src

# 7️⃣ Build the project (skip tests)
RUN mvn clean package -DskipTests

# 8️⃣ Run the JAR
CMD ["java", "-jar", "target/ScraperEndpoint-1.0-SNAPSHOT.jar"]
