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

import okhttp3.ConnectionPool
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.net.URL
import java.util.concurrent.TimeUnit

interface HttpBenchmarkClient {
    fun setup()
    fun shutdown()
    fun load(url: URL): Response
}

class OkHttpBenchmarkClient : HttpBenchmarkClient {
    private var httpClient: OkHttpClient? = null
    override fun setup() {
//        httpClient = OkHttpClient()
        httpClient = OkHttpClient.Builder()
                .connectTimeout(5, TimeUnit.MINUTES)
                .readTimeout(5, TimeUnit.MINUTES)
                .writeTimeout(5, TimeUnit.MINUTES)
                .retryOnConnectionFailure(true)
                .connectionPool(ConnectionPool(1_000_000, 5, TimeUnit.MINUTES))
                .build()
    }

    override fun shutdown() {
        httpClient = null
    }

    override fun load(url: URL): Response {
        val request = Request.Builder().url(url).build()
        val response = httpClient!!.newCall(request).execute()
        return response
    }
}
