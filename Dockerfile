# Use official Java 21 image
FROM eclipse-temurin:21-jdk

# Set the working directory
WORKDIR /app

# Copy pom.xml and download dependencies first (to leverage Docker cache)
COPY pom.xml .

RUN apt-get update && apt-get install -y maven && \
    mvn dependency:go-offline

# Copy the rest of the application code
COPY src ./src

# Package the application
RUN mvn clean install -DskipTests || cat target/surefire-reports/*.txt || true

# Run the app (adjust based on your jar name)
CMD ["java", "-jar", "target/ScraperEndpoint-0.0.1-SNAPSHOT.jar"]

COPY --from=builder /app/target/app.jar app.jar
