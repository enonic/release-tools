FROM openjdk:11 AS builder
COPY . .
RUN "chmod +x gradlew && ./gradlew build

FROM openjdk:11
COPY --from=builder build/libs/generate-changelog.jar generate-changelog.jar
ENTRYPOINT ["java", "-jar", "generate-changelog.jar", "-r", "$GITHUB_REPOSITORY", "-s", "$PREVIOS_RELEASE_TAG"]