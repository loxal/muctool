#!/usr/bin/env sh

./gradlew clean build

DOCKER_TAG=latest

# docker login -u loxal
docker build --tag=loxal/muctool:$DOCKER_TAG .
docker push loxal/muctool:$DOCKER_TAG
docker rm -f muctool
docker run -d -p 180:8080 --env MY_ENV=$my_env --env MY_ENV_SINGLE --label jvm_lang=kotlin --label sans-backing_service --name muctool loxal/muctool:$DOCKER_TAG
