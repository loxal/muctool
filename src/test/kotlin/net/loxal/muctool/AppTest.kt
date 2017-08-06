/*
 * Copyright 2017 Alexander Orlov <alexander.orlov@loxal.net>. All rights reserved.
 */

package net.loxal.muctool

import org.jetbrains.ktor.application.Application
import org.jetbrains.ktor.http.HttpHeaders
import org.jetbrains.ktor.http.HttpMethod
import org.jetbrains.ktor.http.HttpStatusCode
import org.jetbrains.ktor.testing.TestApplicationHost
import org.jetbrains.ktor.testing.handleRequest
import org.jetbrains.ktor.testing.withTestApplication
import org.junit.Test
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.*
import kotlin.test.*

class AppTest {

    @Test fun test() = withTestApplication(Application::main) {
        with(handleRequest(HttpMethod.Get, "test")) {
            assertEquals(HttpStatusCode.OK, response.status())
            assertTrue(response.content!!.startsWith("Serving entropy..."))
        }
    }

    @Test fun testRedirection() = withTestApplication(Application::main) {
        with(handleRequest(HttpMethod.Get, "dilbert-quote/index.html")) {
            assertTrue(requestHandled)
            assertEquals(HttpStatusCode.MovedPermanently, response.status())
            assertNull(response.content)
            assertNotNull(response.headers[HttpHeaders.Location])
            assertEquals("$dilbertService/dilbert-quote/index.html", response.headers[HttpHeaders.Location])
        }
        with(handleRequest(HttpMethod.Get, "dilbert-quote/programmer")) {
            assertTrue(requestHandled)
            assertEquals(HttpStatusCode.MovedPermanently, response.status())
            assertNull(response.content)
            assertNotNull(response.headers[HttpHeaders.Location])
            assertEquals("$dilbertService/dilbert-quote/programmer", response.headers[HttpHeaders.Location])
        }
        with(handleRequest(HttpMethod.Get, "index.html")) {
            assertTrue(requestHandled)
            assertEquals(HttpStatusCode.OK, response.status())
            assertFalse(response.content.isNullOrBlank())
        }
        with(handleRequest(HttpMethod.Get, "/")) {
            assertTrue(requestHandled)
            assertEquals(HttpStatusCode.OK, response.status())
            assertFalse(response.content.isNullOrBlank())
        }
    }

    @Test fun testWhoisLookupForAsn() = withTestApplication(Application::main) {

        // TODO activate once Ktor fixed the queryParam encoding issue
//        with(handleRequest(HttpMethod.Get, "whois/asn?queryIP=fe80::b87a:9e0b:8c74:254a%6")) {
//            assertTrue(requestHandled)
//            assertEquals(HttpStatusCode.InternalServerError, response.status())
//            assertNull(response.content)
//        }

        val whoisEndpoint = "whois/asn"

        `provide IP implicitly in the request & 404 because it cannot be found`(whoisEndpoint)

        `provide IP in query`(whoisEndpoint)

        `query for localhost`(whoisEndpoint)

        `query for 127_0_0_1`(whoisEndpoint)

        `query for an unknown IPv6 with subnet`(whoisEndpoint)

        `query for an unknown, short IPv6`(whoisEndpoint)

        `query for a known IPv6`(whoisEndpoint)

        `query for localhost in IPv6`(whoisEndpoint)

        `query for a malformed IP address`(whoisEndpoint)
    }

    private fun TestApplicationHost.`query for an unknown, short IPv6`(whoisEndpoint: String) {
        with(handleRequest(HttpMethod.Get, "$whoisEndpoint?queryIP=fd00::b87a:9e0b:8c74:254a")) {
            assertTrue(requestHandled)
            assertEquals(HttpStatusCode.NotFound, response.status())
            assertNull(response.content)
        }
    }

    private fun TestApplicationHost.`query for a malformed IP address`(whoisEndpoint: String) {
        with(handleRequest(HttpMethod.Get, "$whoisEndpoint?queryIP=${UUID.randomUUID()}")) {
            assertTrue(requestHandled)
            assertEquals(HttpStatusCode.NotFound, response.status())
            assertNull(response.content)
        }
    }

    private fun TestApplicationHost.`query for localhost in IPv6`(whoisEndpoint: String) {
        with(handleRequest(HttpMethod.Get, "$whoisEndpoint?queryIP=::1")) {
            assertTrue(requestHandled)
            assertEquals(HttpStatusCode.NotFound, response.status())
            assertNull(response.content)
        }
    }

    internal fun TestApplicationHost.`query for a known IPv6`(whoisEndpoint: String) {
        with(handleRequest(HttpMethod.Get, "$whoisEndpoint?queryIP=2001:a61:1010:7c01:b87a:9e0b:8c74:254a")) {
            assertTrue(requestHandled)
            assertEquals(HttpStatusCode.OK, response.status())
            assertEquals(1423155314, response.content?.hashCode())
        }
    }

    private fun TestApplicationHost.`query for an unknown IPv6 with subnet`(whoisEndpoint: String) {
        with(handleRequest(HttpMethod.Get, "$whoisEndpoint?queryIP=fd00::b87a:9e0b:8c74:254a/64")) {
            assertTrue(requestHandled)
            assertEquals(HttpStatusCode.NotFound, response.status())
            assertNull(response.content)
        }
    }

    private fun TestApplicationHost.`query for 127_0_0_1`(whoisEndpoint: String) {
        with(handleRequest(HttpMethod.Get, "$whoisEndpoint?queryIP=127.0.0.1")) {
            assertTrue(requestHandled)
            assertEquals(HttpStatusCode.NotFound, response.status())
            assertNull(response.content)
        }
    }

    internal fun TestApplicationHost.`query for localhost`(whoisEndpoint: String) {
        with(handleRequest(HttpMethod.Get, "$whoisEndpoint?queryIP=localhost")) {
            assertTrue(requestHandled)
            assertEquals(HttpStatusCode.NotFound, response.status())
            assertNull(response.content)
        }
    }

    @Test fun testWhoisLookupForCity() = withTestApplication(Application::main) {
        with(handleRequest(HttpMethod.Get, "whois/city")) {
            assertTrue(requestHandled)
            assertEquals(HttpStatusCode.NotFound, response.status())
            assertNull(response.content)
        }
        with(handleRequest(HttpMethod.Get, "whois/city?queryIP=$queryIP")) {
            assertTrue(requestHandled)
            assertEquals(HttpStatusCode.OK, response.status())
            assertNotNull(response.content)
            assertEquals(1323, response.byteContent?.size)
        }
    }

    @Test fun testWhoisLookupForCountry() = withTestApplication(Application::main) {
        with(handleRequest(HttpMethod.Get, "whois/country")) {
            assertTrue(requestHandled)
            assertEquals(HttpStatusCode.NotFound, response.status())
            assertNull(response.content)
        }
        with(handleRequest(HttpMethod.Get, "whois/country?queryIP=$queryIP")) {
            assertTrue(requestHandled)
            assertEquals(HttpStatusCode.OK, response.status())
            assertNotNull(response.content)
            assertEquals(801, response.byteContent?.size)
        }
    }

    companion object {
        val LOG: Logger = LoggerFactory.getLogger(AppTest::class.java)
        val queryIP = "88.217.181.79"
    }
}

internal fun TestApplicationHost.`provide IP in query`(whoisEndpoint: String) {
    with(handleRequest(HttpMethod.Get, "$whoisEndpoint?queryIP=${AppTest.queryIP}")) {
        assertTrue(requestHandled)
        assertEquals(HttpStatusCode.OK, response.status())
        assertNotNull(response.content)
        val hashCodeForQueryIPresponse: Int = -1064249020
        assertEquals(hashCodeForQueryIPresponse, response.content?.hashCode())
    }
}

private fun TestApplicationHost.`provide IP implicitly in the request & 404 because it cannot be found`(whoisEndpoint: String) {
    with(handleRequest(HttpMethod.Get, whoisEndpoint)) {
        assertTrue(requestHandled)
        assertEquals(HttpStatusCode.NotFound, response.status())
        assertNull(response.content)
        assertEquals(null, response.content?.hashCode())
    }
}