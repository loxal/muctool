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

/*
 * Copyright 2017 Alexander Orlov <alexander.orlov@loxal.net>. All rights reserved.
 */

data class DTO(val id: String = "1")

fun main(args: Array<String>) {
    println("frontend")
    js("console.log('frontend js');")

    val xhr: XMLHttpRequest = XMLHttpRequest()
    xhr.open("GET", "https://sitesearch.cloud")
//    xhr.open("GET", "https://google.com")
//    xhr.open("GET", "http://localhost:1180")
//    xhr.open("GET", "https://example.com/robots.txt")
//    xhr.open("GET", "https://example.com/sitemaps.xml")
//    xhr.onload = {event: Event ->
//        println(event)
//    }
    xhr.onreadystatechange = {
        println(xhr.readyState)
        println(xhr.status)
//           println(xhr.responseText)
    }
    xhr.send()

}