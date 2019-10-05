#!/usr/bin/env sh

./gradlew clean build shadowJar --info $1 -x test
