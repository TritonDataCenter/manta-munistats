#!/usr/bin/env bash
#
# archiveData.sh: travserse the data directory, tar up each day on each
# route, and upload those tar archives to manta.
#

if [[ $# -ne 1 ]]; then
    echo "usage: $0 <dir>"
    exit 1
fi

CWD=$PWD
DATA_DIR=$(readlink -f "$1")

AGENCY=$(basename $(dirname "${DATA_DIR}"))
MANTA_PATH=/bpijewski/stor/munistats/${AGENCY}/data

OLDIFS=$IFS
IFS=$'\n'

NAME=$(basename ${DATA_DIR})

for year in $(ls -1 $DATA_DIR); do
    for month in $(ls -1 $DATA_DIR/$year); do
         for day in $(ls -1 $DATA_DIR/$year/$month); do
             cd "$DATA_DIR/$year/$month/$day"

             YEAR=$(printf "%04d" ${year})
             MONTH=$(printf "%02d" ${month})
             DAY=$(printf "%02d" ${day})

             tarfile="${NAME}_${YEAR}_${MONTH}_${DAY}.tar"

             # Manta keys cannot have spaces in them
             tarfile=$(echo ${tarfile} | tr -d " ")

             set +o errexit
             mls "$MANTA_PATH/${tarfile}" >/dev/null 2>&1
             if [[ $? -eq 0 ]]; then
                 set -o errexit
                 echo "${tarfile} already uploaded, continuing."
                 continue
             fi
             set -o errexit

             echo "Uploading ${tarfile}."

             tar -cf $CWD/${tarfile} *.xml

             mput -f $CWD/${tarfile} $MANTA_PATH/${tarfile}

             rm $CWD/${tarfile}
         done
    done
done


IFS=${OLDIFS}

exit 0
