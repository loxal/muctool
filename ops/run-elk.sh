#!/usr/bin/env sh

docker swarm init --advertise-addr 88.99.37.232
docker swarm join --token SWMTKN-1-blub-blib 88.99.37.232:2377
docker stack deploy -c elk-stack.yaml --prune elk