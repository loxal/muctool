/*
 * MUCtool Web Toolkit
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

package net.loxal.muctool

import com.maxmind.db.CHMCache
import com.maxmind.geoip2.DatabaseReader
import okhttp3.OkHttpClient
import okhttp3.Request
import org.jetbrains.ktor.application.Application
import org.jetbrains.ktor.application.install
import org.jetbrains.ktor.content.default
import org.jetbrains.ktor.content.files
import org.jetbrains.ktor.content.static
import org.jetbrains.ktor.features.CallLogging
import org.jetbrains.ktor.features.Compression
import org.jetbrains.ktor.features.DefaultHeaders
import org.jetbrains.ktor.gson.GsonSupport
import org.jetbrains.ktor.http.ContentType
import org.jetbrains.ktor.http.HttpStatusCode
import org.jetbrains.ktor.http.withCharset
import org.jetbrains.ktor.locations.Locations
import org.jetbrains.ktor.pipeline.PipelineContext
import org.jetbrains.ktor.request.receiveText
import org.jetbrains.ktor.response.respond
import org.jetbrains.ktor.response.respondRedirect
import org.jetbrains.ktor.response.respondText
import org.jetbrains.ktor.routing.*
import org.jetbrains.ktor.util.toMap
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import sun.security.x509.*
import java.io.File
import java.io.IOException
import java.math.BigInteger
import java.net.InetAddress
import java.net.URI
import java.net.UnknownHostException
import java.security.KeyPairGenerator
import java.security.KeyStore
import java.security.SecureRandom
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*
import java.util.concurrent.atomic.AtomicLong


data class Echo(
        val data: String,
        val method: String,
        val version: String,
        val scheme: String,
        val uri: String,
        val ip: String,
        val host: String,
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

val LOG: Logger = LoggerFactory.getLogger(Application::class.java)
val dilbertService = "http://sky.loxal.net:1181"
private val RESOURCES = "src/main/resources/"

private val asnDBreader: DatabaseReader = DatabaseReader
        .Builder(File("${RESOURCES}GeoLite2-ASN.mmdb"))
        .withCache(CHMCache())
        .build()

private val cityDBreader: DatabaseReader = DatabaseReader
        .Builder(File("${RESOURCES}GeoLite2-City.mmdb"))
        .withCache(CHMCache())
        .build()

private val countryDBreader: DatabaseReader = DatabaseReader
        .Builder(File("${RESOURCES}GeoLite2-Country.mmdb"))
        .withCache(CHMCache())
        .build()

fun Application.main() {
    install(Locations)
    install(Compression)
    install(DefaultHeaders)
    install(GsonSupport)
//    install(CORS) {
        // breaks font-awesome, when used in plain form
//        method(HttpMethod.Options)
//        method(HttpMethod.Get)
//        header(HttpHeaders.XForwardedProto)
//        header(HttpHeaders.Referrer)
//        anyHost()
//        allowCredentials = true
//        maxAge = Duration.ofDays(1)
//    }
    install(CallLogging)
    routing {
        options("dilbert-quote/{path}") {
            LOG.info("123: 123")
            // TODO is this actually rquired? look in the logs
            call.respondRedirect("$dilbertService/dilbert-quote/${call.parameters["path"]}", true)
        }
        get("dilbert-quote/{path}") {
            call.respondRedirect("$dilbertService/dilbert-quote/${call.parameters["path"]}", true)
        }
        fun PipelineContext<Unit>.inetAddress(): InetAddress? {
            val queryIP = call.request.queryParameters["queryIP"]
            if (queryIP != null) LOG.info("queryIP: $queryIP")

            try {
                return if (queryIP == null) InetAddress.getByName(call.request.local.remoteHost) else InetAddress.getByName(queryIP)
            } catch (e: UnknownHostException) {
                LOG.info(e.message)
                return null
            }
        }

        get("whois/asn") {
            val ip: InetAddress? = inetAddress()
            asnDBreader.let({ reader ->
                try {
                    val dbLookup = reader.asn(ip)
                    call.respondText(dbLookup.toJson(), ContentType.Application.Json.withCharset(Charsets.UTF_8))
                } catch(e: Exception) {
                    call.respond(HttpStatusCode.NotFound)
                }
            })
        }
        get("whois") {
            try {
                val clientId = UUID.fromString(call.request.queryParameters["clientId"])
                LOG.info("clientId: $clientId") // simplest approach to count queries
                LOG.info("clientSecret: ${call.request.queryParameters["clientSecret"]}")
            } catch (e: Exception) {
                call.respondText(
                        "clientId query parameter must be a valid UUID",
                        ContentType.Text.Plain,
                        HttpStatusCode.BadRequest
                )
            }

            val ip: InetAddress? = inetAddress()
            cityDBreader.let({ reader ->
                try {
                    val dbLookup = reader.city(ip)

                    var isp: String = ""
                    var ispId: Int = -1

                    asnDBreader.let({ readerAsn ->
                        val dbLookupAsn = readerAsn.asn(ip)
                        isp = dbLookupAsn.autonomousSystemOrganization
                        ispId = dbLookupAsn.autonomousSystemNumber
                    })

                    val whois = Whois(
                            ip = InetAddress.getByName(dbLookup.traits.ipAddress),
                            country = dbLookup.country.name ?: "",
                            countryIso = dbLookup.country.isoCode ?: "",
                            countryGeonameId = dbLookup.country.geoNameId ?: -1,
                            isp = isp,
                            ispId = ispId,
                            city = dbLookup.city.name ?: "",
                            cityGonameId = dbLookup.city.geoNameId ?: -1,
                            isTor = false,
                            timeZone = dbLookup.location.timeZone ?: "",
                            latitude = dbLookup.location.latitude ?: -1.0,
                            longitude = dbLookup.location.longitude ?: -1.0,
                            postalCode = dbLookup.postal.code ?: "",
                            subdivisionGeonameId = dbLookup.mostSpecificSubdivision.geoNameId ?: -1,
                            subdivisionIso = dbLookup.mostSpecificSubdivision.isoCode ?: ""
                    )

                    call.respond(whois)
                } catch(e: Exception) {
                    LOG.info(e.message)
                    call.respond(HttpStatusCode.NotFound)
                }
            })
        }
        get("whois/city") {
            val ip: InetAddress? = inetAddress()
            cityDBreader.also({ reader ->
                try {
                    val dbLookup = reader.city(ip)
                    call.respondText(dbLookup.toJson(), ContentType.Application.Json.withCharset(Charsets.UTF_8))
                } catch(e: Exception) {
                    call.respond(HttpStatusCode.NotFound)
                }
            })
        }
        get("whois/country") {
            val ip: InetAddress? = inetAddress()
            countryDBreader.also({ reader ->
                try {
                    val dbLookup = reader.country(ip)
                    call.respondText(dbLookup.toJson(), ContentType.Application.Json.withCharset(Charsets.UTF_8))
                } catch(e: Exception) {
                    call.respond(HttpStatusCode.NotFound)
                }
            })
        }
        delete("echo") {
            call.respond(echo())
        }
        get("echo") {
            call.respond(echo())
        }
        put("echo") {
            call.respond(echo())
        }
        post("echo") {
            call.respond(echo())
        }
        get("entropy") {
            call.respondText(UUID.randomUUID().toString(), ContentType.Application.Json)
        }
        get("randomness") {
            call.respond(Randomness())
        }

        get("keystore.jks") {
            val file = File("build/ephemeral-disposable.jks")

            if (!file.exists()) {
                file.parentFile.mkdirs()
                CertificateGenerator.generateCertificate1(file)
            }
            call.respond(file.readBytes())
        }
        val uptimeChecks: MutableMap<UUID, TimerTask> = mutableMapOf()
        get("uptime") {
            val monitorUrl: URI = if (call.request.queryParameters.contains("url"))
                URI.create(call.request.queryParameters["url"]) else URI.create("http://example.com")

            class TestTimerTask(val monitor: URI) : TimerTask() {
                var client = OkHttpClient()

                @Throws(IOException::class)
                fun run(url: String): String {
                    val request = Request.Builder()
                            .url(url)
                            .build()

                    val response = client.newCall(request).execute()
                    LOG.info("response.code(): ${response.code()}")
                    return response.body()!!.string()
                }

                override fun run() {
                    val content: String = run("https://example.com")
//                    LOG.info("content: $content")
                    // call monitor via client
                    LOG.info("$monitor")
                    LOG.info("`{uptimeChecks}`: ${uptimeChecks.size}")
                }
            }

            val uptimeCheck = TestTimerTask(monitorUrl)

            Timer().schedule(uptimeCheck, 0, 6_000)
            uptimeChecks.put(UUID.randomUUID(), uptimeCheck)
        }
        val pageViews: AtomicLong = AtomicLong()
        get("stats") {
            call.respondText("$pageViews", ContentType.Text.Plain)
        }
        static("/") {
            LOG.info("pageViews.toInt(): ${pageViews.toInt()}")
            pageViews.getAndIncrement()
            pageViews.incrementAndGet()
            LOG.info("pageViews: ${pageViews.incrementAndGet()}")
            // TODO introduce counter here that is available under /queries-status-statistics-*stats*
            files("static")
            default("static/main.html")
        }
    }
}

private suspend fun PipelineContext<Unit>.echo(): Echo {
    return Echo(
            data = call.receiveText(),
            method = call.request.local.method.value,
            version = call.request.local.version,
            scheme = call.request.local.scheme,
            uri = call.request.local.uri,
            query = call.request.queryParameters.toMap(),
            headers = call.request.headers.toMap(),
            ip = call.request.local.remoteHost,
            host = call.request.local.host
    )
}

class CertificateGenerator {
    companion object {
        /**
         * Generates simple self-signed certificate with [keyAlias] name, private key is encrypted with [keyPassword],
         * and a JKS keystore to hold it in [file] with [jksPassword].
         *
         * Only for testing purposes: NEVER use it for production!
         *
         * A generated certificate will have 3 days validity period and 1024-bits key strength.
         * Only localhost and 127.0.0.1 domains are valid with the certificate.
         */
        fun generateCertificate1(file: File, algorithm: String = "SHA256withRSA", keyAlias: String = "alias", keyPassword: String = "changeit", jksPassword: String = keyPassword): KeyStore {
            val daysValid: Long = 30
            val jks = KeyStore.getInstance("JKS")!!
            jks.load(null, null)

            val keyPairGenerator = KeyPairGenerator.getInstance("RSA")!!
            keyPairGenerator.initialize(1024)
            val keyPair = keyPairGenerator.genKeyPair()!!

            val certInfo = X509CertInfo()
            val from = Date()
            val to = LocalDateTime.now().plusDays(daysValid).atZone(ZoneId.systemDefault())
            val certValidity = CertificateValidity(from, Date.from(to.toInstant()))

            val sn = BigInteger(64, SecureRandom())

            val owner = X500Name("cn=muctool.loxal.net, ou=MUCtool, o=loxal, c=DE")

            certInfo.set(X509CertInfo.VALIDITY, certValidity)
            certInfo.set(X509CertInfo.SERIAL_NUMBER, CertificateSerialNumber(sn))
            certInfo.set(X509CertInfo.SUBJECT, owner)
            certInfo.set(X509CertInfo.ISSUER, owner)
            certInfo.set(X509CertInfo.KEY, CertificateX509Key(keyPair.public))
            certInfo.set(X509CertInfo.VERSION, CertificateVersion(CertificateVersion.V3))
            certInfo.set(X509CertInfo.EXTENSIONS, CertificateExtensions().apply {
                set(SubjectAlternativeNameExtension.NAME, SubjectAlternativeNameExtension(GeneralNames().apply {
                    add(GeneralName(DNSName("muctool.loxal.net")))
                    add(GeneralName(IPAddressName("94.130.32.101")))
                }))
            })

            var algo = AlgorithmId(AlgorithmId.sha1WithRSAEncryption_oid)
            certInfo.set(X509CertInfo.ALGORITHM_ID, CertificateAlgorithmId(algo))

            var cert = X509CertImpl(certInfo)
            cert.sign(keyPair.private, algorithm)

            algo = cert.get(X509CertImpl.SIG_ALG) as AlgorithmId
            certInfo.set(CertificateAlgorithmId.NAME + "." + CertificateAlgorithmId.ALGORITHM, algo)
            certInfo.set("version", CertificateVersion(2))

            cert = X509CertImpl(certInfo)
            cert.sign(keyPair.private, algorithm)

            jks.setCertificateEntry(keyAlias, cert)
            jks.setKeyEntry(keyAlias, keyPair.private, keyPassword.toCharArray(), arrayOf(cert))

            file.parentFile.mkdirs()
            file.outputStream().use {
                jks.store(it, jksPassword.toCharArray())
            }

            return jks
        }

        @JvmStatic
        fun main(args: Array<String>) {
            val file = File("build/ephemeral-disposable.jks")

            if (!file.exists()) {
                file.parentFile.mkdirs()
                generateCertificate1(file)
            }
        }
    }
}
