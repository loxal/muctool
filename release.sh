#!/usr/bin/env sh
# DEPRECATED - CAN_BE_DELETED

docker_network=main
docker network create main
docker_image=muctool
docker_tag=latest

cd service
docker build --pull --tag loxal/${docker_image}:${docker_tag} .
cd ..

docker rm -f $docker_image
docker run --privileged -d \
    --env MUCTOOL_GITHUB_CLIENT_ID=$MUCTOOL_GITHUB_CLIENT_ID \
    --env MUCTOOL_GITHUB_CLIENT_SECRET=$MUCTOOL_GITHUB_CLIENT_SECRET \
    --env SECURITY_USER_PASSWORD=$SECURITY_USER_PASSWORD \
    --env BUILD_NUMBER=$BUILD_NUMBER \
    --env SCM_HASH=$SCM_HASH \
    --label buildCounter=$BUILD_COUNTER \
    --label sans-backing_service \
    --name $docker_image \
    --restart=unless-stopped \
    --network $docker_network \
    loxal/${docker_image}:${docker_tag}

sh release-router.sh

danglingImages=$(docker images -f "dangling=true" -q)
if [ "$danglingImages" ]; then
    docker rmi -f $danglingImages # cleanup, GC for dangling images
else
    echo "There are no dangling Docker images"
fi