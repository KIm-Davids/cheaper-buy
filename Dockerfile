# ---------- Stage 1: Build ----------
FROM eclipse-temurin:21-jdk AS builder

WORKDIR /app

# Install Maven
RUN apt-get update && apt-get install -y maven

# Copy only pom.xml to cache dependencies
COPY pom.xml .

# Download dependencies first (uses Docker cache)
RUN mvn dependency:go-offline

# Copy full source code
COPY src ./src

# Build the JAR
RUN mvn clean install -DskipTests || (cat target/surefire-reports/*.txt && false)

# ---------- Stage 2: Run ----------
FROM eclipse-temurin:21-jdk

WORKDIR /app

# Copy built JAR from builder stage
COPY --from=builder /app/target/ScraperEndpoint-1.0-SNAPSHOT.jar ./app.jar

# Start the app
CMD ["java", "-jar", "app.jar"]
