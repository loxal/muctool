#!/usr/bin/env powershell

#https://certbot.eff.org
#cf. https://gist.github.com/cecilemuller/a26737699a7e70a7093d4dc115915de8
#improve SSL Labs Server Test rating: https://www.digitalocean.com/community/tutorials/how-to-secure-nginx-with-let-s-encrypt-on-ubuntu-14-04

#function cleanup_nginx {
#    sudo systemctl stop nginx.service
#    sudo apt -y remove nginx-common python-certbot-nginx certbot
#    sudo apt -y autoremove
#    sudo rm -rf /etc/nginx
#    sudo rm -rf /etc/init.d/nginx
#    sudo rm -rf /var/log/nginx
#    sudo rm -rf /var/www
#
#}

sudo apt install software-properties-common
sudo add-apt-repository ppa:certbot/certbot
sudo apt update
sudo apt install certbot
#sudo certbot certonly
# stop anything running on port 80 only

#cleanup_nginx
#sudo apt install python-certbot-nginx

#sudo certbot --nginx certonly --domain "sky.loxal.net, muctool.loxal.net, www.loxal.net, ci.loxal.net, api.loxal.net, loxal.net, muctool.de, www.muctool.de, whois.muctool.de, api.muctool.de" --expand
#sudo certbot --webroot certonly -w /var/www/letsencrypt --expand --domain "sky.loxal.net, muctool.loxal.net, www.loxal.net, ci.loxal.net, api.loxal.net, loxal.net, muctool.de, www.muctool.de, whois.muctool.de, api.muctool.de"
#sudo certbot --webroot certonly -w /var/www/letsencrypt --expand -d sky.loxal.net -d muctool.loxal.net -d www.loxal.net -d ci.loxal.net -d api.loxal.net -d loxal.net -d muctool.de -d www.muctool.de -d whois.muctool.de -d api.muctool.de
sudo certbot --webroot certonly -w /var/www/letsencrypt --expand -d sky.loxal.net -d muctool.loxal.net -d www.loxal.net -d ci.loxal.net -d api.loxal.net -d loxal.net -d muctool.de -d www.muctool.de -d whois.muctool.de -d api.muctool.de

sudo openssl pkcs12 -export -in /etc/letsencrypt/live/sky.loxal.net/fullchain.pem -inkey /etc/letsencrypt/live/sky.loxal.net/privkey.pem -out pkcs.p12 -name alias
keytool -importkeystore -destkeystore keystore.jks -srcstoretype PKCS12 -srckeystore pkcs.p12 -alias alias
#sudo keytool -import -alias alias -keystore keystore.jks -file /etc/letsencrypt/live/muctool.loxal.net/fullchain.pem
#cp keystore.jks src/main/resources
sudo keytool -list -v -alias alias -keystore keystore.jks
