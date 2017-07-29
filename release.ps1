./gradlew singleJar $args[0]

$docker_image = "muctool"
$docker_tag = "latest"

# docker login -u loxal
docker build --tag loxal/${docker_image}:${docker_tag} .
#docker push loxal/${docker_image}:${docker_tag} # do not push until credentials have been removed from application.conf
docker rm -f $docker_image
docker run -d -p 180:8300 -p 443:8443 --env MY_ENV=$docker_image --env MY_ENV_SINGLE --label jvm_lang=kotlin --label sans-backing_service --name $docker_image loxal/${docker_image}:${docker_tag}

$danglingImages = $(docker images -f "dangling=true" -q)

if ([string]::IsNullOrEmpty($danglingImages)){
    "There are no dangling Docker images"
} else {
    docker rmi -f $danglingImages # cleanup, GC for dangling images
}