#!/usr/bin/env sh

# tooling
docker start teamcity-server
docker start teamcity-agent-Merkur
docker start teamcity-agent-Venus

# misc
docker start service-kit

# core
docker start muctool
docker start router

