/*
 * Copyright 2017 Alexander Orlov <alexander.orlov@loxal.net>. All rights reserved.
 */

package net.loxal.servicekit.service

import net.loxal.servicekit.endpoint.Endpoint
import net.loxal.servicekit.model.common.ErrorMessage
import net.loxal.servicekit.model.whois.Host
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.net.InetAddress
import java.net.UnknownHostException
import java.util.concurrent.TimeUnit
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.QueryParam
import javax.ws.rs.container.AsyncResponse
import javax.ws.rs.container.ContainerRequestContext
import javax.ws.rs.container.Suspended
import javax.ws.rs.core.Context
import javax.ws.rs.core.Response

@Path(ResolveIpAddressService.RESOURCE_PATH)
class ResolveIpAddressService : Endpoint() {

    @GET
    fun resolveIpAddress(
            @QueryParam(HOST_NAME_PARAM) hostName: String?,
            @Context requestContext: ContainerRequestContext,
            @Suspended asyncResponse: AsyncResponse
    ) {
        asyncResponse.setTimeout(ASYNC_RESPONSE_TIMEOUT.toLong(), TimeUnit.SECONDS)

        try {
            val localHost = InetAddress.getByName(hostName)

            val host = Host(localHost.hostName, localHost.hostAddress)
            asyncResponse.resume(Response.ok(host).build())

            LOG.info(requestContext.method)
        } catch (e: UnknownHostException) {
            val errorMsg = e.message
            val errorMessage = ErrorMessage(Response.Status.INTERNAL_SERVER_ERROR)
            errorMessage.message = errorMsg

            asyncResponse.resume(Response.serverError().entity(errorMessage).build())
            LOG.error(errorMsg)
        }
    }

    companion object {
        const val HOST_NAME_PARAM: String = "hostName"
        private val LOG: Logger = LoggerFactory.getLogger(ResolveIpAddressService::class.java)
        const val RESOURCE_PATH = "resolve-ip-address"
    }
}