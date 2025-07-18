# ---------- Stage 1: Build ----------
FROM eclipse-temurin:21-jdk AS builder

WORKDIR /app

# Install Maven
RUN apt-get update && apt-get install -y maven

# Copy only files needed to build dependencies first (for Docker cache)
COPY pom.xml .
RUN mvn dependency:go-offline

# Now copy the full source code
COPY src ./src

# Build the JAR
RUN mvn clean install -DskipTests

# ---------- Stage 2: Run ----------
FROM eclipse-temurin:21-jdk

WORKDIR /app

# Copy the JAR from the builder stage
COPY --from=builder /app/target/ScraperEndpoint-1.0-SNAPSHOT.jar ./app.jar

# Run the app
CMD ["java", "-jar", "app.jar"]
