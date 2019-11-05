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

import com.fasterxml.jackson.databind.ObjectMapper
import io.ktor.http.HttpStatusCode
import org.junit.Test
import org.slf4j.LoggerFactory
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpHeaders
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class ApiHealthCheck {
    @Test
    @Throws(Exception::class)
    fun redirectFromHttpNakedDomain() {
        val request = HttpRequest.newBuilder()
            .uri(URI.create("http://$domain"))
            .build()
        val response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString())
        assertEquals(HttpStatusCode.MovedPermanently.value, response.statusCode())
    }

    @Test
    @Throws(Exception::class)
    fun redirectFromUnencryptedWWW() {
        val request = HttpRequest.newBuilder()
            .uri(URI.create("http://www.$domain"))
            .build()
        val response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString())
        assertEquals(HttpStatusCode.PermanentRedirect.value, response.statusCode())
    }

    @Test
    @Throws(Exception::class)
    fun redirectFromEncryptedWWW() {
        val request = HttpRequest.newBuilder()
            .uri(URI.create("https://www.$domain"))
            .build()
        val response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString())
        assertEquals(HttpStatusCode.PermanentRedirect.value, response.statusCode())
    }

    @Test
    @Throws(Exception::class)
    fun productFrontpageContent() {
        val request = HttpRequest.newBuilder()
            .uri(URI.create("https://$domain"))
            .build()
        val response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString())
        assertEquals(HttpStatusCode.OK.value, response.statusCode())
        assertTrue(response.body().contains(productFrontpageMarker))
    }

    private fun assureCorsHeaders(headers: HttpHeaders) {
        assertEquals("true", headers.firstValue("access-control-allow-credentials").get())
    }

    @Test
    @Throws(Exception::class)
    fun apiFrontpageContent() {
        val request = HttpRequest.newBuilder()
            .uri(URI.create("https://api.$domain"))
            .build()
        val response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString())
        assertEquals(HttpStatusCode.OK.value, response.statusCode())
        assertNotNull(response.body())
        assertTrue(response.body().contains(productFrontpageMarker))

        assertEquals(HttpStatusCode.OK.value, response.statusCode())
        assureCorsHeaders(response.headers())
    }

    @Test
    @Throws(Exception::class)
    fun whois() {
        val request = HttpRequest.newBuilder()
            .uri(URI.create("https://api.$domain/whois?clientId=0-0-0-0-3&queryIP=185.17.205.98"))
            .build()

        val response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString())

        assertEquals(HttpStatusCode.OK.value, response.statusCode())
        assertNotNull(response.body())

        assureCorsHeaders(response.headers())
    }

    companion object {
        private val LOG = LoggerFactory.getLogger(ApiHealthCheck::class.java)
        private const val domain = "muctool.de"

        private const val productFrontpageMarker = "<title>GeoIP Whois</title>"

        private val MAPPER = ObjectMapper()
        private val CLIENT = HttpClient.newHttpClient()
    }
}