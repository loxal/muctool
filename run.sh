#!/usr/bin/env sh

{
    ./gradlew run \
        --continue \
        --parallel \
        --continuous \
        --no-scan \
        --build-cache \
        $1 #
} || {
    hangingJavaProcessToStop=`jps | grep DevelopmentHost | awk '{print $1}'`
    kill -9 $hangingJavaProcessToStop
    echo "Gracefully killed hanging process: $hangingJavaProcessToStop"
}
