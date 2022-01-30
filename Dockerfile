# syntax=docker/dockerfile:1
FROM gradle:7.3.3-jdk11-alpine as base
WORKDIR /app
COPY build.gradle settings.gradle ./
COPY src ./src

FROM base as build
RUN gradle --no-daemon bootJar

FROM openjdk:11-jre-slim as production
WORKDIR /app
EXPOSE 8080

COPY --from=build /app/build/libs/m21-business-tracker*.jar ./app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
