#!/usr/bin/env powershell

#Param([string] $suffix_args)

function main() {
    ./gradlew jmh $args
}
main