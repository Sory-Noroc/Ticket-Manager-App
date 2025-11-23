FROM openjdk:17-ea
WORKDIR /app

COPY gradlew .
COPY gradle ./gradle

COPY build.gradle.kts .
COPY settings.gradle.kts .

RUN chmod +x ./gradlew

RUN ./gradlew build --no-daemon

COPY . .

RUN ./gradlew build --no-daemon

EXPOSE 9090

CMD ["java", "-jar", "build/libs/AuthAPI-0.0.1-SNAPSHOT.jar"]
