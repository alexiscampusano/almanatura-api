# syntax=docker/dockerfile:1.7

# ---- Stage 1: Build ----
FROM eclipse-temurin:21-jdk-alpine AS builder

WORKDIR /app

COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

RUN chmod +x ./mvnw && ./mvnw -q dependency:go-offline -B

COPY src src

RUN ./mvnw -q clean package -DskipTests -B

# ---- Stage 2: Runtime ----
FROM eclipse-temurin:21-jre-alpine AS runtime

RUN addgroup -g 1001 -S spring && \
    adduser -u 1001 -S spring -G spring && \
    apk add --no-cache wget

WORKDIR /app

COPY --from=builder /app/target/*.jar app.jar
RUN chown -R spring:spring /app

USER spring:spring

ENV JAVA_OPTS="-Xms256m -Xmx512m -XX:+UseG1GC" \
    SPRING_PROFILES_ACTIVE=docker \
    TZ=Europe/Madrid

EXPOSE 8080

HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
    CMD wget -qO- http://localhost:8080/api/v1/actuator/health || exit 1

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
