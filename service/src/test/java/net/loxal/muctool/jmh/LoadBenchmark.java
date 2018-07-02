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

package net.loxal.muctool.jmh;

import io.ktor.http.HttpStatusCode;
import net.loxal.muctool.OkHttpBenchmarkClient;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.junit.Test;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;
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

@State(Scope.Benchmark)
public class LoadBenchmark {
    private static final Logger LOG = LoggerFactory.getLogger(LoadBenchmark.class);
    private static final OkHttpBenchmarkClient CLIENT = new OkHttpBenchmarkClient();
    private static final URI LOAD_TARGET = URI.create("https://api.muctool.de");

    public static void main(String... args) throws RunnerException {
        Options options = new OptionsBuilder()
//                .timeout(TimeValue.seconds(13))
                .include(".*")
                .warmupIterations(1)
                .measurementIterations(20)
                .forks(0)
                .threads(400)
                .mode(Mode.Throughput)
                .resultFormat(ResultFormatType.JSON)
                .result("build/jmh-result.json")
                .shouldFailOnError(true)
                .build();

        new Runner(options).run();
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

    @Benchmark
    public void whoisRandom() throws IOException {
        byte[] randomBytes = new byte[4];
        ENTROPY.nextBytes(randomBytes);
        final String randomIPaddress = Math.abs(randomBytes[0]) + "." + Math.abs(randomBytes[1]) + "." + Math.abs(randomBytes[2]) + "." + Math.abs(randomBytes[3]);
        final Response response = fetchUrl(LOAD_TARGET.resolve("/whois?clientId=0-0-0-0-2&queryIP=" + randomIPaddress).toURL());
        final String body = response.body().string();
        if (HttpStatusCode.Companion.getOK().getValue() == response.code()) {
            assertTrue(response.body().contentType().toString().startsWith("application/json;"));
            LOG.info("body.length(): " + body.length());
            assertTrue(400 < body.length());
        } else {
            assertEquals(0, body.length());
        }
    }

    @Benchmark
    public void staticFiles() throws IOException {
        final Response response = fetchUrl(LOAD_TARGET.toURL());
        assertEquals(HttpStatusCode.Companion.getOK().getValue(), response.code());
        final String body = response.body().string();
        assertTrue(600 < body.length());
        assertTrue(response.body().contentType().toString().startsWith("text/html;"));
    }

    @Test
    public void fuzz() {
        final byte[] fuzz = new byte[1024];
        ENTROPY.nextBytes(fuzz);
        LOG.info("fuzz: " + new String(fuzz));
        final Response response = CLIENT.load(LOAD_TARGET.toString() + "/echo", Headers.of(), RequestBody.create(MediaType.parse("application/json;charset=utf-8"), "blub"), "POST");
        LOG.info("response: " + response);
        if (response != null) {
            assertEquals(HttpStatusCode.Companion.getOK().getValue(), response.code());
        }
    }

    private Response fetchUrl(final URL url) {
        final Response response = CLIENT.load(url);
        return response;
    }
}

