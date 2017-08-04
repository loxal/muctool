param([string] $suffix_args)

try {
    Write-Host "suffix_args: $suffix_args"
    # add "--debug-jvm" to attach debugger
    function main() {
        ./gradlew run --continuous --parallel --build-cache --no-rebuild --no-scan $suffix_args
    }
    main
} finally {
    $hangingJavaProcessToStop = [regex]::match((jps), "(\d+)\ DevelopmentHost").Groups[1].Value
    kill $hangingJavaProcessToStop
#    Stop-Process -Id $hangingJavaProcessToStop
    Write-Host "Gracefully killed hanging process: $hangingJavaProcessToStop"
}