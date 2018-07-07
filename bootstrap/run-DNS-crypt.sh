#!/usr/bin/env sh

#vim /srv/dnscrypt-server/zones/dnscrypt-server.conf
#docker exec dnscrypt-server /opt/unbound/sbin/unbound-checkconf

container_name=dnscrypt-server

docker rm -f dnscrypt-server
docker run -d --name dnscrypt-server \
    -p 1443:443/udp -p 1443:443/tcp \
    -v /srv/dnscrypt-server/zones:/opt/unbound/etc/unbound/zones \
    jedisct1/dnscrypt-server init -N alf.loxal.net:1443 -E 88.99.37.232:1443
docker exec dnscrypt-server /opt/unbound/sbin/unbound-checkconf
docker start dnscrypt-server
docker logs dnscrypt-server -f
