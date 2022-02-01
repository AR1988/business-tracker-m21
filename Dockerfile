# syntax=docker/dockerfile:1
FROM gradle:7.3.3-jdk11-alpine as base
WORKDIR /app
COPY build.gradle settings.gradle ./
COPY src ./src

FROM node:17.4.0-alpine3.14 as base-ui
ENV PATH /app/node_modules/.bin:$PATH
WORKDIR /app
COPY --from=base app/src/main/webapp/ ./

FROM base-ui as build-ui
RUN npm ci --silent
RUN npm install react-scripts@3.4.1 -g --silent
RUN npm run build

FROM base as build-api
WORKDIR /app
COPY --from=build-ui /app/build /app/src/main/resources/static
RUN gradle --no-daemon bootJar

FROM openjdk:11-jre-slim as prod
WORKDIR /app
EXPOSE 8080
COPY --from=build-api /app/build/libs/m21-business-tracker*.jar ./app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]