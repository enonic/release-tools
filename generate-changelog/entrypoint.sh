#!/bin/sh -l

DIR=${PWD}
cd /
if [ -z "${PREVIOS_RELEASE_TAG}" ]
then
      /gradlew run --args="-p $DIR -o ${DIR}/${OUTPUT_FILE}"
else
      /gradlew run --args="-s ${PREVIOS_RELEASE_TAG} -p $DIR -o ${DIR}/${OUTPUT_FILE}"
fi
