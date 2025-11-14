# Dev Container Setup

This project includes a Dev Container configuration that allows you to compile and run the Java Maven project without installing Java or Maven on your local system.

## Prerequisites

- **Docker Desktop** (or Docker Engine) must be installed on your system
- **Dev Containers extension** in Cursor/VS Code

## How to Use

1. **Install Docker Desktop** (if not already installed):
   - Download from: https://www.docker.com/products/docker-desktop
   - Install and start Docker Desktop

2. **Open in Dev Container**:
   - In Cursor, press `F1` (or `Ctrl+Shift+P`)
   - Type: `Dev Containers: Reopen in Container`
   - Select it and wait for the container to build and start

3. **Once inside the container**, you'll have:
   - Java 17 JDK
   - Maven 3.9
   - All project dependencies

4. **Build and Run**:
   ```bash
   # Build the project
   mvn clean package
   
   # Run the application
   java -jar target/csv-to-excel-converter-1.0.0.jar config.xml
   ```

## Alternative: Quick Docker Commands

If you prefer using Docker directly without Dev Containers:

```bash
# Build the project
docker run --rm -v ${PWD}:/workspace -w /workspace maven:3.9-eclipse-temurin-17 mvn clean package

# Run the application
docker run --rm -v ${PWD}:/workspace -w /workspace maven:3.9-eclipse-temurin-17 java -jar target/csv-to-excel-converter-1.0.0.jar config.xml
```

## Notes

- The container includes Java 17 and Maven
- Your project files are mounted into the container
- All build artifacts will be created in the `target/` directory on your local machine

