FROM ubuntu:18.04

MAINTAINER Alexander Orlov <alexander.orlov@loxal.net>

RUN apt-get update; apt-get -y install xrdp vnc4server vim openssh-server openjdk-9-jdk-headless
COPY authorized_keys /root/.ssh/authorized_keys

VOLUME /opt
EXPOSE 22 443 13389 5901 5900
