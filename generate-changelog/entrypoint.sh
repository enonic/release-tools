#!/bin/sh -l

ls -la /

cd /

echo "-r $GITHUB_REPOSITORY -s $PREVIOS_RELEASE_TAG -p $REPOSITORY_PATH"
/gradlew run --args="-r $GITHUB_REPOSITORY -s $PREVIOS_RELEASE_TAG -p $REPOSITORY_PATH"
