/*
 * Copyright 2017 Alexander Orlov <alexander.orlov@loxal.net>. All rights reserved.
 */

package net.loxal.servicekit.service

import net.loxal.servicekit.endpoint.Endpoint
import net.loxal.servicekit.model.whois.Whois
import org.slf4j.LoggerFactory
import java.util.concurrent.TimeUnit
import javax.servlet.http.HttpServletRequest
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.container.AsyncResponse
import javax.ws.rs.container.ContainerRequestContext
import javax.ws.rs.container.Suspended
import javax.ws.rs.core.Context
import javax.ws.rs.core.Response

@Path(WhosService.RESOURCE_PATH)
class WhosService : Endpoint() {

    @GET
    fun whoAmI(@Context request: HttpServletRequest, @Context requestContext: ContainerRequestContext, @Suspended asyncResponse: AsyncResponse) {
        asyncResponse.setTimeout(ASYNC_RESPONSE_TIMEOUT.toLong(), TimeUnit.SECONDS)

        val headers: MutableMap<String, String> = mutableMapOf()
        for (headerName in request.headerNames) {
            headers.put(headerName, request.getHeader(headerName))
        }

        val host = Whois(
                headers = headers,
                name = request.remoteHost,
                address = request.remoteAddr
        )

        asyncResponse.resume(Response.ok(host).build())

        LOG.info(requestContext.method)
    }

    companion object {
        private val LOG = LoggerFactory.getLogger(WhosService::class.java)
        const val RESOURCE_PATH = "whois"
    }
}