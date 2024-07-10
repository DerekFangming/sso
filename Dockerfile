# Stage build
FROM openjdk:17-alpine AS builder

WORKDIR /app
COPY . .

RUN chmod +x gradlew
RUN ./gradlew bootJar

# Stage run
FROM openjdk:17-alpine

WORKDIR /app
COPY --from=builder /app/build/libs .

ENV PRODUCTION=true

CMD ["java", "-jar", "sso.jar"]