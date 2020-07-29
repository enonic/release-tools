FROM adoptopenjdk:11-jdk-hotspot
COPY . /github/workflow/

ENTRYPOINT ["./gradlew", "run", "--args", "\"-r $GITHUB_REPOSITORY -s $PREVIOS_RELEASE_TAG -p $REPOSITORY_PATH\""]
