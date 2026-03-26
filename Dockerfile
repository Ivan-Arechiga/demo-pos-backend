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

# Expose port (Render will override this with PORT env var)
EXPOSE 8080

# Set environment
ENV SPRING_PROFILES_ACTIVE=default

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
