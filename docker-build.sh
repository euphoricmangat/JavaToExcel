#!/bin/bash
# Quick Docker build script for Windows (Git Bash) or Linux/Mac

# Build the project using Docker
docker run --rm \
  -v "$(pwd)":/workspace \
  -w /workspace \
  maven:3.9-eclipse-temurin-17 \
  mvn clean package

echo "Build complete! JAR file is in target/ directory"

