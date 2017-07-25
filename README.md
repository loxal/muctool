**Support this project**
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

[Support this work with cryptocurrencies like BitCoin, Ethereum, Ardor, and Komodo!](http://me.loxal.net/coin-support.html)

# Operate

## Run
    ./run.sh

## Test
    ./test.sh

# Services & Endpoints

## Quotation Endpoint
* Browser client: `/dilbert-quote/index.html`
* Endpoint for enterprise environment working condition quotations: `/dilbert-quote/enterprise`
* Endpoint for manager quotations: `/dilbert-quote/manager`
* Endpoint for programmer quotations: `/dilbert-quote/programmer`
* OpenID 2.0 authentication: `/play/ground.html`

## Who am I

* /whois:
    * Retrieves the IP address of the requesting host

## Getting Started

1. **Add all corresponding required properties like `appdirect.oauth.consumer.secret` to your `~/.m2/settings.xml`.**

1. ./run.sh
1. Call http://local.loxal.net:8300/application.wadl
1. Try http://local.loxal.net:8300

## Demo Showcase

* Demo Instance running on (private) CloudFoundry
    * http://sky.loxal.net/dilbert-quote/index.html

# Deploy & Release

## Deploy to Cloud Foundry

* Run `./release.sh` 

## Deploy to Google App Engine 

1. Edit `./release.sh` appropriately 
1. Run `./release.sh`