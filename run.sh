#!/bin/bash
# Local run script using Java 17
# Requires: Java 17 (JAVA_HOME17 set) and built JAR file

CONFIG_FILE=${1:-config.xml}

if [ ! -f "$CONFIG_FILE" ]; then
    echo "Error: Config file '$CONFIG_FILE' not found"
    exit 1
fi

if [ ! -f "target/csv-to-excel-converter-1.0.0.jar" ]; then
    echo "Error: JAR file not found. Please run build.sh first"
    exit 1
fi

if [ -z "$JAVA_HOME17" ]; then
    echo "Error: JAVA_HOME17 environment variable is not set"
    echo "Please set JAVA_HOME17 to your Java 17 installation path"
    exit 1
fi

if [ ! -f "$JAVA_HOME17/bin/java" ]; then
    echo "Error: Java 17 not found at JAVA_HOME17=$JAVA_HOME17"
    exit 1
fi

export JAVA_HOME="$JAVA_HOME17"
export PATH="$JAVA_HOME/bin:$PATH"

echo "Using Java 17 from: $JAVA_HOME17"
echo "Running with config: $CONFIG_FILE"
echo ""

"$JAVA_HOME/bin/java" -jar target/csv-to-excel-converter-1.0.0.jar "$CONFIG_FILE"

if [ $? -eq 0 ]; then
    echo ""
    echo "Execution complete!"
else
    echo ""
    echo "Execution failed!"
    exit 1
fi

