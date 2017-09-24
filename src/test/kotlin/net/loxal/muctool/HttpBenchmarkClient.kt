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

import okhttp3.*
import java.net.URL
import java.util.concurrent.TimeUnit

interface HttpBenchmarkClient {
    fun setup()
    fun shutdown()
    fun load(url: URL): Response?
    fun load(url: String, headers: Headers, body: RequestBody, method: String): Response?
}

class OkHttpBenchmarkClient : HttpBenchmarkClient {
    private var httpClient: OkHttpClient? = null
    override fun setup() {
        val timeout = 130L
        httpClient = OkHttpClient.Builder()
                .connectTimeout(timeout, TimeUnit.SECONDS)
                .readTimeout(timeout, TimeUnit.SECONDS)
                .writeTimeout(timeout, TimeUnit.SECONDS)
                .retryOnConnectionFailure(true)
                .connectionPool(ConnectionPool(1_000_000, timeout, TimeUnit.SECONDS))
                .build()
    }

    override fun shutdown() {
        httpClient = null
    }

    override fun load(url: URL): Response? {
        val request = Request.Builder()
                .url(url)
                .build()
        val response = httpClient?.newCall(request)?.execute()
        return response
    }

    override fun load(url: String, headers: Headers, body: RequestBody, method: String): Response? {
        val request = Request.Builder()
                .url(url)
                .headers(headers)
                .method(method, body)
                .build()
        val response = httpClient?.newCall(request)?.execute()
        return response
    }
}
