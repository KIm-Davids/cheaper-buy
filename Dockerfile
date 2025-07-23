## Use official Java 21 image
#FROM eclipse-temurin:21-jdk
#
## Set the working directory
#WORKDIR /app
#
## Install Maven and download dependencies first (to leverage Docker cache)
#COPY pom.xml .
#RUN apt-get update && apt-get install -y maven && \
#    mvn dependency:go-offline
#
## Copy the rest of the application code
#COPY src ./src
#
## Package the application
#RUN mvn clean install -DskipTests || cat target/surefire-reports/*.txt || true
#
## Run the app
#CMD ["java", "-jar", "target/ScraperEndpoint-1.0-SNAPSHOT.jar"]


FROM eclipse-temurin:17-jdk

# Environment setup
ENV DEBIAN_FRONTEND=noninteractive

# Install Chrome dependencies and headless Chrome
RUN apt-get update && apt-get install -y \
    curl \
    unzip \
    gnupg \
    ca-certificates \
    fonts-liberation \
    libappindicator3-1 \
    libasound2 \
    libatk-bridge2.0-0 \
    libatk1.0-0 \
    libcups2 \
    libdbus-1-3 \
    libgdk-pixbuf2.0-0 \
    libnspr4 \
    libnss3 \
    libx11-xcb1 \
    libxcomposite1 \
    libxdamage1 \
    libxrandr2 \
    xdg-utils \
    libgbm-dev \
    wget \
    --no-install-recommends && \
    rm -rf /var/lib/apt/lists/*

# Install Chrome directly from stable URL
RUN wget https://dl.google.com/linux/direct/google-chrome-stable_current_amd64.deb && \
    apt install -y ./google-chrome-stable_current_amd64.deb && \
    rm google-chrome-stable_current_amd64.deb

ENV CHROME_BIN=/usr/bin/google-chrome

# Set working directory
WORKDIR /app

# Copy code
COPY . .

# Give permission to mvnw
RUN chmod +x mvnw

# Build the project
RUN ./mvnw clean install -DskipTests

# Run the app
CMD ["java", "-jar", "target/ScraperEndpoint-1.0-SNAPSHOT.jar"]
