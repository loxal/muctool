#!/usr/bin/env sh

screen -mS heatledger /srv/minion/heatledger/bin/heatledger

# wait until chain is synced
# curl http://localhost:7733/api/v1/mining/start/${HEAT_LEDGER_SECRET_PHRASE_ESCAPED}?api_key=${HEAT_API_KEY} # start forging, replace secret phrase' spaces with "%20"