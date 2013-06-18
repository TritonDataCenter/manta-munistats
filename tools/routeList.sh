#!/bin/bash

set -o errexit

if [[ $# -ne 1 ]]; then
    echo "usage: $0 <agency>"
    exit 1
fi

AGENCY=$1

curl -s -G -d "command=routeList" -d "a=${AGENCY}" \
    http://webservices.nextbus.com/service/publicXMLFeed
