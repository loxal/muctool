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
import org.junit.Test;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.results.format.ResultFormatType;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Random;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@State(Scope.Benchmark)
public class LoadBenchmark {
    private static final Logger LOG = LoggerFactory.getLogger(LoadBenchmark.class);
    private static final HttpClient CLIENT = java.net.http.HttpClient.newHttpClient();
    private static final URI LOAD_TARGET = URI.create("https://api.muctool.de");
    private static final Random ENTROPY = new Random();

    public static void main(final String... args) throws RunnerException {
        final var options = new OptionsBuilder()
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
                .build();

        new Runner(options).run();
    }

    @Setup
    public void configureClient() {
    }

    @TearDown
    public void shutdownClient() {
    }

    @Benchmark
    public void whoisRandom() throws Exception {
        byte[] randomBytes = new byte[4];
        ENTROPY.nextBytes(randomBytes);
        final var randomIPaddress = Math.abs(randomBytes[0]) + "." + Math.abs(randomBytes[1]) + "." + Math.abs(randomBytes[2]) + "." + Math.abs(randomBytes[3]);
        final var response = fetchUrl(LOAD_TARGET.resolve("/whois?clientId=0-0-0-0-2&queryIP=" + randomIPaddress).toURL());
        final var body = response.body().toString();
        if (HttpStatusCode.Companion.getOK().getValue() == response.statusCode()) {
            assertTrue(response.headers().firstValue("content-type").get().startsWith("application/json;"));
            LOG.info("body.length(): " + body.length());
            assertTrue(400 < body.length());
        } else {
            assertEquals(0, body.length());
        }
    }

    @Benchmark
    public void staticFiles() throws Exception {
        final var response = fetchUrl(LOAD_TARGET.toURL());
        assertEquals(HttpStatusCode.Companion.getOK().getValue(), response.statusCode());
        final var body = response.body().toString();
        assertTrue(600 < body.length());
        assertTrue(response.headers().firstValue("content-type").get().startsWith("text/html;"));
    }

    @Test
    public void fuzz() throws Exception {
        final var fuzz = new byte[1024];
        ENTROPY.nextBytes(fuzz);
        LOG.info("fuzz: " + new String(fuzz));
        final var request = HttpRequest.newBuilder().uri(URI.create(LOAD_TARGET.toString() + "/echo"))
                //            .POST(HttpRequest.BodyPublishers.ofString("blub"))
                .header("content-type", "application/json;charset=utf-8")
                .build();
        final var response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
        LOG.info("response: " + response);
        if (response != null) {
            assertEquals(HttpStatusCode.Companion.getOK().getValue(), response.statusCode());
        }
    }

    private HttpResponse fetchUrl(final URL url) throws Exception {
        final var request = java.net.http.HttpRequest.newBuilder(url.toURI());
        return CLIENT.send(request.build(), java.net.http.HttpResponse.BodyHandlers.ofString());
    }
}

