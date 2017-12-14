FROM nginx:1.13.7-alpine

MAINTAINER Alexander Orlov <alexander.orlov@loxal.net>

#RUN mkdir -p /var/www/letsencrypt/.well-known/acme-challenge # enables Letsencrypt validation
COPY ssl.nginx /etc/nginx/snippets/ssl.conf
COPY letsencrypt.nginx /etc/nginx/snippets/letsencrypt.conf
COPY cors.nginx /etc/nginx/snippets/cors.conf
COPY routes.nginx /etc/nginx/conf.d/default.conf

EXPOSE 80 443