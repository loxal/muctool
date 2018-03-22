#!/usr/bin/env sh

# tooling
docker start teamcity-server
docker start teamcity-agent-merkur
docker start teamcity-agent-venus

# core
docker start muctool
docker start router

