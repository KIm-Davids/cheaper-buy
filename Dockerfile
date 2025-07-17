# Stage 1: Build the app with Maven
FROM eclipse-temurin:21-jdk as build

WORKDIR /app

# Copy the Maven wrapper (if using)
COPY .mvn .mvn
COPY mvnw pom.xml ./
RUN ./mvnw dependency:go-offline

# Copy the rest of the source code
COPY src ./src

# Build the app
RUN ./mvnw clean package -DskipTests

# Stage 2: Create a minimal runtime image
FROM eclipse-temurin:21-jdk

WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

CMD ["java", "-jar", "app.jar"]
