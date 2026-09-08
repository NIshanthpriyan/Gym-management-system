# Multi-stage Docker Build for Java 21 Spring Boot Application
# Stage 1: Build JAR using Maven
FROM maven:3.9.6-eclipse-temurin-21 AS builder
WORKDIR /app

# Copy pom.xml and download dependencies
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source code and build production package
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Minimal JRE Runtime
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Create non-root user for security
RUN addgroup -S gymgroup && adduser -S gymuser -G gymgroup
USER gymuser

# Copy generated JAR artifact from builder
COPY --from=builder /app/target/*.jar app.jar

# Expose port (default 8080 or dynamic port via PORT environment variable)
ENV PORT=8080
EXPOSE 8080

# Run Spring Boot application
ENTRYPOINT ["java", "-Dserver.port=${PORT}", "-Djava.security.egd=file:/dev/./urandom", "-jar", "app.jar"]
