# ---- Etapa 1: build con Maven ----
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
COPY .mvn .mvn
COPY mvnw .
RUN chmod +x mvnw
# Descarga dependencias primero (aprovecha cache de capas de Docker)
RUN ./mvnw -q dependency:go-offline || true
COPY src ./src
RUN ./mvnw clean package -DskipTests

# ---- Etapa 2: imagen final, solo el JAR ----
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8082
ENTRYPOINT ["java", "-jar", "app.jar"]
