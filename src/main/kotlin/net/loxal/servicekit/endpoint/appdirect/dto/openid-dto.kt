/*
 * Copyright 2017 Alexander Orlov <alexander.orlov@loxal.net>. All rights reserved.
 */

package net.loxal.servicekit.endpoint.appdirect.dto

import java.net.URL

data class OpenIdInfo(
        var url: URL? = URL(""),
        var returnToUrl: URL? = URL(""),
        var signInUrl: URL = URL("")
)