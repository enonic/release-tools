FROM adoptopenjdk:11-jdk-hotspot AS builder

COPY . .
RUN /gradlew installDist

FROM adoptopenjdk:11-jdk-hotspot
COPY --from=builder /build/install/publish-vars .
COPY /entrypoint.sh .
RUN chmod +x /entrypoint.sh
ENTRYPOINT ["/entrypoint.sh"]
