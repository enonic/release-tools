FROM adoptopenjdk:11-jre-hotspot
COPY . .
ENTRYPOINT ["gradlew", "run", "--args", "\"-r $GITHUB_REPOSITORY -s $PREVIOS_RELEASE_TAG\""]
