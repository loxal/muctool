#!/usr/bin/env pwsh

Set-StrictMode -Version Latest
$ErrorActionPreference = "Stop"
$PSDefaultParameterValues["*:ErrorAction"] = "Stop"

try {
    echo "args: $args"
    ./gradlew run `
        --continuous `
        --parallel `
        --build-cache `
        --no-scan `
        --continue `
        $args #
} finally {
    $hangingJavaProcessToStop = [regex]::match((jps), "(\d+)\ DevelopmentHost").Groups[1].Value
    Stop-Process -Id $hangingJavaProcessToStop
    echo "Gracefully killed hanging process: $hangingJavaProcessToStop"
}
