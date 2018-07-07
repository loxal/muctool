#!/usr/bin/env sh
# Should be executed on the host with `~/buildAgent` directory

version=2018.1
service_name=teamcity-server
network=main
docker network create $network

docker rm -f $service_name
docker run -d -t --name $service_name \
    -e TEAMCITY_SERVER_MEM_OPTS="-Xmx2g -XX:MaxPermSize=270m -XX:ReservedCodeCacheSize=350m" \
    -v /srv/${service_name}:/data/teamcity_server/datadir \
    --network $network \
    jetbrains/${service_name}:${version}
docker update --restart=unless-stopped $service_name

sudo ~/BuildAgent/bin/agent.sh stop kill # stop localhost-agent

# Run for major version upgrades
#cd ~/BuildAgent/bin
#    ./install.sh https://ci.loxal.net
#~/BuildAgent/bin/agent.sh run

start_ci_agent() {
    docker rm -f teamcity-agent-$1
    docker run -d -t --name teamcity-agent-$1 \
        -e SERVER_URL="https://ci.loxal.net" \
        -e AGENT_NAME=$1 \
        -v /srv/teamcity-agent-$1:/data/teamcity_agent/conf \
        --network $network \
        jetbrains/teamcity-agent:${version}
    docker update --restart=unless-stopped teamcity-agent-$1
}

start_ci_agent merkur
start_ci_agent venus

sudo ~/BuildAgent/bin/agent.sh start # run localhost-agent on host machine
