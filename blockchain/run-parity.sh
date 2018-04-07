#!/usr/bin/env sh

# Parity Ethereum Node

SRV_HOME=/srv/minion/parity
#    -p 8180:8180 -p 8545:8545 -p 8546:8546 -p 30303:30303 -p 30303:30303/udp \
docker run -d --name parity \
    -v ${SRV_HOME}:/root/.local/share/io.parity.ethereum \
    parity/parity:stable \
    --base-path /root/.local/share/io.parity.ethereum \
    --ui-interface all --jsonrpc-interface all
    
