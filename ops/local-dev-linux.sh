#!/usr/bin/env sh

docker.exe run -t --name local-dev-linux \
    -p 22:22 \
    -v ~/srv/local-dev-linux:/root \
    openjdk:9-jdk

docker.exe $DOCKER_OPTS ps
docker.exe $DOCKER_OPTS rm -f local-dev-linux
#docker.exe $DOCKER_OPTS ps


