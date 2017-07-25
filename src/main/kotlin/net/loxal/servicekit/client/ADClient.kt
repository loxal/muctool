/*
 * Copyright 2017 Alexander Orlov <alexander.orlov@loxal.net>. All rights reserved.
 */

package net.loxal.servicekit.client

import net.loxal.servicekit.endpoint.appdirect.dto.ADError
import net.loxal.servicekit.endpoint.appdirect.dto.Event
import net.loxal.servicekit.endpoint.appdirect.dto.EventType
import net.loxal.servicekit.endpoint.appdirect.dto.Result
import oauth.signpost.OAuthConsumer
import oauth.signpost.basic.DefaultOAuthConsumer
import org.slf4j.LoggerFactory
import java.net.URI
import java.net.URL
import javax.ws.rs.client.ClientBuilder
import javax.ws.rs.container.AsyncResponse
import javax.ws.rs.core.Response

/**
 * AppDirect client
 */
class ADClient {

    private fun fetchEvent(uri: URI? = URI.create("")): Response {
        val signedUrl = oAuthConsumer.sign(uri.toString())

        val fetchedEvent = CLIENT.target(signedUrl).request().get()

        if (Response.Status.Family.SUCCESSFUL == fetchedEvent.statusInfo.family) {
            return fetchedEvent
        } else {
            val error = fetchedEvent.readEntity(ADError::class.java)
            LOG.error("$error")

            return Response.status(fetchedEvent.status).build()
        }
    }

    fun handleAppDirectEvent(asyncResponse: AsyncResponse, eventUrl: URL?, eventType: EventType) {
        val fetchedEvent: Response = fetchEvent(eventUrl?.toURI())

        if (Response.Status.Family.SUCCESSFUL == fetchedEvent.statusInfo.family) {
            val appDirectEvent = fetchedEvent.readEntity(Event::class.java)
            val accountIdentifier: String? = appDirectEvent.creator?.uuid

            LOG.info("$appDirectEvent")

            asyncResponse.resume(Response.ok(Result(
                    success = true,
                    accountIdentifier = accountIdentifier,
                    message = eventType.name
            )).build())
        } else {
            // handle failure in a custom way
            val error = fetchedEvent.readEntity(ADError::class.java)
            LOG.error("$error")

            asyncResponse.resume(Response.status(fetchedEvent.status).entity(error).build())
        }
    }


    companion object {
        private val consumerKey: String = System.getenv("appdirect_oAuth_consumer_key")
        private val consumerSecret: String = System.getenv("appdirect_oAuth_consumer_secret")

        private val LOG = LoggerFactory.getLogger(ADClient::class.java)
        private val CLIENT = ClientBuilder.newClient()
        private val oAuthConsumer: OAuthConsumer

        init {
            oAuthConsumer = DefaultOAuthConsumer(consumerKey, consumerSecret)
        }
    }
}