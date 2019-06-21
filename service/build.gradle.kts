/*
 * MUCtool Web Toolkit
 *
 * Copyright 2019 Alexander Orlov <alexander.orlov@loxal.net>. All rights reserved.
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

import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

//buildscript {
//    ext {
//        ktor_version = "1.2.2"
//    }
//}
// Migrate to Kotlin https://guides.gradle.org/migrating-build-logic-from-groovy-to-kotlin/
plugins {
    idea
    id ("application")
    id("org.jetbrains.kotlin.jvm") version "1.3.40"
    id("com.github.johnrengelman.shadow") version "5.0.0"
}

//group = "net.loxal.muctool"
//version = "1.0.0"
//description = "MUCtool Web Toolkit Goodness"

java {
    sourceCompatibility = JavaVersion.VERSION_12
    targetCompatibility = JavaVersion.VERSION_12
}

val serverEngine = "netty" // or "jetty"
//val mainClassName = "io.ktor.server.${serverEngine}.DevelopmentEngine"

application {
    mainClassName = "io.ktor.server.${serverEngine}.EngineMain"
//    mainClassName = "io.ktor.server.${serverEngine}.DevelopmentEngine"
}

repositories {
    jcenter()
}

tasks.withType<KotlinCompile>().all {
    //tasks.withType<KotlinCompile> {
//tasks {
//    "compileKotlin"(KotlinCompile::class) {
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_12.toString()
    }
}
//}
//tasks.withType<Test> {
//    kotlinOptions{
//        jvmTarget = JavaVersion.VERSION_12
//    }
//}

tasks.withType<Jar> {
    manifest {
        attributes(
            mapOf(
                "Main-Class" to application.mainClassName
            )
        )
    }
}

//tasks{
//    task ("singleJar") {
//tasks.named<Jar>("jar") {
//    //    doFirst {
////        from("src/main/resources/static/app") // TODO instead copy from this module's build directory?
////        into("static/app")
////    }
////    doLast {
////        from("static") // TODO instead copy from this module's build directory?
////        into("src/main/resources/static")
////    }
//    manifest {
//        attributes(
//            mutableMapOf("Main-Class" to application.mainClassName)
//        )
//    }
//
////    from {
////
////    }
//
////        from { configurations.compile.collect { it.isDirectory() ? it : zipTree(it) } } with sun.tools.jar.resources.jar
//}
////}

dependencies {
    val kotlin_version = "1.3.40"
    val ktor_version = "1.2.2"

    compile("com.maxmind.geoip2:geoip2:2.12.0")
    compile("ch.qos.logback:logback-classic:1.2.3")

    compile("io.ktor:ktor-locations:$ktor_version")
    compile("io.ktor:ktor-websockets:$ktor_version")
    compile("io.ktor:ktor-server-${serverEngine}:$ktor_version")
    compile("io.ktor:ktor-gson:$ktor_version")
    compile("org.jetbrains.kotlin:kotlin-stdlib-jdk8:$kotlin_version")
    compile("org.jetbrains.kotlin:kotlin-reflect:$kotlin_version")

    testCompile("org.jetbrains.kotlin:kotlin-test-junit5:$kotlin_version")
    testCompile("io.ktor:ktor-server-test-host:$ktor_version")
}
