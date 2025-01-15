# Use an OpenJDK image
FROM openjdk:17-jdk-slim

# Set working directory
WORKDIR /app

# Copy the build output
COPY ./build/libs/*.jar app.jar

# Expose the application port (update if needed)
EXPOSE 8080

# Run the application
CMD ["java", "-jar", "app.jar"]