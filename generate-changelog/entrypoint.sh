#!/bin/sh -l

DIR=$PWD
cd /

echo "-s $PREVIOS_RELEASE_TAG -p $DIR -f $DIR/$OUTPUT_FILE"
/gradlew run --args="-s $PREVIOS_RELEASE_TAG -p $DIR -f $DIR/$OUTPUT_FILE"
