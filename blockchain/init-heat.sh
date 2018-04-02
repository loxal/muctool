#!/usr/bin/env sh

# Install HEAT Ledger
#    https://heatbrowser.com/report.html
#    http://heatnodes.org/?page_id=329
#    https://heatwallet.com/nodes.cgi

echo "$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
echo $BASH_SOURCE[0]
echo $BASH_SOURCE

MINION_HOME=/srv/minion
HEAT_VERSION=2.4.0
SECRET_PHRASE_WITHOUT_BLANK_SPACES=INSERT_SECRET_PHRASE
HEAT_API_KEY=INSERT_API_KEY

sudo mkdir $MINION_HOME
sudo chown minion:minion $MINION_HOME
cd $MINION_HOME

# download implementation
curl -LO https://github.com/Heat-Ledger-Ltd/heatledger/releases/download/v${HEAT_VERSION}/heatledger-${HEAT_VERSION}.zip
unzip heatledger-*.zip
rm heatledger-*.zip
cd heatledger-${HEAT_VERSION}

# download blockchain
curl -LO https://heatbrowser.com/blockchain.tgz
tar xzvf blockchain.tgz
rm blockchain.tgz

#    HOST_NAME=`hostname`.loxal.net
#    HEAT_VERSION=1.1.0
#
#    cd ~/minion/miner
#    curl -LO https://github.com/Heat-Ledger-Ltd/heatledger/releases/download/v${HEAT_VERSION}/heatledger-${HEAT_VERSION}.zip
#    unzip heatledger-*.zip
#    cd heatledger-*
#
#    # download blockchain
#    curl -LO https://heatbrowser.com/blockchain.tgz
#    tar xzvf blockchain.tgz
#    rm blockchain.tgz

#    cp conf/heat-default.properties conf/heat.properties
#    cp ../heatledger-vPrevious/conf/heat.properties conf/heat.properties

##########    vim conf/heat.properties

##########    screen -mS heatledger bin/heatledger

# on sky.loxal.net or any other server running a HEAT node
# curl http://localhost:7733/api/v1/tools/hallmark/encode/${HOST_NAME}/200/2016-01-01/${SECRET_PHRASE_WITHOUT_BLANK_SPACES} # obtain hallmark

# wait until chain is synced
########## curl http://localhost:7733/api/v1/mining/start/${SECRET_PHRASE_WITHOUT_BLANK_SPACES}?api_key=${HEAT_API_KEY} # start forging, replace secret phrase’ spaces with “%20”

######################################

#      mkdir ~/heatledger
#      cd ~/heatledger
#      wget https://github.com/Heat-Ledger-Ltd/heatledger/releases/download/v2.4.0/heatledger-2.4.0.zip
#      unzip heatledger-2.4.0.zip

#  NOTE: once you've caught up, stop your server. then, to start forging, run these commands:
#      touch heat.properties
#      read -s -p "Enter heat passphrase (WARNING: will be stored in heat.properties in plain text): " H && echo "heat.startForging=$H" >> heat.properties && H=''
#      cp heat.properties heatledger-2.4.0/conf/
#
#      screen -mS HEAT
#      cd ~/heatledger/heatledger-2.4.0/
#      ./bin/heatledger
#
#   To disconnect from screen, hit CONTROL+A, then d
#    to watch log: tail -f ./heatledger.log
#    to reconnect to screen to stop server: screen -r

#  Having trouble loading the blockchain? Try a blockchain backup from this node (infrequently updated):
#    cd ~/heatledger/heatledger-2.4.0/
#    wget https://heatbrowser.com/blockchain.tgz
#    tar -zxvf blockchain.tgz
#    ./bin/heatledger