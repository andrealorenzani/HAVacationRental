#!/bin/bash

sed 's/%NEWID%/null/g' example.json > newlisting

function startServer() {
    `sbt run > tmp 2>&1 &`
    while [ -z "$STARTED" ]
        do
        GREP=`cat tmp | grep "Exception"`
        if [ -n "$GREP" ]
        then
            echo "Another instance of the server running on the same port"
            exit 1
        fi
        STARTED=`cat tmp | grep 'Finagle version'`
        echo -n "."
        sleep 1
    done
    echo "Server started"
    rm -f tmp
}

function createListing() {
    RAWFIRST=`curl --request POST --data '@newlisting' http://localhost:8080/new | jq --raw-output .newId`
    echo $RAWFIRST
}

function removeListing() {
    RAWFIRST=`curl --request DELETE http://localhost:8080/del/"$1"`
    echo "$RAWFIRST"   
}

curl --request DELETE http://localhost:8080/delall

removeListing `createListing`

rm -f newlisting