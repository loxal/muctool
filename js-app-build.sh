#!/usr/bin/env sh

#    :emoji:build \
#    :typing-trainer:build \
#    :bmi-calculator:build \
./gradlew \
    :client:build \
    :contract-creator:build \
    :me:build \
    --parallel \
    --continuous \
    --no-scan \
    --build-cache \
    $1
#./gradlew :client:build  --continuous --parallel --build-cache --no-scan --continue $args
