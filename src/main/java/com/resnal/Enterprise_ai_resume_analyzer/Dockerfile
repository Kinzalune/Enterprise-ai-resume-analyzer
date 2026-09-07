# ==============================================================================
# Multi-Stage Build for Enterprise AI Resume Analyzer
# Base Image: Eclipse Temurin Java 21 LTS
# ==============================================================================

# Stage 1: Build & Package Artifact
FROM eclipse-temurin:21-jdk-alpine AS builder
WORKDIR /workspace

# Copy Maven wrapper and dependencies specification first (caching layer)
COPY .mvn/ .mvn
COPY mvnw pom.xml ./
RUN chmod +x mvnw && ./mvnw dependency:go-offline -B

# Copy source tree and compile application package
COPY src ./src
RUN ./mvnw clean package -DskipTests -B

# Stage 2: Minimal Distroless/Alpine Runtime
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Run as an unprivileged, non-root user for cloud security hardening
RUN addgroup -S appgroup && adduser -S appuser -G appgroup
USER appuser

# Copy executable jar from builder stage
COPY --from=builder /workspace/target/*.jar app.jar

# Expose target application port
EXPOSE 8085

# Optimized JVM execution parameters for containerized memory management
ENTRYPOINT ["java", "-XX:+UseContainerSupport", "-XX:MaxRAMPercentage=75.0", "-jar", "app.jar"]