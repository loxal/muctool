#!/usr/bin/env pwsh

Set-StrictMode -Version Latest
$ErrorActionPreference = "Stop"
$PSDefaultParameterValues["*:ErrorAction"] = "Stop"

try {
    Write-Host "args: $args"
    ./gradlew run `
        --continuous `
        --parallel `
        --build-cache `
        --no-scan `
        --continue `
        --no-rebuild `
        $args #
} finally {
    $hangingJavaProcessToStop = [regex]::match((jps), "(\d+)\ DevelopmentHost").Groups[1].Value
    Stop-Process -Id $hangingJavaProcessToStop
    Write-Host "Gracefully killed hanging process: $hangingJavaProcessToStop"
}
