#!/usr/bin/env sh

sh bootstrap/init-env-secret.sh

# tooling
docker start teamcity-server
docker start teamcity-agent-merkur
docker start teamcity-agent-venus

# core
docker start muctool
docker start router

# start scripts
sh blockchain/run-zcash.sh
