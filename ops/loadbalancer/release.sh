#!/usr/bin/env sh

k8s_master_node=sky.loxal.net
scp -q -o StrictHostKeyChecking=no root@$k8s_master_node:/etc/letsencrypt/live/loxal.net/fullchain.pem .
scp -q -o StrictHostKeyChecking=no root@$k8s_master_node:/etc/letsencrypt/live/loxal.net/privkey.pem .

docker build \
  --build-arg HASHED_DEFAULT_PASSWORD=$HASHED_DEFAULT_PASSWORD \
  --tag muctool/loadbalancer:latest .

echo $DOCKER_PASSWORD | docker login --username loxal --password-stdin
docker push muctool/loadbalancer:latest

echo $ADMIN_SITE_SECRET | docker login --username minion --password-stdin docker.muctool.de
#docker tag muctool/loadbalancer:latest muctool/loadbalancer:latest
#docker push docker.muctool.de/muctool/loadbalancer:latest
docker push muctool/loadbalancer:latest
