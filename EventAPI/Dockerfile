# should be run from project root, not api root
FROM maven:3.8.5-openjdk-17 AS build
WORKDIR /app

COPY EventAPI/pom.xml .
COPY EventAPI/src ./src
COPY AuthAPI/protos/auth.proto ./src/main/proto/auth.proto
RUN mvn clean install -DskipTests -X

FROM openjdk:17.0.1-slim
WORKDIR /app
COPY --from=build /app/target/EventAPI-1.0-SNAPSHOT.jar ./app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
