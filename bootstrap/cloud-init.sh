#!/usr/bin/env sh

touch /srv/remove_this_test
git clone git@github.com:loxal/muctool.git /srv/muctool
sh /srv/muctool/bootstrap/recover-on-boot.sh
