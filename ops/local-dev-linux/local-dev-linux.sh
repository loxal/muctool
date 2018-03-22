#!/usr/bin/env sh

docker.exe build --no-cache --pull --tag loxal/local-dev-linux:latest .

docker.exe network create dev
docker.exe rm -f local-dev-linux
docker.exe run -d -t --name local-dev-linux --hostname nux \
    -p 22:22 \
    -p 23389:3389 \
    -p 5901:5901 \
    -p 5900:5900 \
    -v /c/Users/alex/my/local-dev-linux:/home/alex \
    -v /c/Users/alex/my/local-dev-linux:/mnt/my \
    -v /c/:/mnt/c \
    -v /c/Users/alex:/mnt/alex \
    --network dev \
    loxal/local-dev-linux:latest

docker.exe exec -i local-dev-linux /etc/init.d/ssh start
docker.exe exec -i local-dev-linux /etc/init.d/xrdp start
#docker.exe exec -i local-dev-linux cp /root/.bashrc /home/alex #TODO test if this works
docker.exe exec -i local-dev-linux ln -s /mnt/alex/.gradle/gradle.properties /home/alex/.gradle
docker.exe ps

#term_.command.removeKnownHostByIndex(4)

#TODO make "cfg" folder available



