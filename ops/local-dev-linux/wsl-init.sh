#!/usr/bin/env sh

#sh /mnt/c/Users/alex/my/project/loxal/muctool/ops/local-dev-linux/wsl-init.sh

sudo sed -i -e 's/%sudo\tALL=(ALL:ALL) ALL/%sudo\tALL=(ALL:ALL) NOPASSWD:ALL/g' /etc/sudoers

# needs to be copied as specific permission need to be set
sudo cp -r /mnt/c/Users/alex/.ssh ~/
sudo chown -R minion:minion ~/.ssh
sudo chmod -R 600 ~/.ssh

ln -s /mnt/c/Users/alex/.bash_aliases ~/
ln -s /mnt/c/Users/alex/.gitconfig ~/
mkdir ~/.gradle
ln -s /mnt/c/Users/alex/.gradle/gradle.properties ~/.gradle

sudo apt -y update
sudo apt -y upgrade
sudo apt -y autoremove
sudo apt-get -y install sudo whois vim net-tools rsync netcat uuid curl \
    iputils-ping git unzip screen \
    unattended-upgrades