#!/usr/bin/env sh

#docker swarm leave --force
#docker swarm init --advertise-addr 88.99.37.232
sudo sysctl -w vm.max_map_count=262144

#docker stack rm elk
#docker stack deploy -c elk-stack.yaml --prune elk
#
#docker stack ps elk; docker service ls
#curl http://localhost:9200/_cluster/health?pretty
#curl http://localhost:9200/_cat/health?pretty
#curl http://88.99.37.232:9200

docker rm -f ops-es
docker run -d --name ops-es \
    -e discovery.type=single-node \
    --network main \
    --restart unless-stopped \
    docker.elastic.co/elasticsearch/elasticsearch:7.1.1
#amazon/opendistro-for-elasticsearch:latest
sleep 20

# Init OSS Site Search
docker exec -t ops-es \
  curl -X PUT \
  http://localhost:9200/site-profile/_doc/site-configuration-b7fde685-33f4-4a79-9ac3-ee3b75b83fa3 \
  -H 'Content-Type: application/json' \
  -d '{
    "id": ["b7fde685-33f4-4a79-9ac3-ee3b75b83fa3"],
    "secret": ["56158b15-0d87-49bf-837d-89085a4ec88d"],
    "email": ["user@example.com"]
  }'; \
  curl -X PUT \
  http://localhost:9200/site-profile/_doc/site-configuration-a2e8d60b-0696-47ea-bc48-982598ee35bd \
  -H 'Content-Type: application/json' \
  -d '{
    "id": ["a2e8d60b-0696-47ea-bc48-982598ee35bd"],
    "secret": ["04a0afc6-d89a-45c9-8ba8-41d393d8d2f8"],
    "email": ["user@example.com"]
  }'