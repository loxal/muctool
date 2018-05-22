#!/usr/bin/env sh

sudo ifconfig eth0 78.46.236.49
sudo ip addr add 78.46.236.49 dev eth0
sudo ip addr add 2a01:4f8:1c17:8039::1 dev eth0

# ops & tooling
docker start teamcity-server
docker start teamcity-agent-merkur
docker start teamcity-agent-venus

# core
docker start muctool
docker start router

# start scripts
sh bootstrap/init-env.sh

# start blockchain nodes
sh bootstrap/start-blockchain.sh
