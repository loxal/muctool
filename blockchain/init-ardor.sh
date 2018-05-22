#!/usr/bin/env sh

cd /srv/minion
curl -LO https://www.jelurida.com/ardor-client.sh

#... Interactively install Ardor

cd /srv/minion/ardor
screen -dmS ardor ./run.sh

# start forging
# curl 'http://localhost:27876/nxt?requestType=startForging' --data "secretPhrase=$ARDOR_SECRET_PHRASE_ENCODED"