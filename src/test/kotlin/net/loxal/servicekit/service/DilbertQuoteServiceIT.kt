/*
 * Copyright 2017 Alexander Orlov <alexander.orlov@loxal.net>. All rights reserved.
 */

package net.loxal.servicekit.service

import net.loxal.servicekit.endpoint.AbstractEndpointTest
import net.loxal.servicekit.model.dilbert.Dict
import net.loxal.servicekit.model.dilbert.Quote
import net.loxal.servicekit.service.DilbertQuoteService.Companion.initDilbertesqueExpert
import net.loxal.servicekit.service.DilbertQuoteService.Companion.initDilbertesqueQuote
import org.junit.Test
import javax.ws.rs.core.Response
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class DilbertQuoteServiceIT : AbstractEndpointTest() {

    private val RESOURCE_PATH_EXPERT = "${DilbertQuoteService.RESOURCE_PATH}/${DilbertQuoteService.RESOURCE_PATH_EXPERT}"
    private val RESOURCE_PATH_ENTERPRISE = "${DilbertQuoteService.RESOURCE_PATH}/${DilbertQuoteService.RESOURCE_PATH_ENTERPRISE}"
    private val RESOURCE_PATH_PROGRAMMER = "${DilbertQuoteService.RESOURCE_PATH}/${DilbertQuoteService.RESOURCE_PATH_PROGRAMMER}"
    private val RESOURCE_PATH_MANAGER = "${DilbertQuoteService.RESOURCE_PATH}/${DilbertQuoteService.RESOURCE_PATH_MANAGER}"

    @Test fun getExpertDilberty(): Unit = retrieveSingleQuote(RESOURCE_PATH_EXPERT)
    @Test fun getEnterpriseQuote(): Unit = retrieveSingleQuote(RESOURCE_PATH_ENTERPRISE)

    @Test fun getEnterpriseQuoteViaId() =
            retrieveQuoteById(resource = RESOURCE_PATH_ENTERPRISE, dilbertesque = initDilbertesqueQuote("quotes-dilbert-enterprise.json"))

    @Test fun getProgrammerQuote(): Unit = retrieveSingleQuote(RESOURCE_PATH_PROGRAMMER)

    @Test fun getProgrammerQuoteViaId() =
            retrieveQuoteById(resource = RESOURCE_PATH_PROGRAMMER, dilbertesque = initDilbertesqueQuote("quotes-dilbert-programmer.json"))

    @Test fun getExpertDilbertyById(): Unit =
            retrieveDictById(resource = RESOURCE_PATH_EXPERT, dilbertesque = initDilbertesqueExpert())

    @Test fun getManagerQuote(): Unit = retrieveSingleQuote(RESOURCE_PATH_MANAGER)

    @Test fun getManagerQuoteViaId() =
            retrieveQuoteById(resource = RESOURCE_PATH_MANAGER, dilbertesque = initDilbertesqueQuote("quotes-dilbert-manager.json"))

    private fun retrieveSingleQuote(resource: String) {
        val response = prepareGenericRequest(resource).request().get()
        assertEquals(Response.Status.OK.statusCode, response.status)
        assertEquals(DilbertQuoteService.mediaType, response.mediaType.toString())

        val quote = response.readEntity(Quote::class.java)
        assertNotNull(quote.id)
        assertTrue(quote.id is Int)
        assertNotNull(quote.quote)
        assertTrue(quote.quote is String)
    }

    private fun retrieveQuoteById(resource: String, dilbertesque: List<Quote>) {
        val (quoteId, response) = validateGenericResponse(resource)

        val dilberty = response.readEntity(Quote::class.java)
        val quoteIndex = quoteId - 1
        assertEquals(dilbertesque[quoteIndex], dilberty)
    }

    private fun retrieveDictById(resource: String, dilbertesque: List<Dict>) {
        val (quoteId, response) = validateGenericResponse(resource)

        val dilberty = response.readEntity(Dict::class.java)
        val quoteIndex = quoteId - 1
        assertEquals(dilbertesque[quoteIndex], dilberty)
    }

    private fun validateGenericResponse(resource: String): Pair<Int, Response> {
        val quoteId = 2
        val response = prepareGenericRequest(resource)
                .path(quoteId.toString()).request().get()
        assertEquals(Response.Status.OK.statusCode, response.status)
        assertEquals(DilbertQuoteService.mediaType, response.mediaType.toString())
        return Pair(quoteId, response)
    }
}