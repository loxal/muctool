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
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.results.format.ResultFormatType;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

//@Threads(115)
@Threads(15)
@BenchmarkMode(Mode.Throughput)
@State(Scope.Benchmark)
public class JmhTemplateLoadTest {
    private static final Logger LOG = LoggerFactory.getLogger(JmhTemplateLoadTest.class);
    private static final OkHttpBenchmarkClient CLIENT = new OkHttpBenchmarkClient();

    public static void main(String... args) throws RunnerException {
        System.getProperties().forEach((key, value) -> {
            System.out.println("key: " + key);
            System.out.println("value: " + value);
        });
        Options opt = new OptionsBuilder()
                .include(JmhTemplateLoadTest.class.getSimpleName())
                .warmupIterations(0)
                .measurementIterations(1)
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
        fetchUrl("https://muctool.loxal.net/whois");
    }

    @Benchmark
    public void staticFiles() throws IOException {
        fetchUrl("https://muctool.loxal.net");
    }

    private void fetchUrl(final String url) throws IOException {
        final InputStream load = CLIENT.load(url);
        byte[] response = new byte[8192];
        while (load.read(response) != -1) {
        }
        load.close();
        LOG.info(new String(response));
    }
}

