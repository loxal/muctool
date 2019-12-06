#!/usr/bin/env sh

./gradlew \
    :client:build \
    :whois:build \
    --parallel \
    --continuous \
    --no-scan \
    --build-cache \
    $1
