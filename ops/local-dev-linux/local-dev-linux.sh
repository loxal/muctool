#!/usr/bin/env sh

docker.exe build --no-cache --pull --tag loxal/local-dev-linux:latest .

docker.exe network create dev
docker.exe rm -f local-dev-linux
docker.exe run -d -t --name local-dev-linux --hostname nux \
    -p 1180:1180 \
    -p 8001:8001 \
    -p 1122:22 \
    -p 23389:3389 \
    -p 5901:5901 \
    -p 5900:5900 \
    -v /c/Users/alex/my/local-dev-linux:/home/alex \
    -v /c/:/mnt/c \
    -v /c/Users/alex:/mnt/alex \
    --network dev \
    loxal/local-dev-linux:latest
#    -v /c/Users/alex/my/local-dev-linux:/mnt/my \

docker.exe exec -i local-dev-linux /etc/init.d/ssh start
docker.exe exec -i local-dev-linux /etc/init.d/xrdp start
docker.exe exec -i local-dev-linux cp /root/.bashrc /home/alex #TODO test if this works
docker.exe ps

#term_.command.removeKnownHostByIndex(5)

#TODO put into IntelliJ: bash -c 'ssh root@10.0.75.1 -p 1122'
#TODO make "cfg" folder available 



