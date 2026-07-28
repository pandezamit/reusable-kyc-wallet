# ---- Stage 1: Build the Spring Boot fat jar with Maven ----
FROM maven:3.9.9-eclipse-temurin-17 AS build
WORKDIR /build

# Cache dependencies separately from source so code edits don't re-download the world
COPY pom.xml .
RUN mvn -B -q dependency:go-offline || true

COPY src ./src
RUN mvn -B -q clean package -DskipTests

# ---- Stage 2: Slim runtime image ----
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

RUN groupadd --system app && useradd --system --gid app --home-dir /app app

COPY --from=build /build/target/*.jar /app/app.jar
RUN chown -R app:app /app
USER app

# Cloud Run injects PORT (defaults to 8080); docker-compose can just leave it at 8080.
ENV PORT=8080
EXPOSE 8080

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -Dserver.port=${PORT} -jar /app/app.jar"]
