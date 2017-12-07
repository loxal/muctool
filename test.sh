#!/usr/bin/env sh

# add "--debug-jvm" to attach debugger
echo "args: $1"
./gradlew test \
    --no-scan \
    --parallel \
    --no-rebuild \
    --continuous \
    --build-cache \
    $1 # --continue
