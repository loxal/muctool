#!/usr/bin/env sh

main(){
	appdirect_oAuth_consumer_key=key \
	appdirect_oAuth_consumer_secret=secret \
./gradlew clean jettyRun \
            --continuous \
            --parallel \
            --build-cache \
            --no-rebuild \
            --no-scan 
}
main
