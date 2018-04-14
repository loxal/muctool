#!/usr/bin/env sh

#ln -s /mnt/c/Users/alex/.ssh ~/     # needs to be copied as specific permission need to be set
ln -s /mnt/c/Users/alex/.bash_aliases ~/
ln -s /mnt/c/Users/alex/.gitconfig ~/
ln -s /mnt/c/Users/alex/.gradle/gradle.properties ~/.gradle

#sudo visudo # manual step

sudo apt update
sudo apt upgrade
sudo apt autoremove -y
sudo apt install openjdk-8-jdk-headless -y
sudo apt install docker.io -y