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
import okhttp3.MediaType;
import okhttp3.Response;
import org.jetbrains.ktor.http.HttpStatusCode;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.results.format.ResultFormatType;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@Threads(100)
//@Threads(15)
@BenchmarkMode(Mode.AverageTime)
@State(Scope.Benchmark)
public class JmhTemplateAverageTime {
    private static final Logger LOG = LoggerFactory.getLogger(JmhTemplateAverageTime.class);
    private static final OkHttpBenchmarkClient CLIENT = new OkHttpBenchmarkClient();
    private static final MediaType JSON = MediaType.parse("application/json;charset=utf-8");
    private static final Random ENTROPY = new Random();

    public static void main(String... args) throws RunnerException {
        System.getProperties().forEach((key, value) -> {
            System.out.println("key: " + key);
            System.out.println("value: " + value);
        });
        Options opt = new OptionsBuilder()
                .include(JmhTemplateAverageTime.class.getSimpleName())
                .warmupIterations(1)
                .measurementIterations(20)
                .forks(1)
                .resultFormat(ResultFormatType.JSON)
                .result("build/jmhResult.json")
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

    @Benchmark
    public void base() throws InterruptedException {
        TimeUnit.MILLISECONDS.sleep(1000);
    }

    @Benchmark
    public void whois() throws IOException {
        final Response response = fetchUrl("https://muctool.loxal.net/whois");
        assertEquals(HttpStatusCode.Companion.getOK().getValue(), response.code());
        final String body = response.body().string();
        assertTrue(250 < body.length());
        assertEquals(JSON, response.body().contentType());
    }

    @Benchmark
    public void whoisRandom() throws IOException {
        byte[] randomBytes = new byte[4];
        ENTROPY.nextBytes(randomBytes);
        final String randomIPaddress = Math.abs(randomBytes[0]) + "." + Math.abs(randomBytes[1]) + "." + Math.abs(randomBytes[2]) + "." + Math.abs(randomBytes[3]);
        final Response response = fetchUrl("https://muctool.loxal.net/whois?clientId=0-0-0-0-2&queryIP=" + randomIPaddress);
        final String body = response.body().string();
        if (HttpStatusCode.Companion.getOK().getValue() == response.code()) {
            assertEquals(JSON, response.body().contentType());
            assertTrue(250 < body.length());
        } else {
            assertEquals(0, body.length());
        }
    }

    @Benchmark
    public void staticFiles() throws IOException {
        final Response response = fetchUrl("https://muctool.loxal.net");
        assertEquals(HttpStatusCode.Companion.getOK().getValue(), response.code());
        final String body = response.body().string();
        assertEquals(-1693106498, body.hashCode());
        assertEquals(MediaType.parse("text/html;charset=utf-8"), response.body().contentType());
    }

    private Response fetchUrl(final String url) throws IOException {
        final Response response = CLIENT.load(url);
        return response;
    }
}

