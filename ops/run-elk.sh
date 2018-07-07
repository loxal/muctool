#!/usr/bin/env sh

docker swarm leave --force
docker swarm init --advertise-addr 88.99.37.232
sysctl -w vm.max_map_count=262144
docker stack rm elk
docker stack deploy -c elk-stack.yaml --prune elk