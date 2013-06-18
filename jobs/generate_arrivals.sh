#!/usr/bin/env bash

set -o xtrace 
set -o errexit

JARFILE=/assets/bpijewski/stor/munistats/assets/arrival_predictor.jar
DIR=/var/tmp/files

AGENCY=$(basename $(dirname $(dirname ${MANTA_INPUT_OBJECT})))

KEY=$(basename ${MANTA_INPUT_OBJECT} .tar)
ROUTE=$(echo $KEY | cut -d_ -f1)
YEAR=$(echo $KEY | cut -d_ -f2)
MONTH=$(echo $KEY | cut -d_ -f3)
DAY=$(echo $KEY | cut -d_ -f4)

mkdir -p ${DIR}
cd ${DIR}
tar -xf ${MANTA_INPUT_FILE}

cd ~

tmpfile=/var/tmp/arrivals.$$.txt

jar -xf ${JARFILE} routes
java -jar ${JARFILE} ${AGENCY} ${ROUTE} ${DIR} ${tmpfile}

mput -f ${tmpfile} \
    /bpijewski/stor/munistats/${AGENCY}/arrivals/${ROUTE}_${YEAR}_${MONTH}_${DAY}.txt
