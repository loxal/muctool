/*
 * MUCtool Web Toolkit
 *
 * Copyright 2017 Alexander Orlov <alexander.orlov@loxal.net>. All rights reserved.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.loxal.muctool

import org.jetbrains.ktor.application.Application
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

    @Test
    fun stats() = withTestApplication(Application::main) {
        with(handleRequest(HttpMethod.Get, "stats")) {
            assertEquals(HttpStatusCode.OK, response.status())
            assertTrue(response.content!!.isNotEmpty())
            if (System.getenv("BUILD_NUMBER") == null)
                assertEquals(-552594242, response.content?.hashCode())
        }
    }

    @Test
    fun encoding() = withTestApplication(Application::main) {
        // TODO value=some Base64 value
        // TODO value=some non-Base64 value
        // TODO value=URL, unencoded x
        // TODO value=URL, encoded  x
        // TODO value=URL, emoji  x
        // TODO provide charset parameter x
        with(handleRequest(HttpMethod.Get, "encoding?value=https://example.com")) {
            assertEquals(HttpStatusCode.OK, response.status())
            LOG.info("response.content: ${response.content}")
            assertEquals(-1061403509, response.content?.hashCode())
        }

        with(handleRequest(HttpMethod.Get, "encoding?value=https%3A%2F%2Fexample.com")) {
            assertEquals(HttpStatusCode.OK, response.status())
            LOG.info("response.content: ${response.content}")
            assertEquals(-1061403509, response.content?.hashCode())
        }

        with(handleRequest(HttpMethod.Get, "encoding?value=aHR0cHM6Ly9leGFtcGxlLmNvbQ==")) {
            assertEquals(HttpStatusCode.OK, response.status())
            LOG.info("response.content: ${response.content}")
            assertEquals(-901847569, response.content?.hashCode())
        }

        with(handleRequest(HttpMethod.Get, "encoding?value=\uD83E\uDD84")) {
            assertEquals(HttpStatusCode.OK, response.status())
            LOG.info("response.content: ${response.content}")
            assertEquals(-216594356, response.content?.hashCode())
        }

        with(handleRequest(HttpMethod.Get, "encoding?value=\uD83E\uDD84&charset=UTF-8")) {
            assertEquals(HttpStatusCode.OK, response.status())
            LOG.info("response.content: ${response.content}")
            assertEquals(-216594356, response.content?.hashCode())
        }
    }

    @Test
    fun testRedirection() = withTestApplication(Application::main) {
        //        with(handleRequest(HttpMethod.Get, "http://sky.loxal.net/dilbert-quote/index.html")) {
//            assertTrue(requestHandled)
//            assertEquals(HttpStatusCode.MovedPermanently, response.status())
//            assertNull(response.content)
//            assertNotNull(response.headers[HttpHeaders.Location])
//            assertEquals("$dilbertService/dilbert-quote/index.html", response.headers[HttpHeaders.Location])
//        }
//        with(handleRequest(HttpMethod.Get, "dilbert-quote/programmer")) {
//            assertTrue(requestHandled)
//            assertEquals(HttpStatusCode.MovedPermanently, response.status())
//            assertNull(response.content)
//            assertNotNull(response.headers[HttpHeaders.Location])
//            assertEquals("$dilbertService/dilbert-quote/programmer", response.headers[HttpHeaders.Location])
//        }
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
        with(handleRequest(HttpMethod.Get, "/non-existing")) {
            // should actually return 404 later
            assert(null == response.status())
            assert(null == response.content)
        }
    }

    @Test
    fun testWhoisLookupForAsn() = withTestApplication(Application::main) {

        // TODO activate once Ktor fixed the queryParam encoding issue, solving "4:254a%6" problem
//        with(handleRequest(HttpMethod.Get, "whois/asn?queryIP=fe80::b87a:9e0b:8c74:254a%6")) {
//            assertTrue(requestHandled)
//            assertEquals(HttpStatusCode.InternalServerError, response.status())
//            assertNull(response.content)
//        }

        val whoisEndpoint = "whois/asn"

        `provide IP implicitly in the request & 404 because it cannot be found`(whoisEndpoint)

        `provide IP in query`(whoisEndpoint, -1064249020)

        `query for localhost`(whoisEndpoint)

        `query for 127_0_0_1`(whoisEndpoint)

        `query for an unknown, short IPv6`(whoisEndpoint)

        `query for a known IPv6`(whoisEndpoint, 1423155314)

        `query for localhost in IPv6`(whoisEndpoint)

        `query for an unknown IPv6 with subnet`(whoisEndpoint)

        `query for a malformed IP address`(whoisEndpoint)
    }

    private fun TestApplicationHost.`provide IP in query`(whoisEndpoint: String, hashCodeForQueryIPresponse: Int) {
        with(handleRequest(HttpMethod.Get, "$whoisEndpoint?queryIP=${AppTest.queryIP}&clientId=${UUID.randomUUID()}")) {
            assertTrue(requestHandled)
            assertEquals(HttpStatusCode.OK, response.status())
            assertNotNull(response.content)
            LOG.info("response.content?.hashCode(): ${response.content?.hashCode()}")
            assertEquals(hashCodeForQueryIPresponse, response.content?.hashCode())
        }
    }

    private fun TestApplicationHost.`provide IP implicitly in the request & 404 because it cannot be found`(whoisEndpoint: String) {
        with(handleRequest(HttpMethod.Get, "$whoisEndpoint?clientId=${UUID.randomUUID()}")) {
            assertTrue(requestHandled)
            assertEquals(HttpStatusCode.NotFound, response.status())
            assertNull(response.content)
            assertEquals(null, response.content?.hashCode())
        }
    }

    private fun TestApplicationHost.`query for an unknown, short IPv6`(whoisEndpoint: String) {
        with(handleRequest(HttpMethod.Get, "$whoisEndpoint?queryIP=fd00::b87a:9e0b:8c74:254a&clientId=${UUID.randomUUID()}")) {
            assertTrue(requestHandled)
            assertEquals(HttpStatusCode.NotFound, response.status())
            assertNull(response.content)
        }
    }

    private fun TestApplicationHost.`query for an unknown IPv6 with subnet`(whoisEndpoint: String) {
        with(handleRequest(HttpMethod.Get, "$whoisEndpoint?queryIP=fd00::b87a:9e0b:8c74:254a/64&clientId=${UUID.randomUUID()}")) {
            assertTrue(requestHandled)
            assertEquals(HttpStatusCode.NotFound, response.status())
            assertNull(response.content)
        }
    }

    private fun TestApplicationHost.`query for a malformed IP address`(whoisEndpoint: String) {
        with(handleRequest(HttpMethod.Get, "$whoisEndpoint?queryIP=${UUID.randomUUID()}&clientId=${UUID.randomUUID()}")) {
            assertTrue(requestHandled)
            assertEquals(HttpStatusCode.NotFound, response.status())
            assertNull(response.content)
        }
    }

    private fun TestApplicationHost.`query for localhost in IPv6`(whoisEndpoint: String) {
        with(handleRequest(HttpMethod.Get, "$whoisEndpoint?queryIP=::1&clientId=${UUID.randomUUID()}")) {
            assertTrue(requestHandled)
            assertEquals(HttpStatusCode.NotFound, response.status())
            assertNull(response.content)
        }
    }

    private fun TestApplicationHost.`query for a known IPv6`(whoisEndpoint: String, hashCodeForQueryIPresponse: Int) {
        with(handleRequest(HttpMethod.Get,
                "$whoisEndpoint?queryIP=2001:a61:1010:7c01:b87a:9e0b:8c74:254a" +
                        "&clientId=f5c88067-88f8-4a5b-b43e-bf0e10a8b857"
        )) {
            assertTrue(requestHandled)
            assertEquals(HttpStatusCode.OK, response.status())
            LOG.info("response.content?.hashCode(): ${response.content?.hashCode()}")
            assertEquals(hashCodeForQueryIPresponse, response.content?.hashCode())
        }
    }

    private fun TestApplicationHost.`query for 127_0_0_1`(whoisEndpoint: String) {
        with(handleRequest(HttpMethod.Get, "$whoisEndpoint?queryIP=127.0.0.1&clientId=${UUID.randomUUID()}")) {
            assertTrue(requestHandled)
            assertEquals(HttpStatusCode.NotFound, response.status())
            assertNull(response.content)
        }
    }

    private fun TestApplicationHost.`query for localhost`(whoisEndpoint: String) {
        with(handleRequest(HttpMethod.Get, "$whoisEndpoint?queryIP=localhost&clientId=${UUID.randomUUID()}")) {
            assertTrue(requestHandled)
            assertEquals(HttpStatusCode.NotFound, response.status())
            assertNull(response.content)
        }
    }

    private fun TestApplicationHost.`query without clientId`(whoisEndpoint: String) {
        with(handleRequest(HttpMethod.Get, "$whoisEndpoint?queryIP=185.17.205.98")) {
            assertTrue(requestHandled)
            assertEquals(HttpStatusCode.BadRequest, response.status())
            assertTrue(response.content!!.isNotBlank())
        }
    }

    private fun TestApplicationHost.`simplified consumption, query without clientId`(whoisEndpoint: String) {
        with(handleRequest(HttpMethod.Get, "$whoisEndpoint?queryIP=185.17.205.98")) {
            assertTrue(requestHandled)
            assertEquals(HttpStatusCode.OK, response.status())
            assertTrue(response.content!!.isNotBlank())
        }
    }

    private fun TestApplicationHost.`query with malformed clientId`(whoisEndpoint: String) {
        with(handleRequest(HttpMethod.Get, "$whoisEndpoint?queryIP=185.17.205.98&clientId=malformed")) {
            assertTrue(requestHandled)
            assertEquals(HttpStatusCode.BadRequest, response.status())
            assertTrue(response.content!!.isNotBlank())
        }
    }

    @Test
    fun lookupWhois() = withTestApplication(Application::main) {
        val whoisEndpoint = "whois"

        `provide IP implicitly in the request & 404 because it cannot be found`(whoisEndpoint)

        `provide IP in query`(whoisEndpoint, -76792497)
        `query for a known IPv6`(whoisEndpoint, 1910192781)

        `simplified consumption, query without clientId`(whoisEndpoint)
        `query with malformed clientId`(whoisEndpoint)

        `query for localhost`(whoisEndpoint)

        `query for 127_0_0_1`(whoisEndpoint)

        `query for an unknown, short IPv6`(whoisEndpoint)

        `query for localhost in IPv6`(whoisEndpoint)

        `query for an unknown IPv6 with subnet`(whoisEndpoint)

        `query for a malformed IP address`(whoisEndpoint)
    }

    @Test
    fun testWhoisLookupForCity() = withTestApplication(Application::main) {
        val whoisEndpoint = "whois/city"

        `provide IP implicitly in the request & 404 because it cannot be found`(whoisEndpoint)

        `provide IP in query`(whoisEndpoint, -1463149407)

        `query for localhost`(whoisEndpoint)

        `query for 127_0_0_1`(whoisEndpoint)

        `query for an unknown, short IPv6`(whoisEndpoint)

        `query for a known IPv6`(whoisEndpoint, 382961490)

        `query for localhost in IPv6`(whoisEndpoint)

        `query for an unknown IPv6 with subnet`(whoisEndpoint)

        `query for a malformed IP address`(whoisEndpoint)
    }

    @Test
    fun testWhoisLookupForCountry() = withTestApplication(Application::main) {
        val whoisEndpoint = "whois/country"

        `provide IP implicitly in the request & 404 because it cannot be found`(whoisEndpoint)

        `provide IP in query`(whoisEndpoint, 1378463102)

        `query for localhost`(whoisEndpoint)

        `query for 127_0_0_1`(whoisEndpoint)

        `query for an unknown, short IPv6`(whoisEndpoint)

        `query for a known IPv6`(whoisEndpoint, -1718675560)

        `query for localhost in IPv6`(whoisEndpoint)

        `query for an unknown IPv6 with subnet`(whoisEndpoint)

        `query for a malformed IP address`(whoisEndpoint)
    }

    companion object {
        val LOG: Logger = LoggerFactory.getLogger(AppTest::class.java)
        val queryIP = "88.217.181.79"
    }
}
