#!/usr/bin/env sh

cd service
docker build --pull --tag docker.muctool.de/muctool/muctool:latest .

echo $ADMIN_SITE_SECRET | docker login --username minion --password-stdin docker.muctool.de
docker push docker.muctool.de/muctool/muctool:latest