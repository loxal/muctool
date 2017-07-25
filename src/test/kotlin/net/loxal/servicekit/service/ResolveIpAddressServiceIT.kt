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
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class ResolveIpAddressServiceIT : AbstractEndpointTest() {

    @Test fun assureHostAddress() {
        val hostName = "localhost"

        val response = prepareGenericRequest(ResolveIpAddressService.Companion.RESOURCE_PATH).queryParam(ResolveIpAddressService.HOST_NAME_PARAM, hostName).request().get()

        assertEquals(Response.Status.OK.statusCode.toLong(), response.status.toLong())
        assertEquals(MediaType.APPLICATION_JSON_TYPE, response.mediaType)

        val host = response.readEntity<Host>(Host::class.java)
        assertNotNull(host.address)
        assertTrue(host.address.length in 7..19)
        assertEquals(hostName, host.name)
    }
}