# Use official Java 21 base image
FROM openjdk:21-jdk

# Set the working directory inside the container and copy BE files into it
WORKDIR /app
COPY GameShop-Backend /app

# Installing required utilities for Gradle Wrapper with microdnf (apt-get didn't work -->
# openjdk:21-jdk does not use a Debian-based Linux distribution)
RUN microdnf update && microdnf install -y findutils

RUN cd /app
RUN chmod +x ./gradlew
RUN ./gradlew bootJar
EXPOSE 8080

RUN chmod +wxr /app/build/libs/GameShop-0.0.1-SNAPSHOT.jar
RUN ls -la /app/build/libs

# Run the Spring Boot app
# CMD ["java", "-jar", "/app/build/libs/GameShop-0.0.1-SNAPSHOT.jar"]
CMD ["java", "-Dspring.profiles.active=render", "-jar", "/app/build/libs/GameShop-0.0.1-SNAPSHOT.jar"]