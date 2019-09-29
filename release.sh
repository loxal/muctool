#!/usr/bin/env sh

./gradlew clean build shadowJar --info --no-build-cache

#cd service
#docker build --pull --tag loxal/muctool:latest .