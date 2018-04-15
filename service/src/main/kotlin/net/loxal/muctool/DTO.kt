/*
 * MUCtool Web Toolkit
 *
 * Copyright 2018 Alexander Orlov <alexander.orlov@loxal.net>. All rights reserved.
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

import java.net.InetAddress
import java.net.URL
import java.security.SecureRandom
import java.time.Instant
import java.util.*

data class Curl(val statusCode: Int = 0, val code: Int = 0, val body: String? = "", val url: URL)

data class Encoding(
        val raw: String = "",
        val charset: java.nio.charset.Charset = Charsets.UTF_8,
        val rawLength: Int = 0,
        val hash: Int = 0,
        val octal: String = "[]",
        val decimal: String = "[]",
        val hex: String = "[]",
        val md5: String = "",
        val sha1: String = "",
        val sha256: String = "",
        val base64Encoded: String = "",
        val base64Decoded: String = "",
        val urlEncoded: String = "",
        val urlDecoded: String = ""
)

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

data class Stats(
        val pageViews: Long = 0,
        val whoisPerClient: Map<UUID, Long> = mapOf(),
        val scmHash: String = "",
        val buildNumber: String = "",
        val queryCount: Long = 0
)

data class Randomness(
        val uuid: UUID = UUID.randomUUID(),
        val secureRandomLong: Long = SecureRandom.getInstanceStrong().nextLong(),
        val secureRandomFloat: Float = SecureRandom.getInstanceStrong().nextFloat(),
        val secureRandomGaussian: Double = SecureRandom.getInstanceStrong().nextGaussian(),
        val secureRandomInt: Int = SecureRandom.getInstanceStrong().nextInt(),
        val timestamp: Instant = Instant.now()
)

data class Whois(
        val ip: InetAddress,
        val city: String,
        val isp: String,
        val country: String,
        val countryIso: String,
        val postalCode: String,
        val subdivisionIso: String,
        val timeZone: String,
        val cityGeonameId: Int,
        val countryGeonameId: Int,
        val subdivisionGeonameId: Int,
        val ispId: Int,
        val latitude: Double,
        val longitude: Double,
        val fingerprint: String = "", // with IP address AND without IP address; consider pluginList(browser) and other client-specific properties
        val session: String = "", // more precise version of the fingerprint, includes IP address
        val isTor: Boolean = false,
        val fraud: Float = 0.024F // constRiskForTor * constRiskForCountry(c) * os(browser) * browser(browser) * screenResolution(browser) * pluginList(browser) * constForNonUIClients
)