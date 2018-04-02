FROM openjdk:10-jdk as main
ENV DEBIAN_FRONTEND noninteractive

MAINTAINER Alexander Orlov <alexander.orlov@loxal.net>

RUN adduser --disabled-password --gecos "" minion
RUN usermod -aG sudo minion
RUN usermod --lock minion

RUN apt-get update
RUN apt-get -y install apt-utils 
RUN apt-get -y install sudo whois vim net-tools rsync netcat uuid curl iputils-ping git unzip docker.io
RUN apt-get -y install openssh-server

RUN usermod -aG docker minion

RUN sed -i -e 's/%sudo\tALL=(ALL:ALL) ALL/%sudo\tALL=(ALL:ALL) NOPASSWD:ALL/g' /etc/sudoers

VOLUME /opt /mnt/c /mnt/minion /home/minion
EXPOSE 22 443 13389 5901 5900
EXPOSE 5005/tcp
EXPOSE 5005/udp
