/*
 * Copyright 2017 Alexander Orlov <alexander.orlov@loxal.net>. All rights reserved.
 */

package net.loxal.muctool

import org.jetbrains.ktor.application.Application
import org.jetbrains.ktor.http.HttpMethod
import org.jetbrains.ktor.http.HttpStatusCode
import org.jetbrains.ktor.testing.handleRequest
import org.jetbrains.ktor.testing.withTestApplication
import org.junit.Test
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class AppTest {
    @Test fun testRedirection() = withTestApplication(Application::main) {
        with(handleRequest(HttpMethod.Get, "dilbert-quote/index.html")) {
            assertTrue(requestHandled)
            assertEquals(HttpStatusCode.MovedPermanently, response.status())
            assertNull(response.content)
            assertNotNull(response.headers["Location"])
            assertEquals("$dilbertService/dilbert-quote/index.html", response.headers["Location"])
        }
        with(handleRequest(HttpMethod.Get, "dilbert-quote/programmer")) {
            assertTrue(requestHandled)
            assertEquals(HttpStatusCode.MovedPermanently, response.status())
            assertNull(response.content)
            assertNotNull(response.headers["Location"])
            assertEquals("$dilbertService/dilbert-quote/programmer", response.headers["Location"])
        }
        with(handleRequest(HttpMethod.Get, "test")) {
            assertEquals(HttpStatusCode.OK, response.status())
            assertTrue(response.content!!.startsWith("Serving entropy..."))
        }
        with(handleRequest(HttpMethod.Get, "index.html")) {
            assertTrue(requestHandled)
        }
        with(handleRequest(HttpMethod.Get, "/")) {
            assertTrue(requestHandled)
        }
    }

    @Test fun testWhois() = withTestApplication(Application::main) {
        with(handleRequest(HttpMethod.Get, "whois/asn")) {
            assertTrue(requestHandled)
            assertEquals(HttpStatusCode.NotFound, response.status())
            assertNull(response.content)
        }
        with(handleRequest(HttpMethod.Get, "whois/city")) {
            assertTrue(requestHandled)
            assertEquals(HttpStatusCode.NotFound, response.status())
            assertNull(response.content)
        }
        with(handleRequest(HttpMethod.Get, "whois/country")) {
            assertTrue(requestHandled)
            assertEquals(HttpStatusCode.NotFound, response.status())
            assertNull(response.content)
        }
    }

    companion object {
        val LOG: Logger = LoggerFactory.getLogger(AppTest::class.java)
    }
}