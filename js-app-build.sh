#!/usr/bin/env sh

#    :emoji:build \
#    :typing-trainer:build \
#    :bmi-calculator:build \
./gradlew \
    clean \             # required?
    :clean \            # required?
    :service:clean \    # required?
    :client:build \
    :waves:build \
    :me:build \
    --parallel \
    --continuous \
    --no-scan \
    --build-cache \
    $1
#./gradlew :client:build  --continuous --parallel --build-cache --no-scan --continue $args