#!/usr/bin/env powershell

Set-StrictMode -Version Latest
$ErrorActionPreference = "Stop"
$PSDefaultParameterValues["*:ErrorAction"] = "Stop"

try {
    Write-Host "args: $args"
    # add "--debug-jvm" to attach debugger
    # TODO remove --no-rebuild as it prevents hot-reloading of classes?
    ./gradlew run --continuous --parallel --build-cache --no-rebuild --no-scan --continue $args
} finally {
    $hangingJavaProcessToStop = [regex]::match((jps), "(\d+)\ DevelopmentHost").Groups[1].Value
    Stop-Process -Id $hangingJavaProcessToStop
    Write-Host "Gracefully killed hanging process: $hangingJavaProcessToStop"
}
