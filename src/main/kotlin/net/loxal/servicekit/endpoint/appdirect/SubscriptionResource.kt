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

@Path(SubscriptionResource.RESOURCE_PATH)
class SubscriptionResource : Endpoint() {
    @Path("order")
    @GET
    fun create(
            @QueryParam(EVENT_URL_QUERY_PARAM) eventUrl: URL?,
            @QueryParam(TOKEN_QUERY_PARAM) token: String?,
            @Suspended asyncResponse: AsyncResponse
    ) {
        Endpoint.LOG.info("token = $token")

        appDirectClient.handleAppDirectEvent(asyncResponse, eventUrl, EventType.SUBSCRIPTION_ORDER)
    }

    @Path("change")
    @GET
    fun change(
            @QueryParam(EVENT_URL_QUERY_PARAM) eventUrl: URL?,
            @QueryParam(TOKEN_QUERY_PARAM) token: String?,
            @Suspended asyncResponse: AsyncResponse
    ) {
        Endpoint.LOG.info("token = $token")

        appDirectClient.handleAppDirectEvent(asyncResponse, eventUrl, EventType.SUBSCRIPTION_CHANGE)
    }

    @Path("cancel")
    @GET
    fun cancel(
            @QueryParam(EVENT_URL_QUERY_PARAM) eventUrl: URL?,
            @QueryParam(TOKEN_QUERY_PARAM) token: String?,
            @Suspended asyncResponse: AsyncResponse
    ) {
        Endpoint.LOG.info("token = $token")

        appDirectClient.handleAppDirectEvent(asyncResponse, eventUrl, EventType.SUBSCRIPTION_CANCEL)
    }

    @Path("status")
    @GET
    fun status(
            @QueryParam(EVENT_URL_QUERY_PARAM) eventUrl: URL?,
            @QueryParam(TOKEN_QUERY_PARAM) token: String?,
            @Suspended asyncResponse: AsyncResponse
    ) {
        Endpoint.LOG.info("token = $token")

        appDirectClient.handleAppDirectEvent(asyncResponse, eventUrl, EventType.SUBSCRIPTION_NOTICE)
    }

    @Path("custom")
    @GET
    fun custom(
            @QueryParam(EVENT_URL_QUERY_PARAM) eventUrl: URL?,
            @QueryParam(TOKEN_QUERY_PARAM) token: String?,
            @Suspended asyncResponse: AsyncResponse
    ) {
        Endpoint.LOG.info("token = $token")

        appDirectClient.handleAppDirectEvent(asyncResponse, eventUrl, EventType.ADDON)
    }

    companion object {
        const val TOKEN_QUERY_PARAM = "token"
        const val APPDIRECT_ROOT_PATH = "appdirect"
        const val RESOURCE_PATH = "$APPDIRECT_ROOT_PATH/subscription"
        const val EVENT_URL_QUERY_PARAM = "eventUrl"
        private val appDirectClient = ADClient()
    }
}