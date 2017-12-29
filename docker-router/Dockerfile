FROM nginx:1.13.7-alpine

MAINTAINER Alexander Orlov <alexander.orlov@loxal.net>

RUN mkdir -p /etc/letsencrypt/.well-known # enables Letsencrypt validation
COPY ssl.nginx /etc/nginx/snippets/ssl.conf
COPY cors.nginx /etc/nginx/snippets/cors.conf
COPY routes.nginx /etc/nginx/conf.d/default.conf

EXPOSE 80 443