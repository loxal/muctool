/*
 * Copyright 2017 Alexander Orlov <alexander.orlov@loxal.net>. All rights reserved.
 */

package net.loxal.muctool

import org.jetbrains.ktor.application.Application
import org.jetbrains.ktor.http.HttpHeaders
import org.jetbrains.ktor.http.HttpMethod
import org.jetbrains.ktor.http.HttpStatusCode
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
        with(handleRequest(HttpMethod.Get, "whois/asn")) {
            assertTrue(requestHandled)
            assertEquals(HttpStatusCode.NotFound, response.status())
            assertNull(response.content)
            assertEquals(null, response.content?.hashCode())
        }
        with(handleRequest(HttpMethod.Get, "whois/asn?queryIP=$queryIP")) {
            assertTrue(requestHandled)
            assertEquals(HttpStatusCode.OK, response.status())
            assertNotNull(response.content)
            val hashCodeForQueryIPresponse: Int = -1064249020
            assertEquals(hashCodeForQueryIPresponse, response.content?.hashCode())
        }

        with(handleRequest(HttpMethod.Get, "whois/asn?queryIP=localhost")) {
            assertTrue(requestHandled)
            assertEquals(HttpStatusCode.NotFound, response.status())
            assertNull(response.content)
        }

        with(handleRequest(HttpMethod.Get, "whois/asn?queryIP=127.0.0.1")) {
            assertTrue(requestHandled)
            assertEquals(HttpStatusCode.NotFound, response.status())
            assertNull(response.content)
        }

        // TODO activate once Ktor fixed the queryParam encoding issue
//        with(handleRequest(HttpMethod.Get, "whois/asn?queryIP=fe80::b87a:9e0b:8c74:254a%6")) {
//            assertTrue(requestHandled)
//            assertEquals(HttpStatusCode.InternalServerError, response.status())
//            assertNull(response.content)
//        }

        with(handleRequest(HttpMethod.Get, "whois/asn?queryIP=fd00::b87a:9e0b:8c74:254a/64")) {
            assertTrue(requestHandled)
            assertEquals(HttpStatusCode.NotFound, response.status())
            assertNull(response.content)
        }

        with(handleRequest(HttpMethod.Get, "whois/asn?queryIP=fd00::b87a:9e0b:8c74:254a")) {
            assertTrue(requestHandled)
            assertEquals(HttpStatusCode.NotFound, response.status())
            assertNull(response.content)
        }

        with(handleRequest(HttpMethod.Get, "whois/asn?queryIP=2001:a61:1010:7c01:b87a:9e0b:8c74:254a")) {
            assertTrue(requestHandled)
            assertEquals(HttpStatusCode.OK, response.status())
            assertEquals(1423155314, response.content?.hashCode())
        }

        with(handleRequest(HttpMethod.Get, "whois/asn?queryIP=::1")) {
            assertTrue(requestHandled)
            assertEquals(HttpStatusCode.NotFound, response.status())
            assertNull(response.content)
        }

        with(handleRequest(HttpMethod.Get, "whois/asn?queryIP=${UUID.randomUUID()}")) {
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