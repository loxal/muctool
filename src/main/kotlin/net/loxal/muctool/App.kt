/*
 * Copyright 2017 Alexander Orlov <alexander.orlov@loxal.net>. All rights reserved.
 */

package net.loxal.muctool

import org.jetbrains.ktor.application.Application
import org.jetbrains.ktor.application.install
import org.jetbrains.ktor.content.files
import org.jetbrains.ktor.content.static
import org.jetbrains.ktor.features.DefaultHeaders
import org.jetbrains.ktor.http.ContentType
import org.jetbrains.ktor.logging.CallLogging
import org.jetbrains.ktor.response.respondText
import org.jetbrains.ktor.routing.get
import org.jetbrains.ktor.routing.routing

fun Application.main() {
    install(DefaultHeaders)
    install(CallLogging)
    routing {
        //    install(Routing) {
        get("/") {
            call.respondText("Netty's serving...", ContentType.Text.Plain)
        }

        static("/") {
            files("src/main/resources/static")
        }
    }
}
