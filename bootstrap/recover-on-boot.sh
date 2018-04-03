#!/usr/bin/env sh

# tooling
docker start teamcity-server
docker start teamcity-agent-merkur
docker start teamcity-agent-venus

# core
docker start muctool
docker start router

# start scripts
sh bootstrap/init-env-secret.sh

sh blockchain/run-zcash.sh
sh blockchain/run-waves.sh
sh blockchain/run-nem.sh
sh blockchain/run-komodo.sh
sh blockchain/run-heatledger.sh
sh blockchain/run-ardor.sh
