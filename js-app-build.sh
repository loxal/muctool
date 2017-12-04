#!/usr/bin/env sh

#    :emoji:build \
#    :typing-trainer:build \
#    :bmi-calculator:build \
./gradlew \
    :waves:build \
    :me:build \
    :client:build \
    --parallel --continuous --no-scan \
    $args
#./gradlew :client:build  --continuous --parallel --build-cache --no-rebuild --no-scan --continue $args