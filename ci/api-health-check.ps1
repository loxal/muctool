#!/usr/bin/env pwsh

Set-StrictMode -Version Latest
$ErrorActionPreference = "Stop"
$PSDefaultParameterValues["*:ErrorAction"] = "Stop"

./gradlew :service:clean :service:test `
    --build-cache `
    --no-rebuild `
    --no-scan `
    --parallel `
    --continue `
    --tests *ApiHealthCheck `
    --info `
    $args