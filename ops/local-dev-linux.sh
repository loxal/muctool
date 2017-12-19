#!/usr/bin/env sh

docker.exe rm -f local-dev-linux
docker.exe run -ti --name local-dev-linux \
    -p 22:22 \
    -v /home/alex/srv/local-dev-linux:/opt \
    --network dev \
    openjdk:9-jre bash

#docker.exe ps
#docker.exe rm -f local-dev-linux
#docker.exe ps
#docker.exe version


