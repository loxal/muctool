#Param (
#    [Parameter (Mandatory = $True, Position = 1) ]
#    [string] $input_dir,
#    [Parameter (Mandatory = $True) ]
#    [string] $output_dir,
#    [switch] $force = $false
#)

Param([string] $suffix_args)

Function Main() {
    ./gradlew test $suffix_args
}
Main
