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
      }'
docker exec -t ops-es \
    curl -X PUT \
      http://localhost:9200/site-profile/_doc/site-configuration-a2e8d60b-0696-47ea-bc48-982598ee35bd \
      -H 'Content-Type: application/json' \
      -d '{
        "id": ["a2e8d60b-0696-47ea-bc48-982598ee35bd"],
        "secret": ["04a0afc6-d89a-45c9-8ba8-41d393d8d2f8"],
        "email": ["user@example.com"]
      }'

docker exec -t ops-es \
    curl -X PUT \
      http://localhost:9200/site-profile/_doc/site-configuration-a9ede989-9d94-41d1-8571-a008318b01db \
      -H 'Content-Type: application/json' \
      -d '{
        "id": ["a9ede989-9d94-41d1-8571-a008318b01db"],
        "secret": ["fbdc4e70-0141-4127-b95b-f9fd2d5e1b93"],
        "email": ["user@example.com"]
      }'


curl -X PUT \
  'http://localhost:8001/sites/a9ede989-9d94-41d1-8571-a008318b01db/profile?siteSecret=fbdc4e70-0141-4127-b95b-f9fd2d5e1b93' \
  -H 'Content-Type: application/json' \
  -d '{
    "id": "a9ede989-9d94-41d1-8571-a008318b01db",
    "secret": "fbdc4e70-0141-4127-b95b-f9fd2d5e1b93",
    "configs": [
        {
            "url": "https://api.sitesearch.cloud",
            "allowUrlWithQuery": false,
            "pageBodyCssSelector": "body",
            "sitemapsOnly": true
        },
        {
            "url": "https://dev.sitesearch.cloud",
            "allowUrlWithQuery": false,
            "pageBodyCssSelector": "body",
            "sitemapsOnly": false
        }
    ],
    "email": "user@example.com"
}'

#docker exec -t ops-es \
#    curl -X PUT \
#      http://localhost:9200/svc-singletons/_doc/crawl-status \
#      -H 'content-type: application/json' \
#      -d '{
#        "a2e8d60b-0696-47ea-bc48-982598ee35bd": [
#            "2019-06-09T10:43:22.178497Z",
#            "42"
#        ]
#    }'

curl -X PUT \
  "http://localhost:8001/sites/crawl/status?serviceSecret=$ADMIN_SITE_SECRET" \
  -H 'content-type: application/json' \
  -d '{
    "sites": [
        {
            "siteId": "a2e8d60b-0696-47ea-bc48-982598ee35bd",
            "crawled": "2018-11-21T23:04:46.682264Z",
            "pageCount": -1
        },
        {
            "siteId": "b7fde685-33f4-4a79-9ac3-ee3b75b83fa3",
            "crawled": "2018-11-21T23:04:46.682264Z",
            "pageCount": -1
        }
    ]
}'

curl -X PUT \
  'http://localhost:8001/sites/a2e8d60b-0696-47ea-bc48-982598ee35bd/profile?siteSecret=04a0afc6-d89a-45c9-8ba8-41d393d8d2f8' \
  -H 'content-type: application/json' \
  -d '{
    "id": "a2e8d60b-0696-47ea-bc48-982598ee35bd",
    "secret": "04a0afc6-d89a-45c9-8ba8-41d393d8d2f8",
    "configs": [
        {
            "url": "https://api.sitesearch.cloud",
            "allowUrlWithQuery": false,
            "pageBodyCssSelector": "body",
            "sitemapsOnly": false
        }
    ],
    "email": "user@example.com"
}'