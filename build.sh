#!/usr/bin/env sh

rm -rf whois-service/static/app
./gradlew clean build shadowJar --info $1
