#!/usr/bin/env sh

docker.exe build --tag loxal/local-dev-linux:latest .

docker.exe network create dev
docker.exe rm -f local-dev-linux
docker.exe run -d -t --name local-dev-linux \
    -p 23389:3389 \
    -p 5901:5901 \
    -p 5900:5900 \
    -v /c/:/mnt/c \
    -v /c/Users/alex:/mnt/alex \
    --network dev \
    loxal/local-dev-linux:latest
#    -p 22:22 \
#    -v /home/alex/srv/local-dev-linux:/mnt \
#    ubuntu:bionic
#    alpine:3.7
#    debian:latest bash
#    openjdk:9-jre bash

#docker.exe exec -i local-dev-linux /etc/init.d/ssh start
docker.exe exec -i local-dev-linux /etc/init.d/xrdp start
docker.exe ps

#term_.command.removeKnownHostByIndex(5)


