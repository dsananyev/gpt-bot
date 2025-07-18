FROM openjdk:21-jdk-slim

WORKDIR /app
COPY . .
RUN ./gradlew clean build -x test

COPY build/libs/*SNAPSHOT.jar gpt-bot.jar
COPY application.properties .

CMD ["java", "-jar", "gpt-bot.jar"]