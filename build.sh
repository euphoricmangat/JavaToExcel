#!/bin/bash
# Local build script using Maven and Java 17
# Requires: Maven 3.6+ and Java 17 (JAVA_HOME17 set)

if [ -z "$JAVA_HOME17" ]; then
    echo "Error: JAVA_HOME17 environment variable is not set"
    echo "Please set JAVA_HOME17 to your Java 17 installation path"
    exit 1
fi

if [ ! -f "$JAVA_HOME17/bin/java" ]; then
    echo "Error: Java 17 not found at JAVA_HOME17=$JAVA_HOME17"
    exit 1
fi

echo "Using Java 17 from: $JAVA_HOME17"
echo "Using Maven:"
mvn --version

export JAVA_HOME="$JAVA_HOME17"
export PATH="$JAVA_HOME/bin:$PATH"

echo ""
echo "Building project..."
mvn clean package

if [ $? -eq 0 ]; then
    echo ""
    echo "Build complete! JAR file is in target/ directory"
else
    echo ""
    echo "Build failed!"
    exit 1
fi

