#!/usr/bin/env powershell

Param([string] $suffix_args)
Write-Host "suffix_args: $suffix_args"

#https://certbot.eff.org/
#muctool.loxal.net, sky.loxal.net, www.loxal.net, loxal.net, muctool.de, www.muctool.de, whois.muctool.de
sudo certbot certonly
sudo openssl pkcs12 -export -in /etc/letsencrypt/live/sky.loxal.net/fullchain.pem -inkey /etc/letsencrypt/live/sky.loxal.net/privkey.pem -out pkcs.p12 -name alias
keytool -importkeystore -destkeystore keystore.jks -srcstoretype PKCS12 -srckeystore pkcs.p12 -alias alias
#cp keystore.jks src/main/resources
keytool -list -v -alias alias -keystore keystore.jks
