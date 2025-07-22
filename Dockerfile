FROM eclipse-temurin:21-jdk

# Install just enough for Chromium
RUN apt-get update && apt-get install -y \
    wget \
    curl \
    unzip \
    chromium \
    libglib2.0-0 \
    libnss3 \
    libgconf-2-4 \
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

# Copy Maven build files first
COPY pom.xml .
COPY src ./src

# Build app (no go-offline!)
RUN curl -fsSL https://download.oracle.com/maven/maven-3/3.9.6/binaries/apache-maven-3.9.6-bin.zip -o maven.zip \
    && unzip maven.zip -d /opt \
    && ln -s /opt/apache-maven-3.9.6/bin/mvn /usr/bin/mvn \
    && rm maven.zip

RUN mvn clean package -DskipTests

# Copy the final jar (if you’re copying prebuilt jars)
# COPY target/ScraperEndpoint-1.0-SNAPSHOT.jar .

EXPOSE 8080

CMD ["java", "-jar", "target/ScraperEndpoint-1.0-SNAPSHOT.jar"]
