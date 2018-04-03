#!/usr/bin/env sh

screen -mS ardor /srv/minion/ardor/run.sh

# start forging
curl 'http://localhost:27876/nxt?requestType=startForging' --data "secretPhrase=$ARDOR_SECRET_PHRASE_ENCODED"