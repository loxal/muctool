#!/usr/bin/env sh

# Local DEV Linux - NUX

docker build --pull --tag loxal/nux:latest .

docker network create dev
docker rm -f nux
docker run -d -t --name nux --hostname nux \
    -p 1122:22 \
    -p 23389:3389 \
    -p 5901:5901 \
    -p 5900:5900 \
    -p 5005:5005 \
    -p 1180:1180 \
    -p 8001:8001 \
    -v /c/:/mnt/c \
    -v /c/Users/alex/my/nux:/home/minion \
    -v /c/Users/alex:/mnt/minion \
    --network dev \
    loxal/nux:latest

docker exec nux /etc/init.d/ssh start
docker exec nux ln -s /usr/lib/jvm/java-10-openjdk-amd64 /opt/jdk
docker exec nux ssh-keygen -f /home/minion/.ssh/known_hosts -R localhost
docker exec nux sh -c "cp -rvL /home/minion/.ssh /root/; chmod 0600 /root/.ssh/id_rsa"

#lanWiFiIPv4=172.25.144.1 # works for both / Ethernet adapter vEthernet (nat)
lanWiFiIPv4=172.31.107.145 # works for both / Ethernet adapter vEthernet (Default Switch)
docker exec -d nux ssh -fnNT -L 2375:localhost:2375 minion@$lanWiFiIPv4 # Docker
docker exec -d nux ssh -fnNT -L 6445:${lanWiFiIPv4}:6445 minion@localhost # Kubernetes

# container-external setup
ssh -fnNT -L 5005:localhost:5005 minion@localhost -p 1122 # Java
