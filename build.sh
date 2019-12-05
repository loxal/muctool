#!/usr/bin/env sh

#./gradlew clean build :service:includeKotlinJsRuntime shadowJar --info $1
./gradlew clean build shadowJar --info $1
