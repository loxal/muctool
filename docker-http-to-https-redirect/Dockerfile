FROM nginx:1.13-alpine

MAINTAINER Alexander Orlov <alexander.orlov@loxal.net>

#RUN mkdir -p /var/www/letsencrypt/.well-known/acme-challenge
RUN mkdir /etc/nginx/snippets
COPY ssl.conf /etc/nginx/snippets
COPY letsencrypt.conf /etc/nginx/snippets
COPY routes-ssl.nginx /etc/nginx/conf.d/default.conf

EXPOSE 80 443