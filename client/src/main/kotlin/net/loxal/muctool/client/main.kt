/*
 * MUCtool Web Toolkit
 *
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

package net.loxal.muctool.client

import org.w3c.xhr.XMLHttpRequest

data class DTO(val id: String = "1")

fun main(args: Array<String>) {
    println("Kotlin powered client")

    val xhr: XMLHttpRequest = XMLHttpRequest()
//    xhr.open("GET", "https://muctool.loxal.net/robots.txt")
    xhr.open("GET", "https://muctool.loxal.net/sitemap.xml")
//    xhr.open("GET", "https://ci.loxal.net/app/rest/builds/buildType(id:Loxal_MUCtool_Build)/statusIcon")
    xhr.onload = {
        println(xhr.responseText)
    }
    xhr.send()

}