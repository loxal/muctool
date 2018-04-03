#!/usr/bin/env sh

cd /srv/minion
curl -LO https://bob.nem.ninja/nis-0.6.95.tgz
tar xfz nis-*

screen -mS NEM ./nix.runNis.sh

