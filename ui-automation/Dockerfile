FROM maven:3.9.6-eclipse-temurin-17

WORKDIR /app

# Copy pom.xml first for dependency layer caching
COPY pom.xml .
RUN mvn dependency:go-offline -q

# Copy source code
COPY src ./src

CMD ["mvn", "test"]
