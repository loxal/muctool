#!/usr/bin/env sh

./gradlew :whois-service:test \
  --no-scan \
  --parallel \
  --continue \
  --build-cache \
  --rerun-tasks \
  --tests ApiHealthCheck \
  --info \
  $1
