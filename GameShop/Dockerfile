# Stage 1: Build Vue.js Frontend
FROM node:18-alpine as frontend-build

WORKDIR /app/frontend
COPY GameShop-FrontEnd /app/frontend

# Pass the VITE_BASE_URL during the build (or fallback to default)
ARG VITE_BASE_URL=http://localhost:8080
ENV VITE_BASE_URL=$VITE_BASE_URL

RUN npm install
RUN npm run build

# Stage 2: Build Spring Boot Backend
FROM openjdk:21-jdk

WORKDIR /app

# Copy backend files
COPY GameShop-Backend /app/backend
WORKDIR /app/backend

# Copy the built frontend into Spring Boot's static directory
RUN rm -rf src/main/resources/static && mkdir -p src/main/resources/static
COPY --from=frontend-build /app/frontend/dist/ src/main/resources/static/

RUN microdnf update && microdnf install -y findutils

# Build the backend
RUN chmod +x ./gradlew
RUN ./gradlew bootJar
EXPOSE 8080

# Run the application
CMD ["java", "-jar", "/app/backend/build/libs/GameShop-0.0.1-SNAPSHOT.jar"]

# "-Dspring.profiles.active=render"

# # Use official Java 21 base image
# FROM openjdk:21-jdk

# # Set the working directory inside the container and copy BE files into it
# WORKDIR /app
# COPY GameShop-Backend /app

# # Installing required utilities for Gradle Wrapper with microdnf (apt-get didn't work -->
# # openjdk:21-jdk does not use a Debian-based Linux distribution)
# RUN microdnf update && microdnf install -y findutils

# RUN cd /app
# RUN chmod +x ./gradlew
# RUN ./gradlew bootJar
# EXPOSE 8080

# RUN chmod +wxr /app/build/libs/GameShop-0.0.1-SNAPSHOT.jar
# RUN ls -la /app/build/libs

# # Run the Spring Boot app
# # CMD ["java", "-jar", "/app/build/libs/GameShop-0.0.1-SNAPSHOT.jar"]
# CMD ["java", "-Dspring.profiles.active=render", "-jar", "/app/build/libs/GameShop-0.0.1-SNAPSHOT.jar"]