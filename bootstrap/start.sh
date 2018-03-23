#!/usr/bin/env sh

sh bootstrap/recover-on-boot.sh

# mining & forging
function runZcashMining { # does not work
#    nohup ~/minion/miner/mine-zcash-cpu.sh &
    screen -mS ZCash ~/minion/miner/mine-zcash-cpu.sh
    echo "mine-zcash-cpu.sh started"
}
runZcashMining

function runNemServer {
    cd ~/minion/miner/nem-server
#    nohup ./nix.runNis.sh &
    screen -mS NIS ./nix.runNis.sh
    echo "runNemServer - nix.runNis.sh started"
#    sleep 15m
#    nohup ./nix.runNcc.sh &
#    screen -mS NCC ./nix.runNcc.sh
#    echo "runNemServer - nix.runNcc.sh started, START forging in browser now..."
}
runNemServer

function runHeatLedger {
    cd ~/minion/miner/heatledger-*
    screen -mS heatledger bin/heatledger
#    nohup bin/heatledger &
    echo "Heat Ledger started, START forging now..."
    echo "curl 'http://localhost:7733/api/v1/mining/start/secret%20phrase?api_key=PASSWORD'"
}
runHeatLedger

jps -l
