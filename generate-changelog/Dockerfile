FROM gradle:jdk11
COPY . .
ENTRYPOINT ["gradle", "run", "--args", "\"-r $GITHUB_REPOSITORY -s $PREVIOS_RELEASE_TAG\""]
