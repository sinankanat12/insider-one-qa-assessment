FROM maven:3.9-eclipse-temurin-17 AS builder

WORKDIR /app

# Copy pom.xml first — Docker caches this layer if pom.xml is unchanged
COPY pom.xml .
RUN mvn dependency:go-offline -q

# Copy source and run tests
COPY src/ ./src/
RUN mvn test

# Allure results are in /app/target/allure-results/
# Extract with: docker cp <container>:/app/target/allure-results ./allure-results
