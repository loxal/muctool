#!/usr/bin/env sh

./gradlew clean build shadowJar --info --no-build-cache $1
