# ---- Build stage ----
FROM eclipse-temurin:17-jdk AS build
WORKDIR /app
COPY . .
RUN ./gradlew clean build -x test

# ---- Run stage ----
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/build/libs/*SNAPSHOT.jar app.jar
COPY src/main/resources/static/uploads/ /app/uploads/
EXPOSE 8080
VOLUME ["/app/uploads"]
ENTRYPOINT ["java", "-jar", "app.jar"] 