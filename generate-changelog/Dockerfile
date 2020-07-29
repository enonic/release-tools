FROM adoptopenjdk:11-jdk-hotspot
COPY . /github/workflow/

ENTRYPOINT ["/github/workflow/gradlew", "run", "--args", "\"-r $GITHUB_REPOSITORY -s $PREVIOS_RELEASE_TAG -p $REPOSITORY_PATH\""]
