#!/usr/bin/env bash

# Switch Java JDK
export JAVA_HOME=$(/usr/libexec/java_home -v 1.8)

gcloud config set project service-kit-loxal
./mvnw clean validate gcloud:stage

eval "$(docker-machine env default)"
docker build --tag=gcr.io/service-kit-loxal/default-docker-container .
gcloud docker push gcr.io/service-kit-loxal/default-docker-container

gcloud container clusters create docker-container-cluster \
    --scopes https://www.googleapis.com/auth/userinfo.email,\
https://www.googleapis.com/auth/compute,\
https://www.googleapis.com/auth/cloud.useraccounts,\
https://www.googleapis.com/auth/devstorage.full_control,\
https://www.googleapis.com/auth/taskqueue,\
https://www.googleapis.com/auth/bigquery,\
https://www.googleapis.com/auth/sqlservice.admin,\
https://www.googleapis.com/auth/datastore,\
https://www.googleapis.com/auth/logging.admin,\
https://www.googleapis.com/auth/monitoring,\
https://www.googleapis.com/auth/cloud-platform,\
https://www.googleapis.com/auth/bigtable.data,\
https://www.googleapis.com/auth/bigtable.admin,\
https://www.googleapis.com/auth/pubsub,\
https://www.googleapis.com/auth/logging.write,\
    --num-nodes 1 \
    --image projects/coreos-cloud/global/images/coreos-stable-835-13-0-v20160218 \
    --machine-type g1-small

kubectl run default-docker-container --image=gcr.io/service-kit-loxal/default-docker-container
kubectl expose replicationcontrollers default-docker-container --type=LoadBalancer --port=80 --target-port=8080

kubectl get services
kubectl get replicationcontrollers
kubectl get pods