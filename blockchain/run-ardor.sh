#!/usr/bin/env sh

cd /srv/minion/ardor
screen -mS Ardor ./run.sh

# start forging
# curl 'http://localhost:27876/nxt?requestType=startForging' --data "secretPhrase=$ARDOR_SECRET_PHRASE_ENCODED"