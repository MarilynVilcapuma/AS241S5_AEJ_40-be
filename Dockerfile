# ── Paso 3.1: Imagen base ────────────────────────────────────────
FROM maven:3.9.6-eclipse-temurin-17-alpine AS builder

WORKDIR /app

# ── Paso 3.2: Copia los archivos del proyecto ────────────────────
COPY pom.xml .
COPY src ./src

# ── Paso 3.3: Instala las dependencias y compila ─────────────────
RUN mvn clean package -DskipTests -B

# ── Imagen de ejecución liviana ───────────────────────────────────
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

COPY --from=builder /app/target/DetectorIA_NoSQL-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8082

# ── Paso 3.4: Define el ENTRYPOINT para ejecutar la app ──────────
ENTRYPOINT ["java", "-jar", "app.jar"]
