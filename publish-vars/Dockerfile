FROM adoptopenjdk:11-jdk-hotspot-bionic AS builder

COPY . .
RUN /gradlew installDist

FROM adoptopenjdk:11-jre-hotspot-bionic
COPY --from=builder /build/install/publish-vars ./publish-vars
COPY /entrypoint.sh .

ENTRYPOINT ["/entrypoint.sh"]
