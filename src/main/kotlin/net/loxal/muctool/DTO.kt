/*
 * Copyright 2017 Alexander Orlov <alexander.orlov@loxal.net>. All rights reserved.
 */

package net.loxal.muctool

import java.net.InetAddress

// TODO use this DTO to replace implicit default whois DTO inside library
data class Whois(
        val ip: InetAddress,
        //        val host: String,
        val city: String,
        val cityGonameId: Int,
        val country: String,
        val countryISO: String,
        val countryGeonameId: Int,
        val subdivisionGeonameId: Int,
        val subdivisionISO: String,
        val ispId: Int,
        val isp: String,
        val latitude: Double,
        val longitude: Double,
        val timeZone: String,
        val postalCode: String,
        val isTor: Boolean = false,
        val fraud: Float = 0.024f
)