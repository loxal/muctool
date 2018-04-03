#!/usr/bin/env sh

mkdir /srv/minion/waves
cd /srv/minion/waves

#curl -LO https://github.com/wavesplatform/Waves/releases/download/v0.10.3/waves-all-0.10.3.jar

screen -mS Waves java -jar waves-all-*.jar waves-mainnet.conf