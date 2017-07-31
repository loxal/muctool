Param([string] $suffix_args)

# add "--debug-jvm" to attach debugger
Function Main() {
    ./gradlew run --continuous --parallel --build-cache --no-rebuild --no-scan $suffix_args
}
Main