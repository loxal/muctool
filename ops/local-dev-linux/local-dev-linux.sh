#!/usr/bin/env sh

docker.exe build --no-cache --pull --tag loxal/local-dev-linux:latest .

docker.exe network create dev
docker.exe rm -f local-dev-linux
docker.exe run -d -t --name local-dev-linux --hostname nux \
    -p 22:22 \
    -p 23389:3389 \
    -p 5901:5901 \
    -p 5900:5900 \
        --expose=1025-9000/tcp \
        --expose=1025-9000/udp \
    -v /c/Users/alex/my/local-dev-linux:/home/minion \
    -v /c/:/mnt/c \
    -v /c/Users/alex:/mnt/my \
    --network dev \
    loxal/local-dev-linux:latest

docker.exe exec -i local-dev-linux /etc/init.d/ssh start
#docker.exe exec -i local-dev-linux /etc/init.d/xrdp start
docker.exe exec -i local-dev-linux ln -s /mnt/my/.gradle/gradle.properties /home/minion/.gradle
docker.exe ps

#term_.command.removeKnownHostByIndex(4)

#TODO make "cfg" folder available



