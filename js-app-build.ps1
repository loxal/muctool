#!/usr/bin/env powershell

Set-StrictMode -Version Latest
$ErrorActionPreference = "Stop"
$PSDefaultParameterValues["*:ErrorAction"] = "Stop"

./gradlew :emoji:clean :emoji:build :typing-trainer:build :bmi-calculator:build :client:build `
    --parallel --continuous `
    $args
#./gradlew :client:build  --continuous --parallel --build-cache --no-rebuild --no-scan --continue $args