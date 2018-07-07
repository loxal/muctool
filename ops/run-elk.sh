#!/usr/bin/env sh

docker swarm init --advertise-addr 88.99.37.232
docker stack deploy -c elk-stack.yaml --prune elk