#!/bin/bash

set -o errexit

if [[ $# -ne 2 ]]; then
	echo "Usage: $0 <agency> <route>"
	exit 1
fi

AGENCY=$1
ROUTE=$2

curl -G -d "command=routeConfig" -d "a=$AGENCY" --data-urlencode "r=$ROUTE" \
    http://webservices.nextbus.com/service/publicXMLFeed
