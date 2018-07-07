#!/usr/bin/env sh

docker network create main
docker_network=main
docker_redirect_image="router"
docker_redirect_image_tag="latest"
cd docker-$docker_redirect_image
docker build --pull \
    --build-arg HASHED_DEFAULT_PASSWORD=$HASHED_DEFAULT_PASSWORD \
    --tag loxal/${docker_redirect_image}:${docker_redirect_image_tag} .
docker rm -f $docker_redirect_image
docker run -d --name $docker_redirect_image \
    -p 80:80 \
    -p 443:443 \
    -v /etc/letsencrypt:/etc/letsencrypt \
    -v /srv/mirror:/srv/mirror \
    --network $docker_network \
    loxal/${docker_redirect_image}:${docker_redirect_image_tag}

docker update --restart=unless-stopped $docker_redirect_image