#!/usr/bin/env sh

docker build --tag docker.muctool.de/muctool/muctool:latest .

echo $ADMIN_SITE_SECRET | docker login --username minion --password-stdin docker.muctool.de
docker push docker.muctool.de/muctool/muctool:latest

echo $DOCKER_PASSWORD | docker login --username loxal --password-stdin
docker tag docker.muctool.de/muctool/muc-router:latest muctool/muc-router:latest
docker push muctool/muc-router:latest