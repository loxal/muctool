#!/usr/bin/env sh

# floating IP
sudo ip addr add 78.46.236.49 dev eth0
sudo ip addr add 78.47.232.28 dev eth0 # alt-https
sudo ip addr add 2a01:4f8:1c17:8039::1 dev eth0

# ops & tooling
docker start teamcity-server
docker start teamcity-agent-merkur
docker start teamcity-agent-venus

docker start ops-es

# core
docker start muctool
docker start router

# start blockchain nodes
sh bootstrap/start-blockchain.sh

sh bootstrap/run-DNS-crypt.sh
