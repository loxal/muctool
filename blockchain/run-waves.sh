#!/usr/bin/env sh

cd /srv/minion/waves
curl -LO https://github.com/wavesplatform/Waves/releases/download/v1.1.5/waves-all-1.1.5.jar
screen -dmS waves java -jar /srv/minion/waves/waves-all-*.jar /srv/minion/waves/waves-mainnet.conf