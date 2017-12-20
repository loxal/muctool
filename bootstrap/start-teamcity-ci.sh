#!/usr/bin/env sh

version=2017.2.1

docker rm -f teamcity-server
docker run -d -t --name teamcity-server \
    -p 8111:8111 \
    -e TEAMCITY_SERVER_MEM_OPTS="-Xmx2g -XX:MaxPermSize=270m -XX:ReservedCodeCacheSize=350m" \
    -v ~/srv/teamcity_server:/data/teamcity_server/datadir \
    -v ~/srv/teamcity_server/logs:/opt/teamcity/logs \
    --network main \
    jetbrains/teamcity-server:$version

~/buildAgent/bin/agent.sh stop

start_ci_agent() {
    docker rm -f teamcity-agent-$1
    docker run -d -t --name teamcity-agent-$1 \
        -e SERVER_URL="https://ci.loxal.net" \
        -e AGENT_NAME=$1 \
        -v ~/srv/teamcity-agent-$1:/data/teamcity_agent/conf \
        --network main \
        jetbrains/teamcity-agent:$version
}

start_ci_agent Merkur
start_ci_agent Venus

~/buildAgent/bin/agent.sh start # run agent on host machine
