FROM nginx:stable AS service

MAINTAINER Alexander Orlov <alexander.orlov@loxal.net>

ARG HASHED_DEFAULT_PASSWORD=this_disables_basic_auth_when_no_arg_is_provided

RUN apt-get update && apt-get install curl nano -y
#RUN mkdir -p /etc/letsencrypt/.well-known # enables Letsencrypt validation
COPY cors.nginx /etc/nginx/snippets/cors.conf
COPY nginx.nginx /etc/nginx/nginx.conf
#htpasswd -nb -C 31 minion password
RUN echo minion:$HASHED_DEFAULT_PASSWORD > /etc/nginx/.htpasswd
RUN echo svc:$HASHED_DEFAULT_PASSWORD >> /etc/nginx/.htpasswd

RUN groupadd -g 1200 svc && \
    useradd --create-home --system -u 1200 -g svc svc
#USER svc
WORKDIR /etc/nginx

EXPOSE 80