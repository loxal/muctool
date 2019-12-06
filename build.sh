#!/usr/bin/env sh

rm -rf service/static/app
./gradlew clean build shadowJar --info $1
