#!/usr/bin/env bash
#
# archive_data.sh: archive all routes by uploading them to manta
#

if [[ $# -ne 1 ]]; then
    echo "usage: $0 <dir>"
    exit 1
fi

set -o xtrace

CWD=$PWD
DATA_DIR=$(readlink -f "$1")

AGENCY=$(basename "${DATA_DIR}")
MANTA_PATH=/bpijewski/stor/munistats/${AGENCY}/data

OLDIFS=$IFS
IFS=$'\n'

echo $AGENCY
echo $DATA_DIR

for route in $(ls -1 $DATA_DIR); do
    ./archive_route.sh "$DATA_DIR/${route}"
done

IFS=${OLDIFS}

exit 0
