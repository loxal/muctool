#!/usr/bin/env sh

./gradlew :service:clean :service:test \
    --no-scan \
    --parallel \
    --continue \
    --tests *ApiHealthCheck \
    --info \
    $1