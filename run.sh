#!/bin/bash
# Demo POS Backend - Quick Start Script

set -e

echo "🚀 Demo POS Backend - Starting Application"
echo "=========================================="

# Check if Java is installed
if ! command -v java &> /dev/null; then
    echo "❌ Java is not installed. Please install Java 17 or higher."
    exit 1
fi

# Check Java version
JAVA_VERSION=$(java -version 2>&1 | grep -oP 'version "\K[^"]+')
echo "✅ Java version: $JAVA_VERSION"

# Build the project
echo ""
echo "📦 Building project..."
if command -v gradle &> /dev/null; then
    gradle clean build --no-daemon -q
else
    chmod +x ./gradlew
    ./gradlew clean build --no-daemon -q
fi

echo "✅ Build successful!"
echo ""
echo "🏃 Starting application..."
echo "=========================================="
echo ""
echo "📍 Available URLs:"
echo "   - API: http://localhost:8080"
echo "   - Swagger UI: http://localhost:8080/swagger-ui.html"
echo "   - OpenAPI JSON: http://localhost:8080/api-docs"
echo "   - H2 Console: http://localhost:8080/h2-console"
echo ""
echo "⏸️ Press Ctrl+C to stop the application"
echo "=========================================="
echo ""

# Run the application
java -jar build/libs/demo-pos-backend-1.0.0.jar
