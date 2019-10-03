#!/usr/bin/env sh

./gradlew clean build shadowJar --info --no-build-cache

#cd service
#docker build --pull --tag loxal/muctool:latest .
#echo $DOCKER_PASSWORD | docker login --username loxal --password-stdin
#docker push loxal/muctool:latest
