#!/usr/bin/env sh

docker build --pull \
    --build-arg HASHED_DEFAULT_PASSWORD=$HASHED_DEFAULT_PASSWORD \
    --tag loxal/muc-router:latest .

# docker login
docker push loxal/muc-router:latest
docker tag loxal/muc-router:latest muctool/muc-router:latest
docker push muctool/muc-router:latest
