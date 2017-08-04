    Param([string] $suffix_args)
    Write-Host "suffix_args: $suffix_args"

    sudo keytool -import -alias alias -keystore keystore.jks -file /etc/letsencrypt/live/muctool.loxal.net/fullchain.pem
    sudo keytool -list -v -alias alias -keystore keystore.jks