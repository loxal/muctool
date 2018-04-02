#!/usr/bin/env sh

# Install HEAT Ledger
#    https://heatbrowser.com/report.html
#    http://heatnodes.org/?page_id=329
#    https://heatwallet.com/nodes.cgi

echo "$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

MINION_HOME=/srv/minion
HEAT_VERSION=2.4.0
HOST_NAME=`hostname`.loxal.net
HEAT_API_KEY=INSERT_API_KEY

sudo mkdir $MINION_HOME
sudo chown minion:minion $MINION_HOME
cd $MINION_HOME

# download implementation
curl -LO https://github.com/Heat-Ledger-Ltd/heatledger/releases/download/v${HEAT_VERSION}/heatledger-${HEAT_VERSION}.zip
unzip heatledger-*.zip
rm heatledger-*.zip
mv heatledger-${HEAT_VERSION} heatledger
cd heatledger

# download blockchain
curl -LO https://heatbrowser.com/blockchain.tgz
tar xzvf blockchain.tgz
rm blockchain.tgz

cp conf/heat-default.properties conf/heat.properties
cp ${MINION_HOME}/conf/heat.properties conf/heat.properties

#bin/heatledger &
screen -mS HEATledger bin/heatledger

# on sky.loxal.net or any other server running a HEAT node
# curl http://localhost:7733/api/v1/tools/hallmark/encode/${HOST_NAME}/200/2016-01-01/${HEAT_LEDGER_SECRET_PHRASE_ESCAPED} # obtain hallmark

# wait until chain is synced
# curl http://localhost:7733/api/v1/mining/start/${HEAT_LEDGER_SECRET_PHRASE_ESCAPED}?api_key=${HEAT_API_KEY} # start forging, replace secret phrase’ spaces with “%20”