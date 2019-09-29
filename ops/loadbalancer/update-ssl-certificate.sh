#!/usr/bin/env sh

# https://certbot.eff.org
# cf. https://gist.github.com/cecilemuller/a26737699a7e70a7093d4dc115915de8
# improve SSL Labs Server Test rating: https://www.digitalocean.com/community/tutorials/how-to-secure-nginx-with-let-s-encrypt-on-ubuntu-14-04

sudo apt update -y
sudo apt-get install -y certbot -t $(lsb_release -cs)-backports

#sudo certbot certonly --webroot -w /etc/letsencrypt --expand \
#    -d muctool.de \
#    -d muctool.loxal.net \
#    -d api.muctool.de \
#    -d mirror.muctool.de \
#    -d whois.muctool.de \
#    -d www.muctool.de \
#    -d loxal.org \
#    -d www.loxal.org \
#    -d epvin.com \
#    -d www.epvin.com \
#    -d erpiv.com \
#    -d www.erpiv.com \
#    -d loxal.net \
#    -d me.loxal.net \
#    -d blog.loxal.net \
#    -d news.loxal.net \
#    -d sky.loxal.net \
#    -d es.loxal.net \
#    -d bi.loxal.net \
#    -d ci.loxal.net

sudo certbot --expand \
    -d *.muctool.de \
    -d muctool.de \
    -d *.loxal.net \
    -d loxal.net \
    -d *.loxal.org \
    -d loxal.org \
    -d *.epvin.com \
    -d epvin.com \
    -d *.erpiv.com \
    -d erpiv.com \
    -d novitum.de \
    -d *.novitum.de \
    --manual \
    --preferred-challenges dns certonly \
    --server https://acme-v02.api.letsencrypt.org/directory
