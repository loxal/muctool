#!/usr/bin/env powershell

Set-StrictMode -Version Latest
$ErrorActionPreference = "Stop"
$PSDefaultParameterValues["*:ErrorAction"] = "Stop"

./gradlew :client:clean :client:build $args
#./gradlew :client:build  --continuous --parallel --build-cache --no-rebuild --no-scan --continue $args