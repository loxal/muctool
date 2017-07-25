/*
 * Copyright 2017 Alexander Orlov <alexander.orlov@loxal.net>. All rights reserved.
 */

package net.loxal.servicekit.endpoint

import net.loxal.servicekit.filter.AccessControlFilter
import org.junit.Test
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response
import kotlin.test.assertEquals

class CommonIT : AbstractEndpointTest() {
    @Test
    fun hasIndexWelcomePage() {
        val response = AbstractEndpointTest.prepareGenericRequest(AbstractEndpointTest.resourcePath).request().get()
        AbstractEndpointTest.LOG.info("resourcePath: ${AbstractEndpointTest.resourcePath}")

        assertEquals(Response.Status.OK.statusCode, response.status)
        assertEquals(MediaType.TEXT_HTML_TYPE, response.mediaType)
    }

    @Test
    fun nonExistentPathNotFound() {
        val response = AbstractEndpointTest.prepareGenericRequest(AbstractEndpointTest.resourcePath).path(AbstractEndpointTest.NON_EXISTENT).request().get()
        AbstractEndpointTest.LOG.info("resourcePath: ${AbstractEndpointTest.resourcePath}")

        assertEquals(Response.Status.NOT_FOUND.statusCode, response.status)
        assertEquals(MediaType.APPLICATION_JSON_TYPE, response.mediaType)
        assureCorsHeaders(response)
    }

    private fun assureCorsHeaders(response: Response) {
        assertEquals(AccessControlFilter.allowOriginHeaderValue, response.getHeaderString(AccessControlFilter.allowOriginHeader))
        assertEquals(AccessControlFilter.allowHeadersValue.toString(), response.getHeaderString(AccessControlFilter.allowHeadersHeader).toString())
    }
}