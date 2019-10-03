#!/usr/bin/env sh

docker build \
    --build-arg HASHED_DEFAULT_PASSWORD=$HASHED_DEFAULT_PASSWORD \
    --tag muctool/muc-router:latest .

echo $DOCKER_PASSWORD | docker login --username loxal --password-stdin
docker push muctool/muc-router:latest
#docker tag muctool/muc-router:latest muctool/muc-router:latest
#docker push muctool/muc-router:latest
