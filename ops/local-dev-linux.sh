#!/usr/bin/env sh

docker $DOCKER_OPTS run -it --name local-dev-linux \
    -p 22:22 \
    -v ~/srv/local-dev-linux:/root \
    openjdk:9-jdk

docker $DOCKER_OPTS ps


