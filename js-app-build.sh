#!/usr/bin/env sh

#    :emoji:build \
#    :typing-trainer:build \
#    :bmi-calculator:build \
./gradlew \
    :client:build \
    :waves:build \
    :me:build \
    --parallel --continuous --no-scan \
    $1
#./gradlew :client:build  --continuous --parallel --build-cache --no-scan --continue $args