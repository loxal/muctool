#!/usr/bin/env sh

# ops & tooling
docker start teamcity-server
docker start teamcity-agent-merkur
docker start teamcity-agent-venus

# core
docker start muctool
docker start router

# start scripts
#sh bootstrap/init-env-secret.sh

# start blockchain nodes
sh bootstrap/start-blockchain.sh
