FROM amazoncorretto:21.0.7-alpine3.21 as builder

WORKDIR /app

COPY . .

RUN ./gradlew test createCustomJRE shadowJar

FROM alpine:latest

WORKDIR /app

COPY --from=builder /app/build/custom-jre /opt/java-custom
COPY --from=builder /app/build/libs/CapitalGainsCalculator.jar .

ENTRYPOINT ["/opt/java-custom/bin/java", "-jar", "CapitalGainsCalculator.jar"]
