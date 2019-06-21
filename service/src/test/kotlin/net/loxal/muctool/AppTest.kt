/*
 * MUCtool Web Toolkit
 *
 * Copyright 2019 Alexander Orlov <alexander.orlov@loxal.net>. All rights reserved.
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

import io.ktor.application.Application
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.withTestApplication
import org.junit.Test
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.*
import kotlin.test.*

const val ipAddressWithInfo = "185.17.205.98"

class AppTest {
    @Test
    fun curl() = withTestApplication(Application::main) {
        with(handleRequest(HttpMethod.Get, "curl?url=https://en.wikipedia.org/wiki/Main_Page")) {
            val contentFragment = "Main page"
            val status = HttpStatusCode.OK
            assertEquals(status, response.status())
            assert(response.content!!.isNotEmpty())
            assert(response.content?.contains(contentFragment)!!)

            val curl = mapper.readValue(response.byteContent, Curl::class.java)
            assertEquals(status.value, curl.code)
            assertEquals(status.value, curl.statusCode)
            assert(curl.body!!.contains(contentFragment))
        }

        with(handleRequest(HttpMethod.Get, "curl?url=https://api.muctool.invalid")) {
            assertEquals(HttpStatusCode.NotFound, response.status())
            assertNull(response.content)
        }

        with(handleRequest(HttpMethod.Get, "curl?url=https://en.wikipedia.org/missing-resource")) {
            assertEquals(HttpStatusCode.OK, response.status())
            assertNotNull(response.content)

            val curl = mapper.readValue(response.byteContent, Curl::class.java)
            assertEquals(HttpStatusCode.NotFound.value, curl.code)
            assertEquals(HttpStatusCode.NotFound.value, curl.statusCode)
            assertFalse(curl.body!!.isEmpty())
        }

        with(handleRequest(HttpMethod.Get, "curl?url=http://api.muctool.de")) {
            assertEquals(HttpStatusCode.OK, response.status(), response.status().toString())
            assertNotNull(response.content)

            val curl = mapper.readValue(response.byteContent, Curl::class.java)
            assertEquals(HttpStatusCode.MovedPermanently.value, curl.code)
            assertEquals(HttpStatusCode.MovedPermanently.value, curl.statusCode)
            assert(curl.body!!.contains("<title>301 Moved Permanently</title>"))
        }
    }

    @Test
    fun stats() = withTestApplication(Application::main) {
        with(handleRequest(HttpMethod.Get, "stats")) {
            assertEquals(HttpStatusCode.OK, response.status())
            assertTrue(response.content!!.isNotEmpty())
            if (System.getenv("BUILD_NUMBER") === null) {
                assertEquals(-552594242, response.content?.hashCode())
            }
        }
    }

    @Test
    fun encoding() = withTestApplication(Application::main) {
        with(handleRequest(HttpMethod.Get, "encoding?value=https://example.com")) {
            assertEquals(HttpStatusCode.OK, response.status())
            LOG.info("response.content: ${response.content}")
            assertEquals(408897637, response.content?.hashCode())
        }

        with(handleRequest(HttpMethod.Get, "encoding?value=https%3A%2F%2Fexample.com")) {
            assertEquals(HttpStatusCode.OK, response.status())
            LOG.info("response.content: ${response.content}")
            assertEquals(408897637, response.content?.hashCode())
        }

        with(handleRequest(HttpMethod.Get, "encoding?value=aHR0cHM6Ly9leGFtcGxlLmNvbQ==")) {
            assertEquals(HttpStatusCode.OK, response.status())
            LOG.info("response.content: ${response.content}")
            assertEquals(-11017455, response.content?.hashCode())
        }

        with(handleRequest(HttpMethod.Get, "encoding?value=\uD83E\uDD84")) {
            assertEquals(HttpStatusCode.OK, response.status())
            LOG.info("response.content: ${response.content}")
            assertEquals(415, response.content?.length)
        }

        with(handleRequest(HttpMethod.Get, "encoding?value=\uD83E\uDD84&charset=ISO-8859-1")) {
            assertEquals(HttpStatusCode.OK, response.status())
            LOG.info("response.content: ${response.content}")
            assertEquals(355, response.content?.length)
        }

        with(handleRequest(HttpMethod.Get, "encoding?value=\uD83E\uDD84&charset=UTF-8")) {
            assertEquals(HttpStatusCode.OK, response.status())
            LOG.info("response.content: ${response.content}")
            assertEquals(415, response.content?.length)
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
////        with(handleRequest(HttpMethod.Get, "whois/asn?queryIP=fe80::b87a:9e0b:8c74:254a%256")) {
//            assertTrue(requestHandled)
//            assertEquals(HttpStatusCode.InternalServerError, response.status())
//            assertNull(response.content)
//        }

        val whoisEndpoint = "whois/asn"

        `provide IP implicitly in the request & 404 because it cannot be found`(whoisEndpoint)

        `provide IP in query`(whoisEndpoint, 127)

        `query for localhost`(whoisEndpoint)

        `query for 127_0_0_1`(whoisEndpoint)

        `query for an unknown, short IPv6`(whoisEndpoint)

        `query for a known IPv6`(whoisEndpoint, 152)

        `query for localhost in IPv6`(whoisEndpoint)

        `query for an unknown IPv6 with subnet`(whoisEndpoint)

        `query for a malformed IP address`(whoisEndpoint)
    }

    private fun `provide IP in query`(whoisEndpoint: String, checksum: Int) {
        withTestApplication(Application::main) {
            with(handleRequest(HttpMethod.Get, "$whoisEndpoint?queryIP=${AppTest.queryIP}&clientId=${UUID.randomUUID()}")) {
                assertTrue(requestHandled)
                assertEquals(HttpStatusCode.OK, response.status())
                assertNotNull(response.content)
                assertEquals(checksum, response.content?.length)
            }
        }
    }

    private fun `provide IP implicitly in the request & 404 because it cannot be found`(whoisEndpoint: String) {
        withTestApplication(Application::main) {
            with(handleRequest(HttpMethod.Get, "$whoisEndpoint?clientId=${UUID.randomUUID()}")) {
                assertTrue(requestHandled)
                assertEquals(HttpStatusCode.NotFound, response.status())
                assertNull(response.content)
                assertEquals(null, response.content?.hashCode())
            }
        }
    }

    private fun `query for an unknown, short IPv6`(whoisEndpoint: String) {
        withTestApplication(Application::main) {
            with(handleRequest(HttpMethod.Get, "$whoisEndpoint?queryIP=fd00::b87a:9e0b:8c74:254a&clientId=${UUID.randomUUID()}")) {
                assertTrue(requestHandled)
                assertEquals(HttpStatusCode.NotFound, response.status())
                assertNull(response.content)
            }
        }
    }

    private fun `query for an unknown IPv6 with subnet`(whoisEndpoint: String) {
        withTestApplication(Application::main) {
            with(handleRequest(HttpMethod.Get, "$whoisEndpoint?queryIP=fd00::b87a:9e0b:8c74:254a/64&clientId=${UUID.randomUUID()}")) {
                assertTrue(requestHandled)
                assertEquals(HttpStatusCode.NotFound, response.status())
                assertNull(response.content)
            }
        }
    }

    private fun `query for a malformed IP address`(whoisEndpoint: String) {
        withTestApplication(Application::main) {
            with(handleRequest(HttpMethod.Get, "$whoisEndpoint?queryIP=${UUID.randomUUID()}&clientId=${UUID.randomUUID()}")) {
                assertTrue(requestHandled)
                assertEquals(HttpStatusCode.NotFound, response.status())
                assertNull(response.content)
            }
        }
    }

    private fun `query for localhost in IPv6`(whoisEndpoint: String) {
        withTestApplication(Application::main) {
            with(handleRequest(HttpMethod.Get, "$whoisEndpoint?queryIP=::1&clientId=${UUID.randomUUID()}")) {
                assertTrue(requestHandled)
                assertEquals(HttpStatusCode.NotFound, response.status())
                assertNull(response.content)
            }
        }
    }

    private fun `query for a known IPv6`(whoisEndpoint: String, checksum: Int) {
        withTestApplication(Application::main) {
            with(handleRequest(HttpMethod.Get,
                    "$whoisEndpoint?queryIP=2001:a61:1010:7c01:b87a:9e0b:8c74:254a" +
                            "&clientId=f5c88067-88f8-4a5b-b43e-bf0e10a8b857"
            )) {
                assertTrue(requestHandled)
                assertEquals(HttpStatusCode.OK, response.status())
                assertEquals(checksum, response.content?.length)
            }
        }
    }

    private fun `query for 127_0_0_1`(whoisEndpoint: String) {
        withTestApplication(Application::main) {
            with(handleRequest(HttpMethod.Get, "$whoisEndpoint?queryIP=127.0.0.1&clientId=${UUID.randomUUID()}")) {
                assertTrue(requestHandled)
                assertEquals(HttpStatusCode.NotFound, response.status())
                assertNull(response.content)
            }
        }
    }

    private fun `query for localhost`(whoisEndpoint: String) {
        withTestApplication(Application::main) {
            with(handleRequest(HttpMethod.Get, "$whoisEndpoint?queryIP=localhost&clientId=${UUID.randomUUID()}")) {
                assertTrue(requestHandled)
                assertEquals(HttpStatusCode.NotFound, response.status())
                assertNull(response.content)
            }
        }
    }

    private fun `query without clientId`(whoisEndpoint: String) {
        withTestApplication(Application::main) {
            with(handleRequest(HttpMethod.Get, "$whoisEndpoint?queryIP=$ipAddressWithInfo")) {
                assertTrue(requestHandled)
                assertEquals(HttpStatusCode.BadRequest, response.status())
                assertTrue(response.content!!.isNotBlank())
            }
        }
    }

    private fun `simplified consumption, query without clientId`(whoisEndpoint: String) {
        withTestApplication(Application::main) {
            with(handleRequest(HttpMethod.Get, "$whoisEndpoint?queryIP=$ipAddressWithInfo")) {
                assertTrue(requestHandled)
                assertEquals(HttpStatusCode.OK, response.status())
                assertTrue(response.content!!.isNotBlank())
            }
        }
    }

    private fun `query with malformed clientId`(whoisEndpoint: String) {
        withTestApplication(Application::main) {
            with(handleRequest(HttpMethod.Get, "$whoisEndpoint?queryIP=$ipAddressWithInfo&clientId=malformed")) {
                assertTrue(requestHandled)
                assertEquals(HttpStatusCode.BadRequest, response.status())
                assertTrue(response.content!!.isNotBlank())
            }
        }
    }

    @Test
    fun lookupWhois() = withTestApplication(Application::main) {
        val whoisEndpoint = "whois"

        `provide IP implicitly in the request & 404 because it cannot be found`(whoisEndpoint)

        `provide IP in query`(whoisEndpoint, 501)
        `query for a known IPv6`(whoisEndpoint, 497)

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

        `provide IP in query`(whoisEndpoint, 1402)

        `query for localhost`(whoisEndpoint)

        `query for 127_0_0_1`(whoisEndpoint)

        `query for an unknown, short IPv6`(whoisEndpoint)

        `query for a known IPv6`(whoisEndpoint, 1074)

        `query for localhost in IPv6`(whoisEndpoint)

        `query for an unknown IPv6 with subnet`(whoisEndpoint)

        `query for a malformed IP address`(whoisEndpoint)
    }

    @Test
    fun testWhoisLookupForCountry() = withTestApplication(Application::main) {
        val whoisEndpoint = "whois/country"

        `provide IP implicitly in the request & 404 because it cannot be found`(whoisEndpoint)

        `provide IP in query`(whoisEndpoint, 932)

        `query for localhost`(whoisEndpoint)

        `query for 127_0_0_1`(whoisEndpoint)

        `query for an unknown, short IPv6`(whoisEndpoint)

        `query for a known IPv6`(whoisEndpoint, 957)

        `query for localhost in IPv6`(whoisEndpoint)

        `query for an unknown IPv6 with subnet`(whoisEndpoint)

        `query for a malformed IP address`(whoisEndpoint)
    }

    companion object {
        val LOG: Logger = LoggerFactory.getLogger(AppTest::class.java)
        const val queryIP = "88.217.181.79"
    }
}
