FROM adoptopenjdk:11-jdk-hotspot

RUN apt-get update && apt-get install -y git

COPY . .

ENTRYPOINT ["/entrypoint.sh"]
