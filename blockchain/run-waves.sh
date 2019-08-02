#!/usr/bin/env sh

curl -LO https://github.com/wavesplatform/Waves/releases/download/v1.0.2/waves-all-1.0.2.jar
screen -dmS waves java -jar /srv/minion/waves/waves-all-*.jar /srv/minion/waves/waves-mainnet.conf