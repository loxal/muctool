#FROM openjdk:8-jdk-slim as main
#FROM buildpack-deps:sid-scm as main
#FROM openjdk:9-jdk as main
FROM openjdk:10-jdk as main
ENV DEBIAN_FRONTEND noninteractive

MAINTAINER Alexander Orlov <alexander.orlov@loxal.net>

RUN adduser --disabled-password --gecos "" alex
RUN usermod -aG sudo alex
RUN usermod --lock alex

RUN apt-get update
RUN apt-get -y install apt-utils 
RUN apt-get -y install sudo
RUN apt-get -y install whois
RUN apt-get -y install openssh-server
RUN apt-get -y install xrdp vnc4server vim
#RUN apt-get -y install openjdk-8-jdk-headless

RUN ln -s /mnt/alex/.ssh ~
RUN ln -s /mnt/alex/.bash_aliases ~
RUN ln -s /mnt/alex/.gitconfig ~

RUN sed -i -e 's/%sudo\tALL=(ALL:ALL) ALL/%sudo\tALL=(ALL:ALL) NOPASSWD:ALL/g' /etc/sudoers
# TODO gradle.properties to involve 

VOLUME /opt /mnt/c /mnt/alex /home/alex
EXPOSE 22 443 13389 5901 5900
