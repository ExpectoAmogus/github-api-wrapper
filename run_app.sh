#!/bin/bash

# Package the application
./mvnw clean package

# Check if the build was successful
if [ $? -ne 0 ]; then
  echo "Build failed. Exiting."
  exit 1
fi

# Find the JAR file
JAR_FILE=$(ls target/*.jar | head -n 1)

# Check if the JAR file exists
if [ -z "$JAR_FILE" ]; then
  echo "JAR file not found. Exiting."
  exit 1
fi

echo "Starting Spring Boot application: $JAR_FILE"

# Run the application
java -jar $JAR_FILE
