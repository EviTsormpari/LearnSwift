# We use the OpenJDK image to run the Spring Boot app
FROM openjdk:17-jdk-slim

# Set the working directory in the container
WORKDIR /app

# Copy the jar file from the host to the container
COPY build/libs/LearnSwift-0.0.1-SNAPSHOT.jar /app/LearnSwift-0.0.1-SNAPSHOT.jar

# Run the application
ENTRYPOINT ["java", "-jar", "LearnSwift-0.0.1-SNAPSHOT.jar"]
