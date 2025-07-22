# Use official Java 21 image
FROM eclipse-temurin:21-jdk

# Set the working directory
WORKDIR /app

# Install required dependencies for Chrome and Maven
RUN apt-get update && apt-get install -y \
    maven \
    wget \
    curl \
    unzip \
    gnupg2 \
    fonts-liberation \
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
    --no-install-recommends

# Install Google Chrome
RUN wget https://dl.google.com/linux/direct/google-chrome-stable_current_amd64.deb && \
    apt install -y ./google-chrome-stable_current_amd64.deb && \
    rm google-chrome-stable_current_amd64.deb

# Install ChromeDriver
RUN CHROMEDRIVER_VERSION=$(curl -sS https://chromedriver.storage.googleapis.com/LATEST_RELEASE) && \
    wget -N https://chromedriver.storage.googleapis.com/$CHROMEDRIVER_VERSION/chromedriver_linux64.zip && \
    unzip chromedriver_linux64.zip && \
    mv chromedriver /usr/bin/chromedriver && \
    chmod +x /usr/bin/chromedriver && \
    rm chromedriver_linux64.zip

# Set environment variables for Selenium
ENV CHROME_BIN="/usr/bin/google-chrome"
ENV CHROMEDRIVER="/usr/bin/chromedriver"

# Copy Maven config
COPY pom.xml .

# Pre-download dependencies
RUN mvn dependency:go-offline

# Copy the rest of the source code
COPY src ./src

# Build the application
RUN mvn clean install -DskipTests || cat target/surefire-reports/*.txt || true

# Expose the port your Spring Boot app runs on
EXPOSE 8079

# Run the application
CMD ["java", "-jar", "target/ScraperEndpoint-1.0-SNAPSHOT.jar"]
