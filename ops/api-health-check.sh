#!/usr/bin/env sh

./gradlew :service:clean :service:test \
  --no-scan \
  --parallel \
  --continue \
  --build-cache \
  --tests ApiHealthCheck \
  --info \
  $1
