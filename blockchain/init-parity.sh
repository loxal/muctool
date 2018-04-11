#!/usr/bin/env sh

# Install Parity Ethereum Node

SRV_HOME=/srv/minion/parity
mkdir -p $SRV_HOME
touch $SRV_HOME/config.toml
docker rm -f parity
docker run -ti --name parity \
    -v ${SRV_HOME}:/root/.local/share/io.parity.ethereum \
    parity/parity:stable \
    --base-path /root/.local/share/io.parity.ethereum
