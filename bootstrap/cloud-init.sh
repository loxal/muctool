#!/usr/bin/env sh

# TODO introduce a command to feed a shell script via curl from GitHub

# rescue
#mount -o remount,rw /
#e2fsck -D /dev/sda1 -y

#ip addr add 78.46.236.49/32 dev eth0
ip addr add 78.46.236.49 dev eth0
#ip addr add 2a01:4f8:2c17:2c::1/128 dev eth0
ip addr add 2a01:4f8:1c17:8039::1 dev eth0

git clone git@github.com:loxal/muctool.git /srv/muctool
sh /srv/muctool/bootstrap/recover-on-boot.sh
