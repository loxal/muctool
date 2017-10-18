#!/usr/bin/env powershell

Set-StrictMode -Version Latest
$ErrorActionPreference = "Stop"
$PSDefaultParameterValues["*:ErrorAction"] = "Stop"

$version = "2017.1.5"
$docker_network = "dev"

docker rm -f teamcity-server
docker run -d -t --name teamcity-server `
    -p 8111:8111 `
    -v ~/srv/teamcity_server:/data/teamcity_server/datadir `
    -v ~/srv/teamcity_server/logs:/opt/teamcity/logs `
    jetbrains/teamcity-server:$version

~/buildAgent/bin/agent.sh stop
~/buildAgent/bin/agent.sh start # run agent on host machine

docker rm -f teamcity-agent
docker run -d -t --name teamcity-agent `
    -e SERVER_URL="https://ci.loxal.net" `
    -v ~/srv/teamcity_agent:/data/teamcity_agent/conf `
    jetbrains/teamcity-agent:$version
