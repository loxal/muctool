#!/usr/bin/env sh
#    :emoji:build \
#    :typing-trainer:build \
#    :bmi-calculator:build \
./gradlew \
    :waves:build \
    :me:build \
    :client:build \
    --parallel --continuous --no-scan \
    $1
#./gradlew :client:build  --continuous --parallel --build-cache --no-scan --continue $args