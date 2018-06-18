#!/usr/bin/env sh

#vim /srv/dnscrypt-server/zones/dnscrypt-server.conf
#docker exec dnscrypt-server /opt/unbound/sbin/unbound-checkconf

docker rm -f dnscrypt-server
docker run --name dnscrypt-server \
    -v /srv/dnscrypt-server/zones:/opt/unbound/etc/unbound/zones \
    -p 443:443/udp -p 443:443/tcp --net=host \
    jedisct1/dnscrypt-server init -N alf.loxal.net -E 78.47.232.28:443
docker start dnscrypt-server
docker logs dnscrypt-server

docker rm -f dnscrypt-server
docker run --name dnscrypt-server \
    -p 1443:443/udp -p 1443:443/tcp --net=host \
    jedisct1/dnscrypt-server init -help
docker start dnscrypt-server
docker logs dnscrypt-server

docker rm -f dnscrypt-server
docker run --name dnscrypt-server \
    -v /srv/dnscrypt-server/zones:/opt/unbound/etc/unbound/zones \
    -p 443:443 \
    jedisct1/dnscrypt-server init -N alf.loxal.net -E 78.47.232.28:443
docker start dnscrypt-server
docker logs dnscrypt-server