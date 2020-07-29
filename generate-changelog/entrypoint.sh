#!/bin/sh -l

ls -la /

cd /

/gradlew run --args="-r $GITHUB_REPOSITORY -s $PREVIOS_RELEASE_TAG -p $REPOSITORY_PATH"
