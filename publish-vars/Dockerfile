FROM adoptopenjdk:11-jdk-hotspot AS builder

COPY . .
RUN /gradlew installDist
RUN ls -la /build/install/

FROM alpine:3.10
COPY --from=builder /build/install/publish-vars .
COPY /entrypoint.sh .
ENTRYPOINT ["/entrypoint.sh"]
