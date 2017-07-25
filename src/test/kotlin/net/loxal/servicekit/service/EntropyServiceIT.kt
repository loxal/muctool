/*
 * Copyright 2017 Alexander Orlov <alexander.orlov@loxal.net>. All rights reserved.
 */

package net.loxal.servicekit.service

import net.loxal.servicekit.endpoint.AbstractEndpointTest
import org.junit.Test
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response
import kotlin.test.assertEquals

class EntropyServiceIT : AbstractEndpointTest() {
    @Test
    fun entropy() {
        val response = prepareGenericRequest(EntropyService.RESOURCE_PATH).request().get()

        assertEquals(Response.Status.OK.statusCode.toLong(), response.status.toLong())
        assertEquals(MediaType.APPLICATION_JSON_TYPE, response.mediaType)

        val entropyResponse = response.readEntity(String::class.java)
        assertEquals(48, entropyResponse.length)
        LOG.info(entropyResponse)
    }
}