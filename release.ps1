Param([string] $suffix_args)

./gradlew singleJar $suffix_args

$docker_image = "muctool"
$docker_tag = "latest"

# docker login -u loxal
docker build --tag loxal/${docker_image}:${docker_tag} .
docker push loxal/${docker_image}:${docker_tag}
docker rm -f $docker_image
docker run -d -p 180:8300 --env MY_ENV=$docker_image --env MY_ENV_SINGLE --label jvm_lang=kotlin --label sans-backing_service --name $docker_image loxal/${docker_image}:${docker_tag}

$danglingImages = $(docker images -f "dangling=true" -q)

if ([string]::IsNullOrEmpty($danglingImages)){
    "there are no dangling Docker images"
} else {
    docker rmi -f $danglingImages # cleanup, GC for dangling images
}