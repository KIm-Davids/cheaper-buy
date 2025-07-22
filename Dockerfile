FROM openjdk:21-jdk-slim

# Install required tools and headless Chrome dependencies
RUN apt-get update && apt-get install -y \
    wget \
    curl \
    gnupg \
    unzip \
    chromium-driver \
    chromium \
    libglib2.0-0 \
    libnss3 \
    libx11-xcb1 \
    libxcomposite1 \
    libxdamage1 \
    libxrandr2 \
    libatk1.0-0 \
    libatk-bridge2.0-0 \
    libgtk-3-0 \
    --no-install-recommends && \
    rm -rf /var/lib/apt/lists/*

# Set working directory
WORKDIR /app

# Copy and build your Java app
COPY pom.xml .
COPY src ./src

# Install Maven manually
RUN curl -fsSL https://downloads.apache.org/maven/maven-3/3.9.6/binaries/apache-maven-3.9.6-bin.zip -o maven.zip \
    && unzip maven.zip -d /opt \
    && ln -s /opt/apache-maven-3.9.6/bin/mvn /usr/bin/mvn \
    && rm maven.zip

# Build the project
RUN mvn clean package -DskipTests

EXPOSE 8080

CMD ["java", "-jar", "target/ScraperEndpoint-1.0-SNAPSHOT.jar"]
