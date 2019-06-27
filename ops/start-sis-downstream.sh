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
    --ulimit nofile=65535:65535 \
    --env "bootstrap.memory_lock=true" --ulimit memlock=-1:-1 \
    docker.elastic.co/elasticsearch/elasticsearch:7.2.0
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

docker exec -t ops-es \
    curl -X PUT \
      http://localhost:9200/site-profile/_doc/site-configuration-18e1cb09-b3ec-40e0-8279-dd005771f172 \
      -H 'Content-Type: application/json' \
      -d '{
        "id": ["18e1cb09-b3ec-40e0-8279-dd005771f172"],
        "secret": ["6dd875d6-b75c-43ae-a7a8-c181fc0b0da6"],
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
  'http://localhost:8001/sites/18e1cb09-b3ec-40e0-8279-dd005771f172/profile?siteSecret=6dd875d6-b75c-43ae-a7a8-c181fc0b0da6' \
  -H 'content-type: application/json' \
  -d '{
    "id": "18e1cb09-b3ec-40e0-8279-dd005771f172",
    "secret": "6dd875d6-b75c-43ae-a7a8-c181fc0b0da6",
    "configs": [
        {
            "url": "https://example.com/absolutely-not-relevant-as-it-is-not-considered",
            "allowUrlWithQuery": false,
            "pageBodyCssSelector": "body",
            "sitemapsOnly": false
        },
        {
            "url": "https://welt.de",
            "allowUrlWithQuery": true,
            "pageBodyCssSelector": "body > div > div",
            "sitemapsOnly": true
        },
        {
            "url": "https://fakenews.com",
            "allowUrlWithQuery": true,
            "pageBodyCssSelector": "div",
            "sitemapsOnly": true
        }
    ],
    "email": "user@example.com"
}'

#curl -X POST \
#  'http://localhost:8001/sites/18e1cb09-b3ec-40e0-8279-dd005771f172/recrawl?siteSecret=6dd875d6-b75c-43ae-a7a8-c181fc0b0da6&clearIndex=true&isThrottled=true'

curl -X PUT \
  'http://localhost:8001/sites/18e1cb09-b3ec-40e0-8279-dd005771f172/pages?siteSecret=6dd875d6-b75c-43ae-a7a8-c181fc0b0da6' \
  -H 'Content-Type: application/json' \
  -d '{
    "title": "Wie die Semantische Suche vom Knowledge Graph profitiert",
    "body": "<p>Der Knowledge Graph ist vielen Nutzern bereits durch Google oder Facebook bekannt. Aber auch iFinder",
    "url": "http://intrafind.de/blog/wie-die-semantische-suche-vom-knowledge-graph-profitiert"
}'