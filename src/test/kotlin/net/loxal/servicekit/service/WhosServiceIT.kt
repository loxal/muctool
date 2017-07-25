/*
 * Copyright 2017 Alexander Orlov <alexander.orlov@loxal.net>. All rights reserved.
 */

package net.loxal.servicekit.service

import net.loxal.servicekit.endpoint.AbstractEndpointTest
import net.loxal.servicekit.model.whois.Host
import org.junit.Test
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response
import kotlin.test.assertEquals

class WhosServiceIT : AbstractEndpointTest() {
    @Test
    fun validateHost() {
        val response = prepareGenericRequest(WhosService.Companion.RESOURCE_PATH).request().get()

        assertEquals(Response.Status.OK.statusCode.toLong(), response.status.toLong())
        assertEquals(MediaType.APPLICATION_JSON_TYPE, response.mediaType)

        val localhost = "127.0.0.1"
        val host = response.readEntity<Host>(Host::class.java)
        assertEquals(localhost, host.address)
        assertEquals(localhost, host.name)
    }
}