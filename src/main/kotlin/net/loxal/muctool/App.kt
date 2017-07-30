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
import org.jetbrains.ktor.gson.GsonSupport
import org.jetbrains.ktor.http.ContentType
import org.jetbrains.ktor.logging.CallLogging
import org.jetbrains.ktor.response.respond
import org.jetbrains.ktor.response.respondRedirect
import org.jetbrains.ktor.response.respondText
import org.jetbrains.ktor.routing.get
import org.jetbrains.ktor.routing.routing
import org.jetbrains.ktor.util.generateCertificate
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File
import java.net.InetAddress
import java.security.SecureRandom
import java.time.Instant
import java.util.*

data class Whois(val ip: String, val host: String, val country: String)
data class Randomness(
        val uuid: UUID = UUID.randomUUID(),
        val secureRandomLong: Long = SecureRandom.getInstanceStrong().nextLong(),
        val secureRandomFloat: Float = SecureRandom.getInstanceStrong().nextFloat(),
        val secureRandomGaussian: Double = SecureRandom.getInstanceStrong().nextGaussian(),
        val secureRandomInt: Int = SecureRandom.getInstanceStrong().nextInt(),
        val timestamp: Instant = Instant.now()
)

val LOG: Logger = LoggerFactory.getLogger(Application::class.java)

fun Application.main() {
    install(GsonSupport)
//    install(DefaultHeaders)
    install(CallLogging)
    routing {
        get("/dilbert-quote/{path}/{quoteId}") {
            // TODO introduce * placeholder to handle the entire endpoint
            call.respondRedirect("http://sky.loxal.net/dilbert-quote/${call.parameters.get("path")}")
//            call.respondRedirect("", true)
        }
        get("/dilbert/{path}") {

            LOG.info("call.request.local.host = ${call.request.local.host}")
            LOG.info("call.request.local.remoteHost = ${call.request.local.remoteHost}")
            LOG.info("call.request.local.scheme = ${call.request.local.scheme}")
            LOG.info("call.request.local.uri = ${call.request.local.uri}")
            LOG.info("call.request.local.version = ${call.request.local.version}")
            LOG.info("call.request.local.port = ${call.request.local.port}")
            LOG.info("call.request.local.method = ${call.request.local.method}")

            LOG.info("call.request.queryParameters.entries() SIZE = ${call.request.queryParameters.entries().size}")
            for (entry in call.request.queryParameters.entries()) {
                LOG.warn("queryParameter = ${entry.key}:${entry.value}")
            }

            for (entry in call.request.queryParameters.entries()) {
                LOG.warn("queryParameter = ${entry.key}:${entry.value}")
            }

            for (entry in call.request.headers.entries()) {
                LOG.warn("header = ${entry.key}:${entry.value}")
            }
//        get("/dilbert/*") {
//            LoggerFactory.getLogger(Application::class.java).warn(call.parameters["path"])
//            for (i in call.receiveParameters().entries()) {
//                LoggerFactory.getLogger(Application::class.java).warn("${i.key}:${i.value}")
//            }
            for (i in call.parameters.entries()) {
                LoggerFactory.getLogger(Application::class.java).warn("${i.key}:${i.value}")
            }

//            call.respondRedirect("http://sky.loxal.net:1080/dilbert-quote/")
//            call.respondRedirect("", true)
        }
        get("/whois") {
            call.respond(Whois(ip = "my", host = "my-host", country = "DE"))
        }
        get("/entropy") {
            call.respondText(UUID.randomUUID().toString(), ContentType.Application.Json)
        }
        get("/randomness") {
            call.respond(Randomness())
        }

        get("/test") {
            call.respondText("Netty's serving... entropy: ${UUID.randomUUID()}", ContentType.Text.Plain)
//            playAroundWithGeoIP2()
        }

        static("/") {
            staticRootFolder =
                    File(if (System.getenv("PWD") == null) System.getenv("DIRNAME") else System.getenv("PWD"))
            files("build/resources/main/static")
            default("build/resources/main/static/index.html")
        }
    }
}

class CertificateGenerator {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            val file = File("build/keystore-generated.jks")

            if (!file.exists()) {
                file.parentFile.mkdirs()
                generateCertificate(file)
            }
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
