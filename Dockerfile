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

ENV DEBIAN_FRONTEND=noninteractive
ENV CHROME_BIN=/opt/chrome/chrome

# Install minimal dependencies for headless Chrome
RUN apt-get update && apt-get install -y \
    wget \
    curl \
    unzip \
    libnss3 \
    libx11-xcb1 \
    libxcomposite1 \
    libxdamage1 \
    libxrandr2 \
    libasound2 \
    libatk-bridge2.0-0 \
    libgtk-3-0 \
    libgbm-dev \
    --no-install-recommends && \
    rm -rf /var/lib/apt/lists/*

# Download and install Chrome for Testing
RUN wget -O chrome-linux64.zip https://storage.googleapis.com/chrome-for-testing-public/126.0.6478.114/linux64/chrome-linux64.zip && \
    unzip chrome-linux64.zip && \
    mv chrome-linux64 /opt/chrome && \
    ln -s /opt/chrome/chrome /usr/bin/google-chrome && \
    rm chrome-linux64.zip

# Set workdir
WORKDIR /app

# Copy your app
COPY . .

# Make Maven wrapper executable
RUN chmod +x mvnw

# Build project
RUN ./mvnw clean install -DskipTests

# Run app
CMD ["java", "-jar", "target/ScraperEndpoint-1.0-SNAPSHOT.jar"]
