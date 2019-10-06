#!/usr/bin/env sh

docker build \
  --build-arg HASHED_DEFAULT_PASSWORD=$HASHED_DEFAULT_PASSWORD \
  --tag muctool/muc-router:latest .

echo $DOCKER_PASSWORD | docker login --username loxal --password-stdin
docker push muctool/muc-router:latest

docker tag muctool/muc-router:latest loxal/muc-router:latest
docker push loxal/muc-router:latest

echo $ADMIN_SITE_SECRET | docker login --username minion --password-stdin docker.muctool.de
docker tag muctool/muc-router:latest docker.muctool.de/muctool/muc-router:latest
docker push docker.muctool.de/muctool/muc-router:latest
