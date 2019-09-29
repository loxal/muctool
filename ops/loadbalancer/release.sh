#!/usr/bin/env sh

k8s_master_node=sky.loxal.net
scp -q -o StrictHostKeyChecking=no root@$k8s_master_node:/etc/letsencrypt/live/loxal.net/fullchain.pem .
scp -q -o StrictHostKeyChecking=no root@$k8s_master_node:/etc/letsencrypt/live/loxal.net/privkey.pem .

#cp -a /etc/letsencrypt etc-letsencrypt-copy
docker build \
    --build-arg HASHED_DEFAULT_PASSWORD=$HASHED_DEFAULT_PASSWORD \
    --tag loxal/loadbalancer:latest .
#rm -rf etc-letsencrypt-copy

echo $DOCKER_PASSWORD | docker login --username loxal --password-stdin
docker push loxal/loadbalancer:latest
docker tag loxal/loadbalancer:latest muctool/loadbalancer:latest
docker push muctool/loadbalancer:latest
