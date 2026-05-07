FROM maven:3.9.14-eclipse-temurin-25 AS build

WORKDIR /app

COPY backend/pom.xml .

RUN mvn dependency:go-offline -B

COPY backend/src ./src

RUN mvn package shade:shade

FROM eclipse-temurin:25-jre-alpine-3.23

WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT [ "java", "-jar", "app.jar" ]