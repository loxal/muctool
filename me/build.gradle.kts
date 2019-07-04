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

import org.jetbrains.kotlin.gradle.tasks.Kotlin2JsCompile
import org.jetbrains.kotlin.gradle.tasks.KotlinJsDce

plugins {
    id("kotlin2js") version "1.3.41"
}

apply {
    plugin("kotlin-dce-js")
}

dependencies {
    val kotlinVersion = "1.3.41"

    compile("org.jetbrains.kotlin:kotlin-stdlib-js:$kotlinVersion")
    compile("org.jetbrains.kotlin:kotlin-test-js:$kotlinVersion")
}

tasks {
    val artifactPath = "${project(":service").projectDir}/static/app" // the only module specific property
    project.file("$artifactPath/${project.name}").delete()

    "compileKotlin2Js"(Kotlin2JsCompile::class) {
        kotlinOptions {
            outputFile = "$artifactPath/${project.name}/${project.name}.js"
            sourceMap = true
            sourceMapEmbedSources = "always"
            moduleKind = "umd"
            noStdlib = true
        }
        doLast {
            copy {
                from(sourceSets.main.get().resources)
                into("$artifactPath/${project.name}/resources")
            }
        }
    }

    "runDceKotlinJs"(KotlinJsDce::class) {
        keep("client.net.loxal.muctool.client.autoWhoisOnEntry")
        dceOptions.devMode = false
        dceOptions.outputDirectory = "$artifactPath/${project.name}/min"
    }
}