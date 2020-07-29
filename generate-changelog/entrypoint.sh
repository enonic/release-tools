#!/bin/sh -l

DIR=${PWD}
cd /
/gradlew run --args="-s ${PREVIOS_RELEASE_TAG} -p $DIR -o ${DIR}/${OUTPUT_FILE}"
