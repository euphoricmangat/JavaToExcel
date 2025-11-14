#!/bin/bash
# Quick Docker run script for Windows (Git Bash) or Linux/Mac

CONFIG_FILE=${1:-config.xml}

if [ ! -f "$CONFIG_FILE" ]; then
    echo "Error: Config file '$CONFIG_FILE' not found"
    exit 1
fi

# Run the application using Docker
docker run --rm \
  -v "$(pwd)":/workspace \
  -w /workspace \
  maven:3.9-eclipse-temurin-17 \
  java -jar target/csv-to-excel-converter-1.0.0.jar "$CONFIG_FILE"

