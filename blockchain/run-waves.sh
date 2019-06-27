#!/usr/bin/env sh

#curl -LO https://github.com/wavesplatform/Waves/releases/download/v0.14.6/waves-all-0.14.6.jar
curl -LO https://github.com/wavesplatform/Waves/releases/download/v1.0.0/waves-all-1.0.0.jar
screen -dmS waves java -jar /srv/minion/waves/waves-all-*.jar /srv/minion/waves/waves-mainnet.conf