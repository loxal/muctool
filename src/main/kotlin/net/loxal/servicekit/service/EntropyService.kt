/*
 * Copyright 2017 Alexander Orlov <alexander.orlov@loxal.net>. All rights reserved.
 */

package net.loxal.servicekit.service

import net.loxal.servicekit.endpoint.Endpoint
import org.slf4j.LoggerFactory
import java.util.*
import java.util.concurrent.TimeUnit
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.container.AsyncResponse
import javax.ws.rs.container.ContainerRequestContext
import javax.ws.rs.container.Suspended
import javax.ws.rs.core.Context
import javax.ws.rs.core.Response

@Path(EntropyService.RESOURCE_PATH)
class EntropyService : Endpoint() {

    @GET
    fun uuid(
            @Context requestContext: ContainerRequestContext,
            @Suspended asyncResponse: AsyncResponse
    ) {
        asyncResponse.setTimeout(ASYNC_RESPONSE_TIMEOUT.toLong(), TimeUnit.SECONDS)

        val uuid = UUID.randomUUID()
        asyncResponse.resume(Response.ok(uuid).build())

        LOG.info("${requestContext.method}: $uuid")
    }

    companion object {
        private val LOG = LoggerFactory.getLogger(EntropyService::class.java)
        const val RESOURCE_PATH = "entropy"
    }
}