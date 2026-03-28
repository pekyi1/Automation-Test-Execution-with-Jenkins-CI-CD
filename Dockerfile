# Use Maven image to build and run tests
FROM maven:3.9.5-eclipse-temurin-17-alpine

# Set the working directory
WORKDIR /app

# Copy the pom.xml and the doc-data.json
COPY pom.xml .
COPY docs-data.json .

# Pre-fetch dependencies
RUN mvn dependency:go-offline

# Copy the source code
COPY src ./src

# Default command to run tests
CMD ["mvn", "test"]
