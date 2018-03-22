#!/usr/bin/env sh
# Should be executed on the host with `~/buildAgent` directory

version=2017.2.3
service_name=teamcity-server
docker network create main

docker rm -f $service_name
docker run -d -t --name $service_name \
    -e TEAMCITY_SERVER_MEM_OPTS="-Xmx2g -XX:MaxPermSize=270m -XX:ReservedCodeCacheSize=350m" \
    -v /srv/${service_name}:/data/teamcity_server/datadir \
    -v /srv/${service_name}/logs:/opt/teamcity/logs \
    --network main \
    jetbrains/${service_name}:${version}

sudo ~/buildAgent/bin/agent.sh stop kill # stop localhost-agent

# Run for major version upgrades
#cd ~/buildAgent/bin
#    ./install.sh https://ci.loxal.net
#~/buildAgent/bin/agent.sh run

start_ci_agent() {
    docker rm -f teamcity-agent-$1
    docker run -d -t --name teamcity-agent-$1 \
        -e SERVER_URL="https://ci.loxal.net" \
        -e AGENT_NAME=$1 \
        -v /srv/teamcity-agent-$1:/data/teamcity_agent/conf \
        --network main \
        jetbrains/teamcity-agent:${version}
}

start_ci_agent merkur
start_ci_agent venus

#rm -f ~/buildAgent/logs/buildAgent.pid
sudo ~/buildAgent/bin/agent.sh start # run localhost-agent on host machine
