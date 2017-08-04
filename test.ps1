#Param (
#    [Parameter (Mandatory = $True, Position = 1) ]
#    [string] $input_dir,
#    [Parameter (Mandatory = $True) ]
#    [string] $output_dir,
#    [switch] $force = $false
#)

Param([string] $suffix_args)

# add "--debug-jvm" to attach debugger
Function Main {
    Write-Host "suffix_args: $suffix_args"
    ./gradlew test --no-scan --parallel --no-rebuild --continuous --build-cache $suffix_args
}
Main

