#!/usr/bin/env powershell

#https://certbot.eff.org

sudo apt install software-properties-common
sudo add-apt-repository ppa:certbot/certbot
sudo apt update
sudo apt install certbot
sudo certbot certonly

#https://certbot.eff.org/
#muctool.loxal.net, sky.loxal.net, www.loxal.net, loxal.net, muctool.de, www.muctool.de, whois.muctool.de
sudo certbot certonly
sudo openssl pkcs12 -export -in /etc/letsencrypt/live/sky.loxal.net/fullchain.pem -inkey /etc/letsencrypt/live/sky.loxal.net/privkey.pem -out pkcs.p12 -name alias
keytool -importkeystore -destkeystore keystore.jks -srcstoretype PKCS12 -srckeystore pkcs.p12 -alias alias
#sudo keytool -import -alias alias -keystore keystore.jks -file /etc/letsencrypt/live/muctool.loxal.net/fullchain.pem
#cp keystore.jks src/main/resources
sudo keytool -list -v -alias alias -keystore keystore.jks
