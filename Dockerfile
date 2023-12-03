# Start with a base image containing Java and Maven
FROM maven:3.8.5-openjdk-17 AS builder

# Set the working directory in the container
WORKDIR /app

# Copy the pom.xml and download the dependencies
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy the source code
COPY src ./src

# Build the application
RUN mvn package -DskipTests

# Start a new stage for the runtime image
FROM openjdk:17

# Set the working directory in the container
WORKDIR /app

# Copy the built JAR file from the builder stage
COPY --from=builder /app/target/coderunner.jar .

# Expose the port on which the Spring Boot application will run
EXPOSE 8090

# Run the Spring Boot application
CMD ["java", "-jar", "coderunner.jar"]