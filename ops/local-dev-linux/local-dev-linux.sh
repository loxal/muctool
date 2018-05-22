#!/usr/bin/env sh

docker build --pull --tag loxal/local-dev-linux:latest .

docker network create dev
docker rm -f local-dev-linux
docker run -d -t --name local-dev-linux --hostname nux \
    -p 1122:22 \
    -p 23389:3389 \
    -p 5901:5901 \
    -p 5900:5900 \
    -p 5005:5005 \
    -v /c/Users/alex/my/local-dev-linux:/home/minion \
    -v /c/:/mnt/c \
    -v /c/Users/alex:/mnt/my \
    --network dev \
    loxal/local-dev-linux:latest

docker exec -i local-dev-linux /etc/init.d/ssh start
docker exec -i local-dev-linux ln -s /usr/lib/jvm/java-10-openjdk-amd64 /opt/jdk
docker exec -i local-dev-linux sudo sh -c "cp -rvL /home/minion/.ssh /root/; chmod 0600 /root/.ssh/id_rsa"

lanWiFiIPv4=192.168.10.119
docker exec -d local-dev-linux ssh -4fnNT -L 2375:localhost:2375 minion@$lanWiFiIPv4 # Docker
docker exec -d local-dev-linux ssh -4fnNT -L 6445:${lanWiFiIPv4}:6445 minion@localhost # Kubernetes

# container-external setup
ssh -fnNT -L 5005:localhost:5005 minion@localhost -p 1122 # Java 
