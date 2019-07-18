#!/usr/bin/env sh

cd /srv/minion
git clone https://github.com/jl777/komodo
cd komodo
git checkout beta
git pull
./zcutil/fetch-params.sh
./zcutil/build.sh -j4

#/srv/minion/komodo/src/komodo-cli getnettotals