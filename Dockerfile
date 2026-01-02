FROM maven:3.9-eclipse-temurin-17 AS build

WORKDIR /app

COPY pom.xml .
COPY src ./src

RUN mvn clean package -DskipTests

FROM eclipse-temurin:17-jre

WORKDIR /app

COPY --from=build /app/target/classes ./classes
COPY --from=build /app/target/dependency ./lib
COPY --from=build /app/src/main/resources ./src/main/resources
COPY --from=build /app/src/test/resources ./src/test/resources

ENV CLASSPATH=/app/classes:/app/lib/*

CMD ["java", "-cp", "/app/classes:/app/lib/*", "org.example.App"]





