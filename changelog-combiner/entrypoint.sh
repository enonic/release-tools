#!/usr/bin/env sh

DIR=${PWD}
cd /

/gradlew run --args="-p $DIR -o ${DIR}/${OUTPUT_FILE} ${INUPT_FILES}"
