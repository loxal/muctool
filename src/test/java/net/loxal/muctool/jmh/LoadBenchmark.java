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

package net.loxal.muctool.jmh;

import net.loxal.muctool.OkHttpBenchmarkClient;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.jetbrains.ktor.http.HttpStatusCode;
import org.junit.Test;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.results.format.ResultFormatType;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.Random;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@Threads(200)
@BenchmarkMode(Mode.Throughput)
@State(Scope.Benchmark)
public class LoadBenchmark {
    private static final Logger LOG = LoggerFactory.getLogger(LoadBenchmark.class);
    private static final OkHttpBenchmarkClient CLIENT = new OkHttpBenchmarkClient();
    private static final URI LOAD_TARGET = URI.create("https://muctool.loxal.net");

    public static void main(String... args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(LoadBenchmark.class.getSimpleName())
                .warmupIterations(1)
                .measurementIterations(20)
                .forks(1)
                .resultFormat(ResultFormatType.JSON)
                .result("build/jmh-result.json")
                .shouldFailOnError(true)
                .build();

        new Runner(opt).run();
    }

    @Setup
    public void configureClient() {
        CLIENT.setup();
    }

    @TearDown
    public void shutdownClient() {
        CLIENT.shutdown();
    }

    private static final Random ENTROPY = new Random();
    //    private static final MediaType JSON = MediaType.parse("application/json; charset=UTF-8");
    private static final MediaType JSON = MediaType.parse("application/json;charset=utf-8");
    //    private static final MediaType HTML = MediaType.parse("text/html; charset=UTF-8");
    private static final MediaType HTML = MediaType.parse("text/html;charset=utf-8");

    @Benchmark
    public void whois() throws IOException {
        final Response response = fetchUrl(LOAD_TARGET.resolve("/whois").toURL());
        assertEquals(HttpStatusCode.Companion.getOK().getValue(), response.code());
        final String body = response.body().string();
        LOG.info("body.length(): " + body.length());
        assertTrue(250 < body.length());
        assertEquals(JSON, response.body().contentType());
    }

    @Benchmark
    public void whoisRandom() throws IOException {
        byte[] randomBytes = new byte[4];
        ENTROPY.nextBytes(randomBytes);
        final String randomIPaddress = Math.abs(randomBytes[0]) + "." + Math.abs(randomBytes[1]) + "." + Math.abs(randomBytes[2]) + "." + Math.abs(randomBytes[3]);
        final Response response = fetchUrl(LOAD_TARGET.resolve("/whois?clientId=0-0-0-0-2&queryIP=" + randomIPaddress).toURL());
        final String body = response.body().string();
        if (HttpStatusCode.Companion.getOK().getValue() == response.code()) {
            assertEquals(JSON, response.body().contentType());
            LOG.info("body.length(): " + body.length());
            assertTrue(250 < body.length());
        } else {
            assertEquals(0, body.length());
        }
    }

    @Benchmark
    public void staticFiles() throws IOException {
        final Response response = fetchUrl(LOAD_TARGET.toURL());
        assertEquals(HttpStatusCode.Companion.getOK().getValue(), response.code());
        final String body = response.body().string();
        assertEquals(-1693106498, body.hashCode());
        assertEquals(HTML, response.body().contentType());
    }

    @Test
    public void fuzz() throws Exception {
        byte[] fuzz = new byte[1024];
        ENTROPY.nextBytes(fuzz);
        LOG.info("fuzz: " + new String(fuzz));
        final Response response = CLIENT.load(LOAD_TARGET.toString() + "/echo", Headers.of(), RequestBody.create(MediaType.parse("application/json;charset=utf-8"), "blub"), "POST");
        LOG.info("response: " + response);
        if (response != null) {
            assertEquals(HttpStatusCode.Companion.getOK().getValue(), response.code());
        }
    }

    private Response fetchUrl(final URL url) throws IOException {
        final Response response = CLIENT.load(url);
        return response;
    }
}

