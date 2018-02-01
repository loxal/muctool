#!/usr/bin/env sh

# https://certbot.eff.org
#cf. https://gist.github.com/cecilemuller/a26737699a7e70a7093d4dc115915de8
#improve SSL Labs Server Test rating: https://www.digitalocean.com/community/tutorials/how-to-secure-nginx-with-let-s-encrypt-on-ubuntu-14-04

sudo apt install software-properties-common -y
sudo add-apt-repository ppa:certbot/certbot -y
sudo apt update -y
sudo apt install certbot -y

#Your cert will expire on 2018-05-02. To obtain a new or tweaked
sudo certbot certonly --webroot -w /etc/letsencrypt --expand \
    -d muctool.de \
    -d muctool.loxal.net \
    -d api.muctool.de \
    -d whois.muctool.de \
    -d www.muctool.de \
    -d loxal.net \
    -d www.loxal.net \
    -d api.loxal.net \
    -d ci.loxal.net
