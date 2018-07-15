#!/usr/bin/env sh

sudo ln -s /mnt/c/Users/alex /mnt/minion # only for WSL, not for native Linux
#sudo ln -s /mnt/hgfs/alex /mnt/minion # only for native Linux, not for WSL

#sh /mnt/c/Users/alex/my/project/loxal/muctool/ops/nux/wsl-init.sh

sudo sed -i -e 's/%sudo\tALL=(ALL:ALL) ALL/%sudo\tALL=(ALL:ALL) NOPASSWD:ALL/g' /etc/sudoers

cd
# needs to be copied as specific permission need to be set
sudo cp -r /mnt/minion/.ssh ~/
sudo chown -R 1000:1000 ~/.ssh
sudo chmod -R 700 ~/.ssh

ln -s /mnt/minion/.gitconfig ~/
ln -s /mnt/minion/.gradle ~/
ln -s /mnt/minion/.kube ~/
ln -s /mnt/minion/my ~/

sudo ln -s /mnt/minion/my/nux
ln -s /mnt/nux/.bash_aliases ~/

sudo apt -y update
sudo apt -y upgrade
sudo apt -y autoremove
sudo apt-get -y install sudo whois vim net-tools rsync netcat uuid curl \
    iputils-ping git unzip screen \
    unattended-upgrades
