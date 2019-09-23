#!/usr/bin/env sh

# Init OSS Site Search
    curl -k -X PUT \
      https://es.loxal.net/site-profile/_doc/site-configuration-b7fde685-33f4-4a79-9ac3-ee3b75b83fa3 \
      -H "authorization: $BASIC_ENCODED_PASSWORD" \
      -H 'content-type: application/json' \
      -d '{
        "id": ["b7fde685-33f4-4a79-9ac3-ee3b75b83fa3"],
        "secret": ["56158b15-0d87-49bf-837d-89085a4ec88d"],
        "email": ["user@example.com"]
      }'

    curl -k -X PUT \
      https://es.loxal.net/site-profile/_doc/site-configuration-a2e8d60b-0696-47ea-bc48-982598ee35bd \
      -H "authorization: $BASIC_ENCODED_PASSWORD" \
      -H 'content-type: application/json' \
      -d '{
        "id": ["a2e8d60b-0696-47ea-bc48-982598ee35bd"],
        "secret": ["04a0afc6-d89a-45c9-8ba8-41d393d8d2f8"],
        "email": ["user@example.com"]
      }'

    curl -k -X PUT \
      https://es.loxal.net/site-profile/_doc/site-configuration-a9ede989-9d94-41d1-8571-a008318b01db \
      -H "authorization: $BASIC_ENCODED_PASSWORD" \
      -H 'content-type: application/json' \
      -d '{
        "id": ["a9ede989-9d94-41d1-8571-a008318b01db"],
        "secret": ["fbdc4e70-0141-4127-b95b-f9fd2d5e1b93"],
        "email": ["user@example.com"]
      }'

    curl -k -X PUT \
      https://es.loxal.net/site-profile/_doc/site-configuration-18e1cb09-b3ec-40e0-8279-dd005771f172 \
      -H "authorization: $BASIC_ENCODED_PASSWORD" \
      -H 'content-type: application/json' \
      -d '{
        "id": ["18e1cb09-b3ec-40e0-8279-dd005771f172"],
        "secret": ["6dd875d6-b75c-43ae-a7a8-c181fc0b0da6"],
        "email": ["user@example.com"]
      }'

curl -k -X PUT \
  'https://finder.muctool.de/sites/a9ede989-9d94-41d1-8571-a008318b01db/profile?siteSecret=fbdc4e70-0141-4127-b95b-f9fd2d5e1b93' \
  -H 'content-type: application/json' \
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

### Self-reference Site

curl -k -X PUT \
  'https://finder.muctool.de/sites/a2e8d60b-0696-47ea-bc48-982598ee35bd/profile?siteSecret=04a0afc6-d89a-45c9-8ba8-41d393d8d2f8' \
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

curl -k -X POST \
  'https://finder.muctool.de/sites/a2e8d60b-0696-47ea-bc48-982598ee35bd/recrawl?siteSecret=04a0afc6-d89a-45c9-8ba8-41d393d8d2f8&clearIndex=true&isThrottled=true'

###

### Reference Site

    curl -k -X PUT \
      https://es.loxal.net/site-profile/_doc/site-configuration-563714f1-96c0-4500-b366-4fc7e734fa1d \
      -H "authorization: $BASIC_ENCODED_PASSWORD" \
      -H 'content-type: application/json' \
      -d '{
        "id": ["563714f1-96c0-4500-b366-4fc7e734fa1d"],
        "secret": ["56158b15-0d87-49bf-837d-89085a4ec88d"],
        "email": ["user@example.com"]
      }'

curl -k -X PUT \
  'https://finder.muctool.de/sites/563714f1-96c0-4500-b366-4fc7e734fa1d/profile?siteSecret=56158b15-0d87-49bf-837d-89085a4ec88d' \
  -H 'content-type: application/json' \
  -d '{
      "id": "563714f1-96c0-4500-b366-4fc7e734fa1d",
      "secret": "56158b15-0d87-49bf-837d-89085a4ec88d",
      "configs": [
          {
              "url": "https://www.migrosbank.ch/de/",
              "allowUrlWithQuery": false,
              "pageBodyCssSelector": "body",
              "sitemapsOnly": false
          },
          {
              "url": "https://blog.migrosbank.ch/de/",
              "allowUrlWithQuery": false,
              "pageBodyCssSelector": "article > div.entry-content",
              "sitemapsOnly": false
          }
      ],
      "email": "alexander.orlov@loxal.net"
  }'

curl -k -X POST \
  'https://finder.muctool.de/sites/563714f1-96c0-4500-b366-4fc7e734fa1d/recrawl?siteSecret=56158b15-0d87-49bf-837d-89085a4ec88d&clearIndex=true&isThrottled=true'

###

curl -k -X PUT \
  "https://finder.muctool.de/sites/crawl/status?serviceSecret=$ADMIN_SITE_SECRET" \
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

curl -k -X PUT \
  'https://finder.muctool.de/sites/18e1cb09-b3ec-40e0-8279-dd005771f172/profile?siteSecret=6dd875d6-b75c-43ae-a7a8-c181fc0b0da6' \
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
            "url": "https://www.welt.de/sport/plus195450935/Fitness-Sport-Ernaehrung-Was-Sie-bei-Protein-Riegeln-beachten-muessen.html",
            "allowUrlWithQuery": true,
            "pageBodyCssSelector": "body > div > div",
            "sitemapsOnly": true
        },
        {
            "url": "https://fakenews.com/articles/",
            "allowUrlWithQuery": true,
            "pageBodyCssSelector": "div",
            "sitemapsOnly": false
        }
    ],
    "email": "user@example.com"
}'

curl -k -X PUT \
  'https://finder.muctool.de/sites/18e1cb09-b3ec-40e0-8279-dd005771f172/pages?siteSecret=6dd875d6-b75c-43ae-a7a8-c181fc0b0da6' \
  -H 'content-type: application/json' \
  -d '{
    "title": "Wie die Semantische Suche vom Knowledge Graph profitiert",
    "body": "<p>Der Knowledge Graph ist vielen Nutzern bereits durch Google oder Facebook bekannt. Aber auch iFinder",
    "url": "http://intrafind.de/blog/wie-die-semantische-suche-vom-knowledge-graph-profitiert"
}'