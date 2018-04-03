#!/usr/bin/env sh

# TODO introduce a command to feed a shell script via curl from GitHub

touch /srv/remove_this_test
git clone git@github.com:loxal/muctool.git /srv/muctool
sh /srv/muctool/bootstrap/recover-on-boot.sh
