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

// Migrate to Kotlin https://guides.gradle.org/migrating-build-logic-from-groovy-to-kotlin/
plugins {
    idea
    id ("application")
    id("org.jetbrains.kotlin.jvm") version "1.3.40"
    id("com.github.johnrengelman.shadow") version "5.0.0"
}

group = "net.loxal.muctool"
version = "1.0.0"
description = "MUCtool Web Toolkit Goodness"

java {
    sourceCompatibility = JavaVersion.VERSION_12
    targetCompatibility = JavaVersion.VERSION_12
}

val serverEngine = "netty" // or "jetty"

application {
    mainClassName = "io.ktor.server.${serverEngine}.EngineMain"
}

repositories {
    jcenter()
}

tasks.withType<KotlinCompile>().all {
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_12.toString()
    }
}

tasks.withType<Jar> {
    manifest {
        attributes(
            mapOf(
                "Main-Class" to application.mainClassName
            )
        )
    }
}

dependencies {
    val kotlinVersion = "1.3.40"
    val ktorVersion = "1.2.2"

    compile("com.maxmind.geoip2:geoip2:2.12.0")
    compile("ch.qos.logback:logback-classic:1.2.3")

    compile("io.ktor:ktor-locations:$ktorVersion")
    compile("io.ktor:ktor-websockets:$ktorVersion")
    compile("io.ktor:ktor-server-${serverEngine}:$ktorVersion")
    compile("io.ktor:ktor-gson:$ktorVersion")
    compile("org.jetbrains.kotlin:kotlin-stdlib-jdk8:$kotlinVersion")
    compile("org.jetbrains.kotlin:kotlin-reflect:$kotlinVersion")

    testCompile("org.jetbrains.kotlin:kotlin-test-junit5:$kotlinVersion")
    testCompile("io.ktor:ktor-server-test-host:$ktorVersion")
}

task("includeKotlinJsRuntime") {
    println(this.name)
    val artifactPath = "${project(":service").projectDir}/static/app"
    project(":client").configurations["compile"].files.forEach { file ->
        copy {
            from(zipTree(file.absolutePath))
            into("$artifactPath/runtime")
        }

        copy {
            from("${project(":client").buildDir}/kotlin-js-min/main/kotlin.js")
            into("$artifactPath/stdlib")
        }
    }
}