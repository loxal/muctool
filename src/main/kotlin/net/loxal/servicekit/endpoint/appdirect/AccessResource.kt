/*
 * Copyright 2017 Alexander Orlov <alexander.orlov@loxal.net>. All rights reserved.
 */

package net.loxal.servicekit.endpoint.appdirect

import net.loxal.servicekit.client.ADClient
import net.loxal.servicekit.endpoint.Endpoint
import net.loxal.servicekit.endpoint.appdirect.dto.EventType
import java.net.URL
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.QueryParam
import javax.ws.rs.container.AsyncResponse
import javax.ws.rs.container.Suspended

@Path(AccessResource.RESOURCE_PATH)
class AccessResource : Endpoint() {

    @Path("assign")
    @GET
    fun assign(
            @QueryParam(SubscriptionResource.EVENT_URL_QUERY_PARAM) eventUrl: URL?,
            @QueryParam(SubscriptionResource.TOKEN_QUERY_PARAM) token: String?,
            @Suspended asyncResponse: AsyncResponse
    ) {
        Endpoint.LOG.info("token = $token")

        appDirectClient.handleAppDirectEvent(asyncResponse, eventUrl, EventType.USER_ASSIGNMENT)
    }

    @Path("unassign")
    @GET
    fun unassign(
            @QueryParam(SubscriptionResource.EVENT_URL_QUERY_PARAM) eventUrl: URL?,
            @QueryParam(SubscriptionResource.TOKEN_QUERY_PARAM) token: String?,
            @Suspended asyncResponse: AsyncResponse
    ) {
        Endpoint.LOG.info("token = $token")

        appDirectClient.handleAppDirectEvent(asyncResponse, eventUrl, EventType.USER_UNASSIGNMENT)
    }

    companion object {
        const val RESOURCE_PATH = "${SubscriptionResource.APPDIRECT_ROOT_PATH}/user"
        private val appDirectClient = ADClient()
    }
}