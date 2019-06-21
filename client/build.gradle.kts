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

val kotlinVersion = "1.3.40"
plugins {
    id("kotlin2js") version "1.3.40"
}

dependencies {
    compile("org.jetbrains.kotlin:kotlin-stdlib-js:$kotlinVersion")
    compile("org.jetbrains.kotlin:kotlin-test-js:$kotlinVersion")
}

tasks {
    val artifactPath = "../service/static/app"

    "compileKotlin2Js"(Kotlin2JsCompile::class) {
        kotlinOptions {
            metaInfo = true
            outputFile = "${buildDir}/classes/kotlin/main/${project.name}.js"
            sourceMap = true
            moduleKind = "umd"
            main = "call"
            suppressWarnings = false
            target = "v5"
            noStdlib = true
            friendModulesDisabled = false
            sourceMapEmbedSources = "always"
            typedArrays = false
        }

        doLast {
            //            val serviceBuildPath = "../service/build/resources/main/static/app"
            project.file("$artifactPath/${project.name}").delete()
//            project.file("$serviceBuildPath/${project.name}").delete()

            copy {
                from(compileKotlin2Js.get().destinationDir)
                into("$artifactPath/${project.name}")
//                into("$serviceBuildPath/${project.name}")
            }

            copy {
                from(sourceSets.main.get().resources)
                into("$artifactPath/${project.name}/resources")
//                into("$serviceBuildPath/${project.name}/resources")
            }
        }
    }

    task("includeKotlinJsRuntime") {
        configurations["compile"].files.forEach { file ->
            copy {
                from(zipTree(file.absolutePath))
                into("$artifactPath/runtime")
            }
        }
    }
}