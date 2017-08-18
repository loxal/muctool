#!/usr/bin/env powershell

Set-StrictMode -Version Latest
$ErrorActionPreference = "Stop"
$PSDefaultParameterValues["*:ErrorAction"] = "Stop"

# add "--debug-jvm" to attach debugger
Write-Host "args: $args"
./gradlew test --no-scan --parallel --no-rebuild --continuous --build-cache $args # --continue

