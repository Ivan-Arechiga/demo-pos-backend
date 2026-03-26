# Build stage
FROM gradle:8.5-jdk17 as builder
WORKDIR /app
COPY build.gradle.kts settings.gradle.kts ./
COPY gradle ./gradle
COPY src ./src
RUN gradle clean build --no-daemon -q

# Runtime stage
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Copy the built JAR from builder stage
COPY --from=builder /app/build/libs/demo-pos-backend-*.jar app.jar

# Default port (Render will override via PORT environment variable)
ENV PORT=8080
ENV SPRING_PROFILES_ACTIVE=default

# Run the application - Spring Boot reads PORT env var automatically
ENTRYPOINT ["sh", "-c", "java -jar app.jar"]
