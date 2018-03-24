/*
 * MUCtool Web Toolkit
 *
 * Copyright 2018 Alexander Orlov <alexander.orlov@loxal.net>. All rights reserved.
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
import okhttp3.Headers
import okhttp3.OkHttpClient
import okhttp3.Request
import org.slf4j.LoggerFactory
import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ApiHealthCheck {
    @Test
    @Throws(Exception::class)
    fun redirectFromHttpNakedDomain() {
        val request = Request.Builder()
                .url("http://$domain")
                .headers(Headers.of(CORS_TRIGGERING_REQUEST_HEADER))
                .build()
        val response = HTTP_CLIENT.newCall(request).execute()
        assertEquals(HttpStatusCode.MovedPermanently.value.toLong(), response.code().toLong())
    }

    @Test
    @Throws(Exception::class)
    fun redirectFromUnencryptedWWW() {
        val request = Request.Builder()
                .url("http://www.$domain")
                .headers(Headers.of(CORS_TRIGGERING_REQUEST_HEADER))
                .build()
        val response = HTTP_CLIENT.newCall(request).execute()
        assertEquals(HttpStatusCode.MovedPermanently.value.toLong(), response.code().toLong())
    }

    @Test
    @Throws(Exception::class)
    fun redirectFromWWW() {
        val request = Request.Builder()
                .url("https://www.$domain")
                .headers(Headers.of(CORS_TRIGGERING_REQUEST_HEADER))
                .build()
        val response = HTTP_CLIENT.newCall(request).execute()
        assertEquals(HttpStatusCode.OK.value.toLong(), response.code().toLong())
        assertTrue(response.body()!!.string().contains(productFrontpageMarker))
    }

    @Test
    @Throws(Exception::class)
    fun redirectFromHttpApiDomain() {
        val request = Request.Builder()
                .url("http://api.$domain")
                .headers(Headers.of(CORS_TRIGGERING_REQUEST_HEADER))
                .build()
        val response = HTTP_CLIENT.newCall(request).execute()
        assertEquals(HttpStatusCode.MovedPermanently.value.toLong(), response.code().toLong())
    }

    @Test
    @Throws(Exception::class)
    fun productFrontpageContent() {
        val request = Request.Builder()
                .url("https://$domain")
                .headers(Headers.of(CORS_TRIGGERING_REQUEST_HEADER))
                .build()
        val response = HTTP_CLIENT.newCall(request).execute()
        assertEquals(HttpStatusCode.OK.value.toLong(), response.code().toLong())
        assertTrue(response.body()?.string()?.contains(productFrontpageMarker)!!)
    }

    private fun assureCorsHeaders(headers: Headers, byteCount: Int) {
        assertEquals(byteCount.toLong(), headers.byteCount())
        assertEquals("https://example.com", headers.get("access-control-allow-origin"))
        assertEquals("true", headers.get("access-control-allow-credentials"))
    }

    @Test
    @Throws(Exception::class)
    fun apiFrontpageContent() {
        val request = Request.Builder()
                .url("https://api.$domain")
                .headers(Headers.of(CORS_TRIGGERING_REQUEST_HEADER))
                .build()
        val response = HTTP_CLIENT.newCall(request).execute()
        assertEquals(HttpStatusCode.OK.value.toLong(), response.code().toLong())
        assertNotNull(response.body())
        assertTrue(response.body()!!.string().contains(productFrontpageMarker))

        assertEquals(HttpStatusCode.OK.value.toLong(), response.code().toLong())
        assertNull(response.headers().get("x-frame-options"))
        assertNull(response.headers().get("X-Frame-Options"))
        assureCorsHeaders(response.headers(), 241)
    }

    @Test
    @Throws(Exception::class)
    fun whois() {
        val request = Request.Builder()
                .url("https://api.$domain/whois?clientId=0-0-0-0-3&queryIP=185.17.205.98")
                .headers(Headers.of(CORS_TRIGGERING_REQUEST_HEADER))
                .build()
        val response = HTTP_CLIENT.newCall(request).execute()

        assertEquals(HttpStatusCode.OK.value.toLong(), response.code().toLong())
        assertNotNull(response.body())

        assureCorsHeaders(response.headers(), 248)
    }

    companion object {
        private val LOG = LoggerFactory.getLogger(ApiHealthCheck::class.java)
        private val domain = "muctool.de"

        private val productFrontpageMarker = "<title>GeoIP Whois</title>"

        private val MAPPER = ObjectMapper()
        private val HTTP_CLIENT = OkHttpClient.Builder()
                .followRedirects(false)
                .followSslRedirects(false)
                .build()
        private val CORS_TRIGGERING_REQUEST_HEADER = object : HashMap<String, String>() {
            init {
                put("origin", "https://example.com")
            }
        }
    }
}