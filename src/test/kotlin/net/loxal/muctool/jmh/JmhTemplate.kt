/*
 * Copyright 2017 Alexander Orlov <alexander.orlov@loxal.net>. All rights reserved.
 */

package net.loxal.muctool.jmh

import org.openjdk.jmh.annotations.Benchmark
import org.openjdk.jmh.annotations.BenchmarkMode
import org.openjdk.jmh.annotations.Mode
import org.openjdk.jmh.annotations.Threads

//@State(Scope.Benchmark)
open class JmhTemplate {
    @BenchmarkMode(Mode.SingleShotTime)
    @Threads(2)
    @Benchmark
    fun countBeans() {
        for (i in 1..1_000) {
            println("$i. bean")
        }
    }
}