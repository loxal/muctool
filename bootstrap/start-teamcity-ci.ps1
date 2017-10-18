#!/usr/bin/env powershell

Set-StrictMode -Version Latest
$ErrorActionPreference = "Stop"
$PSDefaultParameterValues["*:ErrorAction"] = "Stop"

$version = "2017.1.5"
$docker_network = "dev"
$service_name = "teamcity-server"

docker rm -f $service_name
docker run -d -t --name $service_name `
    -p 8111:8111 `
    -v ~/srv/${service_name}:/data/teamcity_server/datadir `
    -v ~/srv/${service_name}/logs:/opt/teamcity/logs `
    jetbrains/${service_name}:$version

~/buildAgent/bin/agent.sh stop
~/buildAgent/bin/agent.sh start # run agent on host machine

docker rm -f teamcity-agent
docker run -d -t --name teamcity-agent `
    -e SERVER_URL="https://ci.loxal.net" `
    -v ~/srv/teamcity-agent:/data/teamcity_agent/conf `
    jetbrains/teamcity-agent:$version
