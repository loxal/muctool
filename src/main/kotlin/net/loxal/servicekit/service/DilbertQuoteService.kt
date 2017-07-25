/*
 * Copyright 2017 Alexander Orlov <alexander.orlov@loxal.net>. All rights reserved.
 */

package net.loxal.servicekit.service

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import net.loxal.servicekit.endpoint.Endpoint
import net.loxal.servicekit.model.dilbert.Dict
import net.loxal.servicekit.model.dilbert.Quote
import org.slf4j.LoggerFactory
import java.io.InputStreamReader
import java.util.*
import java.util.concurrent.TimeUnit
import javax.servlet.http.HttpServletRequest
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.PathParam
import javax.ws.rs.Produces
import javax.ws.rs.container.AsyncResponse
import javax.ws.rs.container.ContainerRequestContext
import javax.ws.rs.container.Suspended
import javax.ws.rs.core.Context
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

@Produces(DilbertQuoteService.mediaType)
@Path(DilbertQuoteService.RESOURCE_PATH)
class DilbertQuoteService : Endpoint() {

    private fun fetchRandomDilberty(asyncResponse: AsyncResponse, request: HttpServletRequest, requestContext: ContainerRequestContext, dilbertesque: List<Any>) {
        asyncResponse.setTimeout(ASYNC_RESPONSE_TIMEOUT.toLong(), TimeUnit.SECONDS)

        val randomQuoteIndex = random.nextInt(dilbertesque.size)
        asyncResponse.resume(Response.ok(dilbertesque[randomQuoteIndex]).build())

        LOG.info("${requestContext.method} for $randomQuoteIndex ${request.contextPath}")
    }

    @Path(RESOURCE_PATH_EXPERT)
    @GET
    fun quoteExpert(@Context request: HttpServletRequest, @Context requestContext: ContainerRequestContext, @Suspended asyncResponse: AsyncResponse) {
        fetchRandomDilberty(asyncResponse, request, requestContext, dilbertesque = initDilbertesqueExpert())
    }

    @Path(RESOURCE_PATH_ENTERPRISE)
    @GET
    fun quoteEnterprise(@Context request: HttpServletRequest, @Context requestContext: ContainerRequestContext, @Suspended asyncResponse: AsyncResponse) {
        fetchRandomDilberty(asyncResponse, request, requestContext, dilbertesque = initDilbertesqueQuote("quotes-dilbert-enterprise.json"))
    }

    @Path(RESOURCE_PATH_MANAGER)
    @GET
    fun quoteManager(@Context request: HttpServletRequest, @Context requestContext: ContainerRequestContext, @Suspended asyncResponse: AsyncResponse) {
        fetchRandomDilberty(asyncResponse, request, requestContext, dilbertesque = initDilbertesqueQuote("quotes-dilbert-manager.json"))
    }

    @Path(RESOURCE_PATH_PROGRAMMER)
    @GET
    fun quoteProgrammer(@Context request: HttpServletRequest, @Context requestContext: ContainerRequestContext, @Suspended asyncResponse: AsyncResponse) {
        fetchRandomDilberty(asyncResponse, request, requestContext, dilbertesque = initDilbertesqueQuote("quotes-dilbert-programmer.json"))
    }

    @Path("$RESOURCE_PATH_EXPERT/$ID_PATH_PARAM_PLACEHOLDER")
    @GET
    fun quoteExpert(@Context request: HttpServletRequest, @Context requestContext: ContainerRequestContext,
                    @PathParam(ID_PATH_PARAM) quoteId: Int,
                    @Suspended asyncResponse: AsyncResponse) {
        fetchDilbertyById(asyncResponse, quoteId, request, requestContext, dilbertesque = initDilbertesqueExpert())
    }

    @Path("$RESOURCE_PATH_ENTERPRISE/$ID_PATH_PARAM_PLACEHOLDER")
    @GET
    fun quoteEnterprise(@Context request: HttpServletRequest, @Context requestContext: ContainerRequestContext,
                        @PathParam(ID_PATH_PARAM) quoteId: Int,
                        @Suspended asyncResponse: AsyncResponse) {
        fetchDilbertyById(asyncResponse, quoteId, request, requestContext, dilbertesque = initDilbertesqueQuote("quotes-dilbert-enterprise.json"))
    }

    private fun fetchDilbertyById(asyncResponse: AsyncResponse, quoteId: Int, request: HttpServletRequest, requestContext: ContainerRequestContext, dilbertesque: List<Any>) {
        asyncResponse.setTimeout(ASYNC_RESPONSE_TIMEOUT.toLong(), TimeUnit.SECONDS)

        val quoteIndex = quoteId - 1
        asyncResponse.resume(Response.ok(dilbertesque[quoteIndex]).build())

        LOG.info("${requestContext.method} for $quoteId ${request.contextPath}")
    }

    @Path("$RESOURCE_PATH_MANAGER/$ID_PATH_PARAM_PLACEHOLDER")
    @GET
    fun quoteManager(@Context request: HttpServletRequest, @Context requestContext: ContainerRequestContext,
                     @PathParam(ID_PATH_PARAM) quoteId: Int,
                     @Suspended asyncResponse: AsyncResponse) {
        fetchDilbertyById(asyncResponse, quoteId, request, requestContext, dilbertesque = initDilbertesqueQuote("quotes-dilbert-manager.json"))
    }

    @Path("$RESOURCE_PATH_PROGRAMMER/$ID_PATH_PARAM_PLACEHOLDER")
    @GET
    fun quoteProgrammer(@Context request: HttpServletRequest, @Context requestContext: ContainerRequestContext,
                        @PathParam(ID_PATH_PARAM) quoteId: Int,
                        @Suspended asyncResponse: AsyncResponse) {
        fetchDilbertyById(asyncResponse, quoteId, request, requestContext, dilbertesque = initDilbertesqueQuote("quotes-dilbert-programmer.json"))
    }

    companion object {
        private val LOG = LoggerFactory.getLogger(DilbertQuoteService::class.java)

        private val objectMapper = ObjectMapper()

        private val random: Random = Random()

        fun initDilbertesqueQuote(dilbertitySource: String): List<Quote> {
            class Quotes : TypeReference<List<Quote>>()

            val quotesType = Quotes()

            val dilbertityReader = InputStreamReader(DilbertQuoteService::class.java.getResourceAsStream(dilbertitySource), Charsets.UTF_8)
            val dilbertityData = dilbertityReader.readText()
            val dilbertity: List<Quote> = objectMapper.readValue(dilbertityData, quotesType)
            return dilbertity
        }

        fun initDilbertesqueExpert(): List<Dict> {
            class Dicts : TypeReference<List<Dict>>()

            val dictsType = Dicts()
            val quotesExpertReader = InputStreamReader(DilbertQuoteService::class.java.getResourceAsStream("dictionary-dilbert-expert.json"), Charsets.UTF_8)
            val quoteExpertData = quotesExpertReader.readText()
            val quotesExpert: List<Dict> = objectMapper.readValue(quoteExpertData, dictsType)
            return quotesExpert
        }

        const val RESOURCE_PATH = "dilbert-quote"
        const val RESOURCE_PATH_PROGRAMMER = "programmer"
        const val RESOURCE_PATH_MANAGER = "manager"
        const val RESOURCE_PATH_ENTERPRISE = "enterprise"
        const val RESOURCE_PATH_EXPERT = "expert"
        const val mediaType = MediaType.APPLICATION_JSON + ";charset=utf-8"
    }
}