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

<img src="https://muctool.loxal.net/favicon.ico" alt="MUCtool" title="MUCtool - Web Toolkit" width="100" style="max-width:100%;">

MUCtool
-
* [![Travis CI Build Status](https://travis-ci.org/loxal/muctool.svg)](https://travis-ci.org/loxal/muctool)
* [![TeamCity CI Build Status](https://ci.loxal.net/app/rest/builds/buildType(id:Loxal_MUCtool_Build)/statusIcon)](https://ci.loxal.net/viewType.html?buildTypeId=Loxal_MUCtool_Build)
* [API](https://muctool.loxal.net/api/index.html)

***> > > [DEMO](https://muctool.loxal.net) < < <***

# Operations

[![Join the chat at https://gitter.im/MUCtool/Lobby](https://badges.gitter.im/MUCtool/Lobby.svg)](https://gitter.im/MUCtool/Lobby?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)

* [PowerShell required](https://github.com/PowerShell/PowerShell)

## Run
    ./run.ps1

## Test
    ./test.ps1

## Release
    ./release.ps1 

## Load
    ./load-test.ps1
    
## Build JavaScript Apps (Kotlin code)
    ./js-app-build.ps1

## Persist Statistics

    curl https://muctool.loxal.net/stats 
    
    crontab -e
    0 5 * * * curl https://muctool.loxal.net/stats > ~/srv/muctool/stats/muctool-`date -u +"%Y-%m-%dT%H:%M:%SZ"`.json
    
# Attribution
* Made with ♥ in Munich
* [This service includes GeoLite2 data, created by MaxMind.](https://www.maxmind.com)