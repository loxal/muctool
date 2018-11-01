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

package net.loxal.muctool.jmh

import io.ktor.http.HttpStatusCode
import net.loxal.muctool.OkHttpBenchmarkClient
import okhttp3.Headers
import okhttp3.MediaType
import okhttp3.RequestBody
import okhttp3.Response
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.openjdk.jmh.annotations.*
import org.openjdk.jmh.results.format.ResultFormatType
import org.openjdk.jmh.runner.Runner
import org.openjdk.jmh.runner.RunnerException
import org.openjdk.jmh.runner.options.OptionsBuilder
import org.slf4j.LoggerFactory
import java.io.IOException
import java.net.URI
import java.net.URL
import java.util.*

@State(Scope.Benchmark)
class LoadBenchmark {

    @Setup
    fun configureClient() {
        CLIENT.setup()
    }

    @TearDown
    fun shutdownClient() {
        CLIENT.shutdown()
    }

    @Benchmark
    @Throws(IOException::class)
    fun whoisRandom() {
        val randomBytes = ByteArray(4)
        ENTROPY.nextBytes(randomBytes)
        val randomIPaddress =
            Math.abs(randomBytes[0].toInt()).toString() + "." + Math.abs(randomBytes[1].toInt()) + "." + Math.abs(
                randomBytes[2].toInt()
            ) + "." + Math.abs(randomBytes[3].toInt())
        val response = fetchUrl(LOAD_TARGET.resolve("/whois?clientId=0-0-0-0-2&queryIP=$randomIPaddress").toURL())
        val body = response!!.body()?.string()
        if (HttpStatusCode.OK.value == response.code()) {
            assertTrue(response.body()?.contentType().toString().startsWith("application/json;"))
            LOG.info("body!!.length: " + body!!.length)
            assertTrue(400 < body.length)
        } else {
            assertEquals(0, body!!.length)
        }
    }

    @Benchmark
    @Throws(IOException::class)
    fun staticFiles() {
        val response = fetchUrl(LOAD_TARGET.toURL())
        assertEquals(HttpStatusCode.OK.value, response!!.code())
        val body = response.body()?.string()
        assertTrue(600 < body!!.length)
        assertTrue(response.body()?.contentType().toString().startsWith("text/html;"))
    }

    @Test
    fun fuzz() {
        val fuzz = ByteArray(1024)
        ENTROPY.nextBytes(fuzz)
        LOG.info("fuzz: " + String(fuzz))
        val response = CLIENT.load(
            LOAD_TARGET.toString() + "/echo",
            Headers.of(),
            RequestBody.create(MediaType.parse("application/json;charset=utf-8"), "blub"),
            "POST"
        )
        LOG.info("response: " + response!!)
        assertEquals(HttpStatusCode.OK.value, response.code())
    }

    private fun fetchUrl(url: URL): Response? {
        val response = CLIENT.load(url)
        return response
    }

    companion object {
        private val LOG = LoggerFactory.getLogger(LoadBenchmark::class.java)
        private val CLIENT = OkHttpBenchmarkClient()
        private val LOAD_TARGET = URI.create("https://api.muctool.de")

        @Throws(RunnerException::class)
        @JvmStatic
        fun main(args: Array<String>) {
            val options = OptionsBuilder()
                //                .timeout(TimeValue.seconds(13))
                .include(".*")
                .warmupIterations(1)
                .measurementIterations(20)
                .forks(1)
                .threads(50)
                .mode(Mode.Throughput)
                .resultFormat(ResultFormatType.JSON)
                .result("build/jmh-result.json")
                .shouldFailOnError(true)
                .build()

            Runner(options).run()
        }

        private val ENTROPY = Random()
    }
}

