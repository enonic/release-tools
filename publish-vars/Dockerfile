FROM adoptopenjdk:11-jdk-hotspot

RUN /gradlew build
COPY /build/install/publish-vars/ .

ENTRYPOINT ["/entrypoint.sh"]
