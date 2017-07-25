#!/usr/bin/env bash

./gradlew clean war

DOCKER_TAG=latest

# docker login -u loxal
docker build --tag=loxal/service-kit:$DOCKER_TAG . -f Dockerfile
docker push loxal/service-kit:$DOCKER_TAG
docker rm -f service-kit
docker run -d -p 80:8080 --env appdirect_oAuth_consumer_key=$appdirect_oAuth_consumer_key --env appdirect_oAuth_consumer_secret --label jvm_lang=kotlin --label sans-backing_service --name service-kit loxal/service-kit:$DOCKER_TAG
