#!/usr/bin/env pwsh

Set-StrictMode -Version Latest
$ErrorActionPreference = "Stop"
$PSDefaultParameterValues["*:ErrorAction"] = "Stop"

# tooling
docker start teamcity-server
~/buildAgent/bin/agent.sh start

# misc
docker start service-kit

# core
docker start muctool
docker start router

# mining & forging
function runZcashMining {
    nohup ~/minion/miner/mine-zcash-cpu.sh &
    echo "mine-zcash-cpu.sh started"
}
runZcashMining

function runNemServer {
    cd ~/minion/miner/nem-server
    nohup ./nix.runNis.sh &
    echo "runNemServer - nix.runNis.sh started"
#    sleep 15m
    nohup ./nix.runNcc.sh &
    echo "runNemServer - nix.runNcc.sh started, START forging now..."
    # start mining in browser
}
runNemServer

function runHeatLedger {
    cd ~/minion/miner/heatledger-*
#    screen -mS heatledger bin/heatledger
    nohup bin/heatledger &
    echo "Heat Ledger started, START forgin now..."
    echo "curl 'http://localhost:7733/api/v1/mining/start/secret%20phrase?api_key=PASSWORD'"
}
runHeatLedger
