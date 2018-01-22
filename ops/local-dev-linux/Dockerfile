#FROM ubuntu:18.04 as main
FROM ubuntu:latest as main

MAINTAINER Alexander Orlov <alexander.orlov@loxal.net>

RUN apt-get update; apt-get -y install sudo xrdp vnc4server vim openssh-server openjdk-8-jdk-headless
COPY authorized_keys /root/.ssh/authorized_keys

VOLUME /opt
EXPOSE 22 443 13389 5901 5900
