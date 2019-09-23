#!/usr/bin/env sh

curl -LO https://github.com/wavesplatform/Waves/releases/download/v1.1.2/waves-all-1.1.2.jar
screen -dmS waves java -jar /srv/minion/waves/waves-all-*.jar /srv/minion/waves/waves-mainnet.conf