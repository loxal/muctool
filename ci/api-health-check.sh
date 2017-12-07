#!/usr/bin/env sh

./gradlew :service:clean :service:test \
    --build-cache \
    --no-rebuild \
    --no-scan \
    --parallel \
    --continue \
    --tests *ApiHealthCheck \
    --info \
    $1