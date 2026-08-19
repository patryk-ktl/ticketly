FROM maven:3.9-eclipse-temurin-21 AS builder
WORKDIR /build

COPY pom.xml .

COPY ticketing-service/pom.xml ticketing-service/pom.xml
COPY payment-service/pom.xml payment-service/pom.xml
COPY eureka-server/pom.xml eureka-server/pom.xml
COPY gateway/pom.xml gateway/pom.xml

ARG MODULE
RUN mvn -B -pl ${MODULE} -am dependency:go-offline

COPY ticketing-service/src ./ticketing-service/src
COPY payment-service/src ./payment-service/src
COPY eureka-server/src ./eureka-server/src
COPY gateway/src ./gateway/src

RUN mvn -B -pl ${MODULE} -am package -DskipTests

FROM eclipse-temurin:21-jre
ARG MODULE
WORKDIR /app

RUN groupadd -r appgroup && useradd -r -g appgroup -u 10001 appuser

COPY --from=builder /build/${MODULE}/target/*.jar app.jar

RUN chown -R appuser:appgroup /app

USER appuser

ARG PORT=8080
EXPOSE ${PORT}

ENTRYPOINT ["java", "-jar", "/app/app.jar"]