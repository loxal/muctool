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

package net.loxal.muctool.jmh

import org.openjdk.jmh.annotations.Mode
import org.openjdk.jmh.annotations.Scope
import org.openjdk.jmh.annotations.State
import org.openjdk.jmh.runner.Runner
import org.openjdk.jmh.runner.options.OptionsBuilder
import java.util.concurrent.TimeUnit


@State(Scope.Benchmark)
open class JmhTemplate1 {
//    @Benchmark
//    @BenchmarkMode(Mode.All)
//    @Threads(2)
//    fun countBeans1M() {
//        for (i in 1..1_000_000) {
//            println("$i. bean")
//        }
//    }

//   companion object{
//        @BenchmarkMode(Mode.Throughput)
//        @Threads(2)
//        @Benchmark
//        fun countBeans() {
//            for (i in 1..1_000) {
//                println("$i. bean")
//            }
//        }


//    }


}

val jmhOptions = OptionsBuilder()
        .mode(Mode.Throughput)
        .timeUnit(TimeUnit.MILLISECONDS)
        .forks(1)
        .build()

//@Throws(RunnerException::class)
//@JvmStatic
fun main(args: Array<String>) {
//    val opt = OptionsBuilder()
//            .include(JmhTemplate1::class.java.simpleName)
////                    .include("JmhTemplate1")
//            .forks(0)
//            .build()

    Runner(jmhOptions).run()
}