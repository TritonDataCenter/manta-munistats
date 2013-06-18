#!/usr/bin/env bash

set -o xtrace 
set -o errexit

JARFILE=/assets/bpijewski/stor/munistats/assets/trips.jar
DIR=/var/tmp/files

AGENCY=$(basename $(dirname $(dirname ${MANTA_INPUT_OBJECT})))

KEY=$(basename ${MANTA_INPUT_OBJECT} .tar)
ROUTE=$(echo $KEY | cut -d_ -f1)
YEAR=$(echo $KEY | cut -d_ -f2)
MONTH=$(echo $KEY | cut -d_ -f3)
DAY=$(echo $KEY | cut -d_ -f4)

DIRECTION=$1
FROM_STOP=$2
TO_STOP=$3

cd ~

tmpfile=/var/tmp/trips.$$.txt

jar -xf ${JARFILE} route
java -jar ${JARFILE} ${AGENCY} ${ROUTE} ${MANTA_INPUT_FILE} ${DIRECTION} ${FROM_STOP} ${TO_STOP}
