#!/usr/bin/env sh

docker.exe build --tag loxal/local-dev-linux:latest .

docker.exe rm -f local-dev-linux
docker.exe run -d -t --name local-dev-linux \
    -p 22:22 \
    -v /c/:/mnt/c \
    -v /c/Users/alex:/mnt/alex \
    --network dev \
    loxal/local-dev-linux:latest
#    -v /home/alex/srv/local-dev-linux:/mnt \
#    ubuntu:bionic
#    alpine:3.7
#    debian:latest bash
#    openjdk:9-jre bash

docker.exe exec -i local-dev-linux /etc/init.d/ssh start
docker.exe ps

#term_.command.removeKnownHostByIndex(5)



