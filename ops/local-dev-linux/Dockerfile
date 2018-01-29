#FROM ubuntu:18.04 as main
FROM openjdk:8-jdk-slim as main

MAINTAINER Alexander Orlov <alexander.orlov@loxal.net>

RUN adduser --disabled-password --gecos "" alex
RUN usermod -aG sudo alex
RUN usermod --lock alex

RUN apt-get update
RUN apt-get -y install apt-utils
RUN apt-get -y install sudo
RUN apt-get -y install openssh-server
#RUN apt-get -y install apt-utils # TODO check why it's required via red hint during installation
RUN apt-get -y install xrdp vnc4server vim
RUN apt-get -y install openjdk-8-jdk-headless

RUN ln -s /mnt/alex/.ssh ~
RUN ln -s /mnt/alex/.bash_aliases ~
RUN ln -s /mnt/alex/.gitconfig ~

#RUN ln -s /mnt/alex/.ssh /home/alex/.ssh
#RUN ln -s /mnt/alex/.ssh /home/alex/.ssh1
#RUN ln -s /mnt/alex/.bash_aliases /home/alex/.bash_aliases
#RUN ln -s /mnt/alex/.bash_aliases /home/alex/.bash_aliases1
#RUN ln -s /mnt/alex/.gitconfig /home/alex/.gitconfig
#RUN ln -s /mnt/alex/.gitconfig /home/alex/.gitconfig1

VOLUME /opt /mnt/c /mnt/alex /home/alex
EXPOSE 22 443 13389 5901 5900
