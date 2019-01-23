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

package net.loxal.muctool

import com.fasterxml.jackson.databind.ObjectMapper
import com.maxmind.db.CHMCache
import com.maxmind.geoip2.DatabaseReader
import io.ktor.application.*
import io.ktor.features.CallLogging
import io.ktor.features.Compression
import io.ktor.features.ContentNegotiation
import io.ktor.features.DefaultHeaders
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.cio.websocket.CloseReason
import io.ktor.http.cio.websocket.Frame
import io.ktor.http.cio.websocket.close
import io.ktor.http.cio.websocket.readText
import io.ktor.http.content.default
import io.ktor.http.content.files
import io.ktor.http.content.static
import io.ktor.http.withCharset
import io.ktor.request.ApplicationRequest
import io.ktor.request.header
import io.ktor.request.receiveText
import io.ktor.response.respond
import io.ktor.response.respondRedirect
import io.ktor.response.respondText
import io.ktor.routing.delete
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.put
import io.ktor.routing.routing
import io.ktor.sessions.get
import io.ktor.sessions.sessions
import io.ktor.util.pipeline.PipelineContext
import io.ktor.util.toMap
import io.ktor.websocket.WebSockets
import io.ktor.websocket.webSocket
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File
import java.net.*
import java.net.http.HttpClient
import java.nio.charset.Charset
import java.security.MessageDigest
import java.time.Duration
import java.util.*
import java.util.concurrent.atomic.AtomicLong


private val log: Logger = LoggerFactory.getLogger(Application::class.java)
private const val resources = "src/main/resources/"
val mapper = ObjectMapper()

private val asnDBreader: DatabaseReader = DatabaseReader
    .Builder(File("${resources}GeoLite2-ASN.mmdb"))
    .withCache(CHMCache())
    .build()

private val cityDBreader: DatabaseReader = DatabaseReader
    .Builder(File("${resources}GeoLite2-City.mmdb"))
    .withCache(CHMCache())
    .build()

private val countryDBreader: DatabaseReader = DatabaseReader
    .Builder(File("${resources}GeoLite2-Country.mmdb"))
    .withCache(CHMCache())
    .build()

private val pageViews: AtomicLong = AtomicLong()
private val whoisPerClient: MutableMap<UUID, Long> = mutableMapOf()

private fun PipelineContext<Unit, ApplicationCall>.inetAddress(): InetAddress? {
    val queryIP = call.request.queryParameters["queryIP"]

    return try {
        log.info("queryIP: $queryIP")
        if (queryIP.isNullOrEmpty()) InetAddress.getByName(call.request.local.remoteHost) else InetAddress.getByName(
            queryIP
        )
    } catch (e: UnknownHostException) {
        log.info(e.message)
        null
    }
}

data class Session(val id: String = "0") {
    companion object {
        const val sessionKey = "SESSION"
    }
}

private val javaClient = java.net.http.HttpClient.newBuilder().followRedirects(HttpClient.Redirect.NEVER).build()

fun Application.main() {
    install(Compression) // delegated to nginx only or does it also make sense here?
    install(DefaultHeaders)
    install(ContentNegotiation)
    install(CallLogging)
    install(WebSockets) {
        pingPeriod = Duration.ofMinutes(1)
    }
    routing {
        //        install(Sessions) {
//            cookie<Session>(sessionKey)
//        }
//
//        intercept(ApplicationCallPipeline.Features) {
//            if (call.sessions.get<Session>() == null) {
//                call.sessions.set(sessionKey, Session(generateNonce()))
//            }
//        }

        webSocket("curl") {
            val session = call.sessions.get<Session>()
            if (session == null) {
                close(CloseReason(CloseReason.Codes.VIOLATED_POLICY, "NO_SESSION"))
                return@webSocket
            }

            try {
                for (frame in incoming) {
                    if (frame is Frame.Text) {
                        val url = frame.readText()
                        outgoing.offer(Frame.Text(url))
                        outgoing.offer(Frame.Text(1.toString()))
                        val curlString = Curl(
                            statusCode = frame.frameType.ordinal,
                            code = frame.frameType.opcode,
                            url = url
                        ).toString()
                        outgoing.send(Frame.Text(curlString))
                        log.info(curlString)
                    }
                }
            } catch (e: Exception) {
                val exceptionMessage = "Exception: ${e.message}"
                log.warn(exceptionMessage)
                outgoing.send(Frame.Text(exceptionMessage))
            } finally {
                log.error("session.id: ${session.id}")
            }
        }
        get {
            log.info("pageViews: ${pageViews.incrementAndGet()}")
        }
        get("product/download") {
            // TODO if the raw url is exposed, use Nginx routing
            call.respondRedirect("https://github.com/loxal/muctool/archive/master.zip", true) // TODO should be true
        }

        // TODO create redirect from /product to GitHub ZIP to obfuscate GitHub
        get("whois/asn") {
            val ip: InetAddress? = inetAddress()
            asnDBreader.let { reader ->
                try {
                    val dbLookup = reader.asn(ip)
                    call.respondText(dbLookup.toJson(), ContentType.Application.Json.withCharset(Charsets.UTF_8))
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.NotFound)
                }
            }
        }
        get("encoding") {
            // TODO expose via Swagger
            val value = call.request.queryParameters["value"] ?: ""
            val charset = call.request.queryParameters["charset"] ?: Charsets.UTF_8.name()

            val appliedCharset = Charset.forName(charset)
            fun decodeBase64(encoded: String): String {
                return try {
                    Base64.getDecoder().decode(encoded)
                        .toString(appliedCharset)
                } catch (e: IllegalArgumentException) {
                    log.warn(e.message)
                    ""
                }
            }

            fun encodeBase64(raw: String) = Base64.getEncoder().encodeToString(raw.toByteArray(appliedCharset))

            fun decodeUrl(encoded: String) = URLDecoder.decode(encoded, charset)
            fun encodeUrl(raw: String) = URLEncoder.encode(raw, charset)

            val rawArray = value.toByteArray(appliedCharset)
            val capacity = rawArray.size * 2 + 2
            val hex = StringBuilder(capacity)
            val octal = StringBuilder(capacity)
            octal.append("[")
            hex.append("[")
            rawArray.forEachIndexed { index, byte ->
                octal.append(byte.toString(8))
                hex.append(byte.toString(16))
                if (index < rawArray.size - 1) {
                    octal.append(", ")
                    hex.append(", ")
                }
            }
            octal.append("]")
            hex.append("]")

            val encoding = Encoding(
                raw = value,
                charset = appliedCharset,
                octal = octal.toString(),
                decimal = rawArray.contentToString(),
                hex = hex.toString(),
                md5 = ch.qos.logback.core.encoder.ByteArrayUtil.toHexString(digest(md5Digestor, rawArray)),
                sha1 = ch.qos.logback.core.encoder.ByteArrayUtil.toHexString(digest(sha1Digestor, rawArray)),
                sha256 = ch.qos.logback.core.encoder.ByteArrayUtil.toHexString(digest(sha256Digestor, rawArray)),
                hash = Objects.hash(value),
                rawLength = value.length,
                base64Encoded = encodeBase64(value),
                base64Decoded = decodeBase64(value),
                urlEncoded = encodeUrl(value),
                urlDecoded = decodeUrl(value)
            )

            call.respondText(mapper.writeValueAsString(encoding), ContentType.Application.Json)
        }
        get("whois") {
            val clientId: UUID
            try {
                val clientIdParam = call.request.queryParameters["clientId"]
                clientId = if (clientIdParam === null)
                    UUID.fromString("0-0-0-0-0")
                else
                    UUID.fromString(clientIdParam)

                log.info("clientId: $clientId")
            } catch (e: Exception) {
                call.respondText(
                    "clientId query parameter must be a valid UUID",
                    ContentType.Text.Plain,
                    HttpStatusCode.BadRequest
                )
                return@get
            }

            val ip: InetAddress? = inetAddress()
            cityDBreader.let { reader ->
                try {
                    val dbLookupMajor = reader.city(ip)

                    var isp: String
                    var ispId: Int

                    asnDBreader.let { readerAsn ->
                        val dbLookupMinor = readerAsn.asn(ip)
                        isp = dbLookupMinor.autonomousSystemOrganization
                        ispId = dbLookupMinor.autonomousSystemNumber
                    }

                    val fingerprint = takeFingerprint(call.request)

                    val whois = Whois(
                        ip = InetAddress.getByName(dbLookupMajor.traits.ipAddress),
                        country = dbLookupMajor.country.name ?: "",
                        countryIso = dbLookupMajor.country.isoCode ?: "",
                        countryGeonameId = dbLookupMajor.country.geoNameId ?: -1,
                        isp = isp,
                        ispId = ispId,
                        city = dbLookupMajor.city.name ?: "",
                        cityGeonameId = dbLookupMajor.city.geoNameId ?: -1,
                        isTor = false,
                        timeZone = dbLookupMajor.location.timeZone ?: "",
                        latitude = dbLookupMajor.location.latitude ?: -1.0,
                        longitude = dbLookupMajor.location.longitude ?: -1.0,
                        postalCode = dbLookupMajor.postal.code ?: "",
                        subdivisionGeonameId = dbLookupMajor.mostSpecificSubdivision.geoNameId ?: -1,
                        subdivisionIso = dbLookupMajor.mostSpecificSubdivision.isoCode ?: "",
                        fingerprint = fingerprint,
                        session = recordSession(fingerprint, ip, call.request.header(HttpHeaders.Cookie))
                    )

                    call.respondText(mapper.writeValueAsString(whois), ContentType.Application.Json)
                    whoisPerClient.put(clientId, whoisPerClient.getOrDefault(clientId, 0).inc())
                } catch (e: Exception) {
                    log.info(e.message)
                    call.respond(HttpStatusCode.NotFound)
                }
            }
        }

        get("whois/city") {
            val ip: InetAddress? = inetAddress()
            cityDBreader.also { reader ->
                try {
                    val dbLookup = reader.city(ip)
                    call.respondText(dbLookup.toJson(), ContentType.Application.Json.withCharset(Charsets.UTF_8))
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.NotFound)
                }
            }
        }
        get("whois/country") {
            val ip: InetAddress? = inetAddress()
            countryDBreader.also { reader ->
                try {
                    val dbLookup = reader.country(ip)
                    call.respondText(dbLookup.toJson(), ContentType.Application.Json.withCharset(Charsets.UTF_8))
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.NotFound)
                }
            }
        }
        get("curl") {
            val url = call.request.queryParameters["url"]
            val followRedirects: Boolean = !call.request.queryParameters["followRedirects"].isNullOrEmpty()

            if (url == null || url.isEmpty())
                call.respond(HttpStatusCode.BadRequest)
            else {
                val httpRequest = java.net.http.HttpRequest.newBuilder().uri(URI.create(url)).GET().build()
                try {
                    val response = javaClient.send(httpRequest, java.net.http.HttpResponse.BodyHandlers.ofString())
                    call.respondText(
                        mapper.writeValueAsString(
                            Curl(
                                code = response.statusCode(),
                                statusCode = response.statusCode(),
                                body = response.body(),
                                url = url
                            )
                        ), ContentType.Application.Json
                    )
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.NotFound)
                }
            }
        }
        get("echo") {
            call.respondText(mapper.writeValueAsString(echo()), ContentType.Application.Json)
        }
        post("echo") {
            call.respondText(mapper.writeValueAsString(echo()), ContentType.Application.Json)
        }
        put("echo") {
            call.respondText(mapper.writeValueAsString(echo()), ContentType.Application.Json)
        }
        delete("echo") {
            call.respondText(mapper.writeValueAsString(echo()), ContentType.Application.Json)
        }
        get("entropy") {
            call.respondText(UUID.randomUUID().toString(), ContentType.Application.Json)
        }
        get("randomness") {
            call.respond(Randomness())
        }
        get("test") {
            call.respondText("triggered", ContentType.Text.Plain)
        }
        get("stats") {
            // TODO protect with basic auth
            val clientId = call.request.queryParameters["clientId"] ?: "0-0-0-0-0"
            val stats = Stats(
                pageViews = pageViews.toLong(),
                whoisPerClient = whoisPerClient,
                queryCount = whoisPerClient.getOrDefault(UUID.fromString(clientId), 0),
                scmHash = System.getenv("SCM_HASH") ?: "",
                buildNumber = System.getenv("BUILD_NUMBER") ?: ""
            )

            call.respondText(mapper.writeValueAsString(stats), ContentType.Application.Json)
        }
        static("/") {
            files("static")
            default("static/whois.html")
        }
    }
}

private fun recordSession(fingerprint: String, ip: InetAddress?, cookie: String?): String {
    // could also be generated by looping through ALL headers
    return ch.qos.logback.core.encoder.ByteArrayUtil.toHexString(
        digest(
            sha256Digestor,
            (fingerprint + ip + cookie).toByteArray()
        )
    )
}

private fun takeFingerprint(request: ApplicationRequest): String {
    val requestTraits = request.header(HttpHeaders.UserAgent) +
            request.header(HttpHeaders.CacheControl) +
            request.header(HttpHeaders.Connection) +
            request.header(HttpHeaders.AcceptEncoding) +
            request.header(HttpHeaders.AcceptLanguage)

    return ch.qos.logback.core.encoder.ByteArrayUtil.toHexString(digest(sha256Digestor, requestTraits.toByteArray()))
}

private suspend fun PipelineContext<Unit, ApplicationCall>.echo(): Echo {
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

private val md5Digestor = MessageDigest.getInstance("MD5")
private val sha1Digestor = MessageDigest.getInstance("SHA1")
private val sha256Digestor = MessageDigest.getInstance("SHA-256")
private fun digest(digester: MessageDigest, data: ByteArray): ByteArray {
    digester.update(data)
    return digester.digest()
}
