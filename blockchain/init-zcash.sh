#!/usr/bin/env sh

# Install ZCash

#cd /srv/minion
#curl https://z.cash/downloads/zcash-1.0.15-linux64.tar.gz -o zcash-linux64.tar.gz
#tar -xvf zcash-linux64.tar.gz
#cd zcash-*


wget -qO - https://apt.z.cash/zcash.asc | sudo apt-key add -
echo "deb [arch=amd64] https://apt.z.cash/ jessie main" | sudo tee /etc/apt/sources.list.d/zcash.list

sudo apt-get -y update && sudo apt-get install -y zcash
