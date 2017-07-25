/*
 * Copyright 2017 Alexander Orlov <alexander.orlov@loxal.net>. All rights reserved.
 */

package net.loxal.servicekit.filter

import javax.annotation.Priority
import javax.ws.rs.Priorities
import javax.ws.rs.container.ContainerRequestContext
import javax.ws.rs.container.ContainerResponseContext
import javax.ws.rs.container.ContainerResponseFilter
import javax.ws.rs.core.HttpHeaders
import javax.ws.rs.ext.Provider

@Provider
@Priority(Priorities.HEADER_DECORATOR)
class AccessControlFilter : ContainerResponseFilter {
    override fun filter(requestContext: ContainerRequestContext, responseContext: ContainerResponseContext) {
        val headers = responseContext.headers

        headers.putSingle(allowOriginHeader, allowOriginHeaderValue)
        headers.putSingle(allowHeadersHeader, allowHeadersValue)

        headers.putSingle("Server", "ServiceKit v1")
    }

    companion object {
        const val allowOriginHeader = "Access-Control-Allow-Origin"
        const val allowOriginHeaderValue = "*"
        const val allowHeadersHeader = "Access-Control-Allow-Headers"
        val allowHeadersValue = listOf("origin", "x-requested-with", HttpHeaders.CONTENT_TYPE, HttpHeaders.ACCEPT)
    }
}
