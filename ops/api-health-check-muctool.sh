#!/usr/bin/env sh

./gradlew :service:test \
  --no-scan \
  --parallel \
  --continue \
  --build-cache \
  --rerun-tasks \
  --tests ApiHealthCheck \
  --info \
  $1
