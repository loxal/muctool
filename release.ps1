#!/usr/bin/env powershell

./gradlew singleJar $args

$docker_image = (Get-ChildItem build/libs).BaseName.Substring(0, 7)
$docker_tag = "latest"

# docker login -u loxal
docker build --tag loxal/${docker_image}:${docker_tag} .
#docker push loxal/${docker_image}:${docker_tag} # do not push until credentials have been removed from application.conf
docker rm -f $docker_image
#docker run -d -p 443:1443 --env MY_ENV=$docker_image --env MY_ENV_SINGLE --label teamcity=buildNumber --label sans-backing_service --name $docker_image loxal/${docker_image}:${docker_tag}
#sudo chown -R 1000:1000 ~/srv/$docker_image
#sudo chmod -R 744 ~/srv/$docker_image
docker run -d -p 443:1443 -v ~/srv/muctool/logs:logs -v ~/srv/muctool/data:data --env SECURITY_USER_PASSWORD=$env:SECURITY_USER_PASSWORD --env BUILD_NUMBER=$env:BUILD_NUMBER --env SCM_HASH=$env:SCM_HASH --label buildCounter=$env:BUILD_COUNTER --label sans-backing_service --name $docker_image loxal/${docker_image}:${docker_tag}

# Redirect container
$docker_redirect_image = "http-to-https-redirect"
$docker_redirect_image_tag = "latest"
cd docker-$docker_redirect_image
docker build --tag loxal/${docker_redirect_image}:$docker_redirect_image_tag .
docker push loxal/${docker_redirect_image}:$docker_redirect_image_tag
docker rm -f $docker_redirect_image
docker run -d --name $docker_redirect_image -p 80:80 loxal/${docker_redirect_image}:$docker_redirect_image_tag

$danglingImages = $(docker images -f "dangling=true" -q)
if ([string]::IsNullOrEmpty($danglingImages)){
    "There are no dangling Docker images"
} else {
    docker rmi -f $danglingImages # cleanup, GC for dangling images
}