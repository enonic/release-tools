#!/bin/sh -l

PREVIOUS_RELEASE_TAG=$(git tag --sort=-version:refname --merged | grep -E '^v([[:digit:]]+\.){2}[[:digit:]]$' | head -1)

DIR=$PWD
cd /

/gradlew run --args="-s $PREVIOS_RELEASE_TAG -p $DIR -f $DIR/changelog.md"
