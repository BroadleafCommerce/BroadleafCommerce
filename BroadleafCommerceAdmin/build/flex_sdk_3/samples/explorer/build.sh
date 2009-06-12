#!/bin/sh
OPTS=-use-network=false

../../bin/mxmlc $OPTS explorer.mxml

mxmlFiles=`find */* -name '*.mxml' -print`

for mxml in ${mxmlFiles}; do
        echo "building $mxml"
        (../../bin/mxmlc $OPTS ${mxml})
done
