# ── Etapa 1: Build con Maven ────────────────────────────────────────────────
FROM maven:3.9.9-eclipse-temurin-17 AS builder
WORKDIR /app

# Copiar descriptores primero para aprovechar cache de capas
COPY pom.xml .
RUN mvn -q -DskipTests dependency:go-offline

COPY src ./src
RUN mvn -q -DskipTests clean package

# ── Etapa 2: Runtime mínimo ──────────────────────────────────────────────────
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

COPY --from=builder /app/target/brixo-*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["sh", "-c", "java -jar app.jar --server.port=${PORT:-8080}"]
