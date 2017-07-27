#!/usr/bin/env sh
set -e

./gradlew singleJar

DOCKER_IMAGE=muctool
DOCKER_TAG=latest

# docker login -u loxal
docker build --tag=loxal/$DOCKER_IMAGE:$DOCKER_TAG .
docker push loxal/$DOCKER_IMAGE:$DOCKER_TAG
docker rm -f $DOCKER_IMAGE
docker run -d -p 180:8300 --env MY_ENV=$my_env --env MY_ENV_SINGLE --label jvm_lang=kotlin --label sans-backing_service --name $DOCKER_IMAGE loxal/$DOCKER_IMAGE:$DOCKER_TAG

danglingImages=$(docker images -f 'dangling=true' -q)
if [ -z "$danglingImages" ]
then
    echo 'there are no dangling Docker images'
else
    docker rmi -f $danglingImages # cleanup, GC for dangling images
fi
