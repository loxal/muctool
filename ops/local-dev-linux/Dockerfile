FROM ubuntu:18.04 as main

MAINTAINER Alexander Orlov <alexander.orlov@loxal.net>

RUN apt-get update; apt-get -y install sudo xrdp vnc4server vim openssh-server openjdk-8-jdk-headless
COPY authorized_keys /root/.ssh/authorized_keys
#COPY /mnt/alex/.ssh /root/aha
#RUN ln -s /mnt/alex/.ssh/ ~
#RUN ln -s /mnt/alex/.bash_aliases ~
#RUN ln -s /mnt/alex/.gitconfig ~

VOLUME /opt /mnt/c /mnt/alex
EXPOSE 22 443 13389 5901 5900
