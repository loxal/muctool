#!/usr/bin/env sh

# add "--debug-jvm" to attach debugger
echo "args: $1"
./gradlew test \
    --no-scan \
    --parallel \
    --continuous \
    --build-cache \
    $1 # --continue
