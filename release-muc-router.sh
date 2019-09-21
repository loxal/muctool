#!/usr/bin/env sh

cd ops/muc-router
docker build --pull \
    --build-arg HASHED_DEFAULT_PASSWORD=$HASHED_DEFAULT_PASSWORD \
    --tag loxal/muc-router:latest .

# docker login
docker push loxal/muc-router:latest
