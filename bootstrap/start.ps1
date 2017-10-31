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
