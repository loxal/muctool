#!/usr/bin/env powershell

# add "--debug-jvm" to attach debugger
Write-Host "args: $args"
./gradlew test --no-scan --parallel --no-rebuild --continuous --build-cache $args

