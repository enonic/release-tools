FROM ubuntu:latest
COPY . .
ENTRYPOINT ["./gradlew", "run", "--args", "\"-r $GITHUB_REPOSITORY -s $PREVIOS_RELEASE_TAG\""]
