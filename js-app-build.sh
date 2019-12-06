#!/usr/bin/env sh

./gradlew \
    :client:build \
    :whois:build \
    :me:build \
    --parallel \
    --continuous \
    --no-scan \
    --build-cache \
    $1
