#!/usr/bin/env powershell

# tooling
docker start teamcity-server
~/buildAgent/bin/agent.sh start

# misc
docker start service-kit

# core
docker start muctool
docker start http-to-https-redirect
