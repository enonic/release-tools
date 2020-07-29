#!/bin/sh -l

ls -la /

DIR=$PWD
cd /

echo "-r $GITHUB_REPOSITORY -s $PREVIOS_RELEASE_TAG -p $DIR"
/gradlew run --args="-r $GITHUB_REPOSITORY -s $PREVIOS_RELEASE_TAG -p $DIR"
