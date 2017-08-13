<!-- BADGES/ -->
<span class="badge-paypal">
<a href="https://www.paypal.com/cgi-bin/webscr?cmd=_s-xclick&amp;hosted_button_id=MA847TR65D4N2" title="Donate to this project using PayPal">
<img src="https://img.shields.io/badge/paypal-donate-yellow.svg" alt="PayPal Donate"/>
</a></span>
<span class="badge-flattr">
<a href="https://flattr.com/submit/auto?fid=o6ok7n&url=https%3A%2F%2Fgithub.com%2Floxal" title="Donate to this project using Flattr">
<img src="https://img.shields.io/badge/flattr-donate-yellow.svg" alt="Flattr Donate" />
</a></span>
<span class="badge-gratipay"><a href="https://gratipay.com/~loxal" title="Donate weekly to this project using Gratipay">
<img src="https://img.shields.io/badge/gratipay-donate-yellow.svg" alt="Gratipay Donate" />
</a></span>
<!-- /BADGES -->

[Support this work with cryptocurrencies like BitCoin, Ethereum, Ardor, and Komodo!](https://muctool.loxal.net/cryptocurrency-coin-support.html)

MUCtool
-
[![Build Status](https://travis-ci.org/loxal/muctool.svg)](https://travis-ci.org/loxal/muctool)

# Operations

* [PowerShell required](https://github.com/PowerShell/PowerShell)

## Run
    ./run.ps1

## Test
    ./test.ps1

## Release
    ./release.ps1 
    
## SSL
    sudo keytool -import -alias alias -keystore keystore.jks -file /etc/letsencrypt/live/muctool.loxal.net/fullchain.pem
    sudo keytool -list -v -alias alias -keystore keystore.jks

## Persist Statistics

    curl https://muctool.loxal.net/stats > ~/srv/muctool/stats/muctool-`date -u +"%Y-%m-%dT%H:%M:%SZ"`.json
    
    crontab -e
    0 5 * * * curl https://muctool.loxal.net/stats > ~/srv/muctool/stats/muctool-`date -u +"%Y-%m-%dT%H:%M:%SZ"`.json
    
# Attribution
* Made with ♥ in Munich
* [This service includes GeoLite2 data, created by MaxMind.](https://www.maxmind.com)