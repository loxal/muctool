/*
 * Copyright 2017 Alexander Orlov <alexander.orlov@loxal.net>. All rights reserved.
 */

package net.loxal.muctool

import com.maxmind.db.CHMCache
import com.maxmind.geoip2.DatabaseReader
import org.jetbrains.ktor.application.Application
import org.jetbrains.ktor.application.install
import org.jetbrains.ktor.content.default
import org.jetbrains.ktor.content.files
import org.jetbrains.ktor.content.static
import org.jetbrains.ktor.content.staticRootFolder
import org.jetbrains.ktor.http.ContentType
import org.jetbrains.ktor.logging.CallLogging
import org.jetbrains.ktor.response.respond
import org.jetbrains.ktor.response.respondText
import org.jetbrains.ktor.routing.get
import org.jetbrains.ktor.routing.routing
import java.io.File
import java.net.InetAddress
import java.util.*


fun Application.main() {
//    install(DefaultHeaders)
    install(CallLogging)
    routing {
        get("/entropy") {
            call.respondText(UUID.randomUUID().toString())
        }
        get("/randomness") {
            call.respond(UUID.randomUUID())
        }

        get("/test") {
            call.respondText("Netty's serving... entropy: ${UUID.randomUUID()}", ContentType.Text.Plain)

//            var t: String = ""
//            for (mutableEntry in System.getenv()) {
//                t += "${mutableEntry.key}:${mutableEntry.value}\n"
//            }
//            call.respondText(t)

//            playAroundWithGeoIP2()
        }

        static("/") {
            staticRootFolder =
                    File(if (System.getenv("PWD") == null) System.getenv("DIRNAME") else System.getenv("PWD"))
            files("build/resources/main/static")
            default("index.html")
        }
    }
}

private fun playAroundWithGeoIP2() {
    // https://github.com/maxmind/GeoIP2-java
    val reader: DatabaseReader = DatabaseReader.Builder(File("src/main/resources/GeoLite2-ASN.mmdb")).withCache(CHMCache()).build()

//            val ipAddress = InetAddress.getByName("85.25.43.84")
//            val response = reader.city(ipAddress)
//
//            val country = response.country
//            println(country.isoCode)            // 'US'
//            println(country.name)               // 'United States'
//            println(country.names["zh-CN"]) // '美国'
//
//            val subdivision = response.mostSpecificSubdivision
//            println(subdivision.name)    // 'Minnesota'
//            println(subdivision.isoCode) // 'MN'
//
//            val city = response.city
//            println(city.name) // 'Minneapolis'
//
//            val postal = response.postal
//            println(postal.code) // '55455'
//
//            val location = response.location
//            System.out.println(location.latitude)  // 44.9733
//            System.out.println(location.longitude) // -93.2323


    //////
    val ipAddress1 = InetAddress.getByName("128.101.101.101")

    val response1 = reader.asn(ipAddress1)

    println(response1.autonomousSystemNumber)       // 217
    println(response1.autonomousSystemOrganization) // 'University of Minnesota'

    ///
}
