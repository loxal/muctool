param([string] $suffix_args)

try {
    Write-Host "suffix_args: $suffix_args"
    # add "--debug-jvm" to attach debugger
    function main() {
    # TODO remove --no-rebuild as it prevents hot-reloading of classes?
        ./gradlew.bat run --continuous --parallel --build-cache --no-rebuild --no-scan --continue $suffix_args
    }
    main
} finally {
    $hangingJavaProcessToStop = [regex]::match((jps), "(\d+)\ DevelopmentHost").Groups[1].Value
    kill $hangingJavaProcessToStop
#    Stop-Process -Id $hangingJavaProcessToStop
    Write-Host "Gracefully killed hanging process: $hangingJavaProcessToStop"
}