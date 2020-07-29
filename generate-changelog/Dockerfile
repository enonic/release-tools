FROM adoptopenjdk:11-jdk-hotspot
COPY . .
ENTRYPOINT ["/gradlew", "run", "--args", "\"-r $GITHUB_REPOSITORY -s $PREVIOS_RELEASE_TAG -p $REPOSITORY_PATH\""]
