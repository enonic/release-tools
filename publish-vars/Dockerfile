FROM adoptopenjdk:11-jdk-hotspot-bionic AS builder

COPY . .
RUN /gradlew installDist

FROM adoptopenjdk:11-jdk-hotspot-bionic
COPY --from=builder /build/install/publish-vars .
COPY /entrypoint.sh .
RUN chmod +x /entrypoint.sh
ENTRYPOINT ["/entrypoint.sh"]
