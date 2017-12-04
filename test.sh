#!/usr/bin/env sh

# add "--debug-jvm" to attach debugger
Write-Host "args: $args"
./gradlew test \
    --no-scan \
    --parallel \
    --no-rebuild \
    --continuous \
    --build-cache \
    $args # --continue