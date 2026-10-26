#!/bin/bash
# Smoke test of the REST API. Needs curl and jq.
# Usage: ./integrationTests.sh          (starts the server with sbt, then stops it)
#        START_SERVER=no ./integrationTests.sh   (uses a server already running on $BASE_URL)
set -euo pipefail
cd "$(dirname "$0")"

BASE_URL=${BASE_URL:-http://localhost:8080}
LOG=$(mktemp)
NEWLISTING=$(mktemp)
SERVER_PID=""

cleanup() {
    if [ -n "$SERVER_PID" ]; then
        # sbt forks the server in a JVM of its own: stop the whole process group
        kill -- "-$SERVER_PID" 2>/dev/null || true
    fi
    rm -f "$LOG" "$NEWLISTING"
}
trap cleanup EXIT

fail() { echo "FAIL: $1"; exit 1; }
expect() { [ "$2" == "$3" ] || fail "$1 (expected '$3', got '$2')"; echo "ok: $1"; }

startServer() {
    if curl -s -o /dev/null "$BASE_URL/size"; then
        fail "another instance of the server is already running on $BASE_URL"
    fi
    setsid sbt run > "$LOG" 2>&1 &
    SERVER_PID=$!
    for _ in $(seq 1 120); do
        curl -s -o /dev/null "$BASE_URL/size" && { echo "Server started"; return; }
        kill -0 "$SERVER_PID" 2>/dev/null || { cat "$LOG"; fail "the server died"; }
        sleep 1
    done
    fail "timeout waiting for the server"
}

[ "${START_SERVER:-yes}" == "yes" ] && startServer

sed 's/%NEWID%/null/g' example.json > "$NEWLISTING"

curl -s --request DELETE "$BASE_URL/delall" > /dev/null
expect "empty store" "$(curl -s "$BASE_URL/size" | jq .size)" 0

ID=$(curl -s --request POST --data "@$NEWLISTING" "$BASE_URL/new" | jq --raw-output .newId)
[ -n "$ID" ] && [ "$ID" != "null" ] || fail "POST /new did not return an id"
expect "size after POST" "$(curl -s "$BASE_URL/size" | jq .size)" 1
expect "GET /listing/<id>" "$(curl -s "$BASE_URL/listing/$ID" | jq --raw-output .listing.id)" "$ID"

UPDATED=$(jq --arg id "$ID" '.listing.id = $id | .listing.address.city = "Updated"' "$NEWLISTING")
curl -s --request PUT --data "$UPDATED" "$BASE_URL/update" > /dev/null
expect "PUT /update" "$(curl -s "$BASE_URL/listing/$ID" | jq --raw-output .listing.address.city)" "Updated"

expect "GET /listings" "$(curl -s "$BASE_URL/listings" | jq length)" 1
curl -s --request DELETE "$BASE_URL/del/$ID" > /dev/null
expect "size after DELETE" "$(curl -s "$BASE_URL/size" | jq .size)" 0
expect "DELETE unknown id" "$(curl -s -o /dev/null -w '%{http_code}' --request DELETE "$BASE_URL/del/$ID")" 400

echo "All integration tests passed"
