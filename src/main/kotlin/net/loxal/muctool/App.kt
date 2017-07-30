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
import org.jetbrains.ktor.features.DefaultHeaders
import org.jetbrains.ktor.gson.GsonSupport
import org.jetbrains.ktor.http.ContentType
import org.jetbrains.ktor.logging.CallLogging
import org.jetbrains.ktor.request.receiveText
import org.jetbrains.ktor.response.respond
import org.jetbrains.ktor.response.respondRedirect
import org.jetbrains.ktor.response.respondText
import org.jetbrains.ktor.routing.get
import org.jetbrains.ktor.routing.post
import org.jetbrains.ktor.routing.routing
import org.jetbrains.ktor.util.generateCertificate
import org.jetbrains.ktor.util.toMap
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File
import java.net.InetAddress
import java.security.SecureRandom
import java.time.Instant
import java.util.*


data class Whois(
        val data: String,
        val method: String,
        val port: Int,
        val version: String,
        val scheme: String,
        val uri: String,
        val ip: String,
        val host: String,
        val country: String,
        val query: Map<String, List<String>> = mapOf(),
        val headers: Map<String, List<String>> = mapOf()
)

data class Randomness(
        val uuid: UUID = UUID.randomUUID(),
        val secureRandomLong: Long = SecureRandom.getInstanceStrong().nextLong(),
        val secureRandomFloat: Float = SecureRandom.getInstanceStrong().nextFloat(),
        val secureRandomGaussian: Double = SecureRandom.getInstanceStrong().nextGaussian(),
        val secureRandomInt: Int = SecureRandom.getInstanceStrong().nextInt(),
        val timestamp: Instant = Instant.now()
)

val LOG: Logger = LoggerFactory.getLogger("muctool")
val dilbertService = "http://sky.loxal.net:1181"

val dbReader: DatabaseReader =
        DatabaseReader
                .Builder(File("build/resources/main/GeoLite2-ASN.mmdb"))
                .withCache(CHMCache()).build()

fun Application.main() {
    install(GsonSupport)
    install(DefaultHeaders)
    install(CallLogging)
    routing {
        get("dilbert-quote/{path}") {
            call.respondRedirect("$dilbertService/dilbert-quote/${call.parameters["path"]}", true)
        }
        get("whois/asn") {
            val dbReader: DatabaseReader =
                    DatabaseReader
                            .Builder(File("build/resources/main/GeoLite2-ASN.mmdb"))
                            .withCache(CHMCache()).build()

            dbReader.use({ reader ->
                val ipAddress = InetAddress.getByName(call.request.local.remoteHost)
                val dbLookup = reader.asn(ipAddress)

                call.respondText(dbLookup.toJson(), ContentType.Application.Json)
            })
        }
        get("whois/city") {
            val dbReader: DatabaseReader =
                    DatabaseReader
                            .Builder(File("build/resources/main/GeoLite2-City.mmdb"))
                            .withCache(CHMCache()).build()

            dbReader.use({ reader ->
                val ipAddress = InetAddress.getByName(call.request.local.remoteHost)
                val dbLookup = reader.city(ipAddress)

                call.respondText(dbLookup.toJson(), ContentType.Application.Json)
            })
        }
        get("whois/country") {
            val dbReader: DatabaseReader =
                    DatabaseReader
                            .Builder(File("build/resources/main/GeoLite2-Country.mmdb"))
                            .withCache(CHMCache()).build()

            dbReader.use({ reader ->
                val ipAddress = InetAddress.getByName(call.request.local.remoteHost)
                val dbLookup = reader.country(ipAddress)

                call.respondText(dbLookup.toJson(), ContentType.Application.Json)
            })
        }
        get("whois") {
            call.respond(
                    Whois(
                            data = call.receiveText(),
                            method = call.request.local.method.value,
                            port = call.request.local.port,
                            version = call.request.local.version,
                            scheme = call.request.local.scheme,
                            uri = call.request.local.uri,
                            query = call.request.queryParameters.toMap(),
                            headers = call.request.headers.toMap(),
                            ip = call.request.local.remoteHost,
                            host = call.request.local.host,
                            country = "DE"
                    )
            )
        }
        post("echo") {
            call.respond(
                    Whois(
                            data = call.receiveText(),
                            method = call.request.local.method.value,
                            port = call.request.local.port,
                            version = call.request.local.version,
                            scheme = call.request.local.scheme,
                            uri = call.request.local.uri,
                            query = call.request.queryParameters.toMap(),
                            headers = call.request.headers.toMap(),
                            ip = call.request.local.remoteHost,
                            host = call.request.local.host,
                            country = "DE"
                    )
            )
        }
        get("entropy") {
            call.respondText(UUID.randomUUID().toString(), ContentType.Application.Json)
        }
        get("randomness") {
            call.respond(Randomness())
        }

        get("ephemeral-disposable-keystore.jks") {
            val file = File("build/ephemeral-disposable.jks")

            if (!file.exists()) {
                file.parentFile.mkdirs()
                generateCertificate(file)
            }
            call.respond(file.readBytes())
        }
        get("test") {
            call.respondText("Serving entropy... ${UUID.randomUUID()}", ContentType.Text.Plain)
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
            val file = File("build/ephemeral-disposable.jks")

            if (!file.exists()) {
                file.parentFile.mkdirs()
                generateCertificate(file)
            }
        }
    }
}
