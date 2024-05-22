FROM openjdk:17-alpine AS builder

WORKDIR /app
COPY . .

RUN ./gradlew bootJar


FROM openjdk:17-alpine

WORKDIR /app
COPY --from=builder /app/build/libs .

ENV PRODUCTION=false

CMD ["java", "-jar", "sso.jar"]