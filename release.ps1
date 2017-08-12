#!/usr/bin/env powershell

./gradlew singleJar $args

$docker_image = "muctool"
$docker_tag = "latest"

# docker login -u loxal
docker build --tag loxal/${docker_image}:${docker_tag} .
#docker push loxal/${docker_image}:${docker_tag} # do not push until credentials have been removed from application.conf
docker rm -f $docker_image
#docker run -d -p 443:1443 --env MY_ENV=$docker_image --env MY_ENV_SINGLE --label teamcity=buildNumber --label sans-backing_service --name $docker_image loxal/${docker_image}:${docker_tag}
docker run -d -p 80:1180 -p 443:1443 -v ~/srv/muctool/logs:/home/svc_usr/logs --env BUILD=$BUILD --env BUILDD=$BUILD_COUNTER --env MY_ENV_SINGLE=$buildCounter --label teamcity=buildNumber --label sans-backing_service --name $docker_image loxal/${docker_image}:${docker_tag}

$danglingImages = $(docker images -f "dangling=true" -q)

if ([string]::IsNullOrEmpty($danglingImages)){
    "There are no dangling Docker images"
} else {
    docker rmi -f $danglingImages # cleanup, GC for dangling images
}