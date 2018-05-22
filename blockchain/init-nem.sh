#!/usr/bin/env sh

cd /srv/minion
curl -LO https://bob.nem.ninja/nis-0.6.95.tgz
tar xfz nis-*

screen -dmS nem ./nix.runNis.sh
