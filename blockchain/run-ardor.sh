#!/usr/bin/env sh

cd /srv/minion/ardor
screen -dmS ardor ./run.sh

sleep 2
# start forging
curl http://localhost:27876/nxt?requestType=startForging --data "secretPhrase=$ARDOR_SECRET_PHRASE_ENCODED" # does not work: curl: (7) Failed to connect to localhost port 27876: Connection refused