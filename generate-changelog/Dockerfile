FROM openjdk:11

COPY build/libs/generate-changelog.jar generate-changelog.jar

ENTRYPOINT ["java", "-jar", "generate-changelog.jar", "-r", "$GITHUB_REPOSITORY", "-s", "$PREVIOS_RELEASE_TAG"]