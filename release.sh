#!/usr/bin/env sh

./gradlew clean singleJar $1

docker_network="muctool"
docker network create muctool
#$docker_image=(Get-ChildItem service/build/libs).BaseName.Substring(0, 7)
docker_image=$docker_network
docker_tag="latest"

cd service
# docker login -u loxal
docker build --tag loxal/${docker_image}:${docker_tag} .
#docker push loxal/${docker_image}:${docker_tag} # do not push until credentials have been removed from application.conf
#    -v ~/srv/muctool/logs:/logs \
#    -v ~/srv/muctool/data:/data \
docker rm -f $docker_image
docker run -d \
    -p 1180:1180 \
    --env MUCTOOL_GITHUB_CLIENT_ID=$MUCTOOL_GITHUB_CLIENT_ID \
    --env MUCTOOL_GITHUB_CLIENT_SECRET=$MUCTOOL_GITHUB_CLIENT_SECRET \
    --env SECURITY_USER_PASSWORD=$SECURITY_USER_PASSWORD \
    --env BUILD_NUMBER=$BUILD_NUMBER \
    --env SCM_HASH=$SCM_HASH \
    --label buildCounter=$BUILD_COUNTER \
    --label sans-backing_service \
    --name $docker_image \
    --network $docker_network \
    loxal/${docker_image}:${docker_tag}
cd ..

# Redirect container
docker_redirect_image="router"
docker_redirect_image_tag="latest"
cd docker-$docker_redirect_image
docker build --tag loxal/${docker_redirect_image}:$docker_redirect_image_tag .
#docker push loxal/${docker_redirect_image}:$docker_redirect_image_tag
docker rm -f $docker_redirect_image
docker run -d --name $docker_redirect_image \
    -p 80:80 -p 443:443 \
    -v /etc/letsencrypt:/etc/letsencrypt \
    --network $docker_network \
    loxal/${docker_redirect_image}:${docker_redirect_image_tag}

danglingImages=$(docker images -f "dangling=true" -q)
if [ $danglingImages ]; then
    docker rmi -f $danglingImages # cleanup, GC for dangling images
else
    "There are no dangling Docker images"
fi