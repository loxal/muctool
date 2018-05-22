FROM openjdk:10-jdk as main
ENV DEBIAN_FRONTEND noninteractive

MAINTAINER Alexander Orlov <alexander.orlov@loxal.net>

RUN adduser --disabled-password --gecos "" minion
RUN usermod -aG sudo minion
RUN usermod --lock minion

RUN curl -s https://packages.cloud.google.com/apt/doc/apt-key.gpg | apt-key add -
RUN echo 'deb https://packages.cloud.google.com/apt kubernetes-xenial main' > /etc/apt/sources.list.d/kubernetes.list
RUN apt-get update && apt-get -y install sudo whois vim net-tools rsync netcat uuid curl \
    iputils-ping git unzip screen \
    apt-transport-https unattended-upgrades apt-utils man-db \
    openssh-server \
    docker.io kubectl kubeadm kubelet
RUN usermod -aG docker minion
RUN sed -i -e 's/%sudo\tALL=(ALL:ALL) ALL/%sudo\tALL=(ALL:ALL) NOPASSWD:ALL/g' /etc/sudoers

VOLUME /opt /mnt/c /mnt/minion /home/minion
EXPOSE 22 443 13389 5901 5900
EXPOSE 5005
