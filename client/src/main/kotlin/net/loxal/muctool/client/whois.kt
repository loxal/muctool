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

package net.loxal.muctool.client

import org.w3c.dom.HTMLDListElement
import org.w3c.dom.HTMLDivElement
import org.w3c.dom.HTMLElement
import org.w3c.dom.HTMLInputElement
import org.w3c.dom.events.Event
import org.w3c.xhr.XMLHttpRequest
import kotlin.browser.document
import kotlin.js.*

private fun clearPreviousWhoisView() {
    document.getElementById("whois")?.innerHTML = ""
}

private fun traverse(dlE: HTMLDListElement, obj: Json = JSON.parse(""), process: () -> HTMLElement) {
    fun process(dlE: HTMLDListElement, key: String, value: String, jsonEntryEnd: String): Promise<HTMLElement> {
        fun showAsQueryIpAddress(key: String, value: String) {
            if (key == "ip") {
                (document.getElementById("ipAddress") as HTMLInputElement).value = value
            }
        }

        val dtE = document.createElement("dt") as HTMLElement
        dtE.setAttribute("style", "display: inline-block; text-indent: 1em;")
        val ddE = document.createElement("dd") as HTMLElement
        ddE.setAttribute("style", "display: inline-block; text-indent: -2.5em;")
        dtE.textContent = "\"$key\":"
        showAsQueryIpAddress(key, value)
        if (jsTypeOf(value) != "object") {
            val ddEcontent: String =
                    if (jsTypeOf(value) == "string") {
                        "\"$value\""
                    } else {
                        value
                    }
            ddE.textContent = "$ddEcontent$jsonEntryEnd"
        }

        dlE.appendChild(dtE)
        dlE.appendChild(ddE)
        val blockBreak = document.createElement("dd") as HTMLElement
        blockBreak.setAttribute("style", "display: block;")
        dlE.appendChild(blockBreak)

        return Promise.resolve(ddE)
    }

    val beginContainer = document.createElement("dt") as HTMLElement
    beginContainer.textContent = "{"
    dlE.appendChild(beginContainer)

    val objEntries = js("Object.entries(obj);") as Array<Array<dynamic>>
    objEntries.forEachIndexed { index, entry: Array<dynamic> ->
        val parentDdE: Promise<HTMLElement> =
            process(dlE, entry[0], entry[1], if (objEntries.size == index + 1) "" else ",")
        if (entry[1] !== null && jsTypeOf(entry[1]) === "object") {
            val subDl = document.createElement("dl") as HTMLDListElement
            parentDdE.then({ element: HTMLElement ->
                element.appendChild(subDl)
                traverse(subDl, entry[1], process)
            })
        }
    }

    val endContainer = document.createElement("dd") as HTMLElement
    endContainer.setAttribute("style", "display: block; text-indent: -3.0em;")
    endContainer.textContent = "}"
    dlE.appendChild(endContainer)
}

private const val apiUrl = "https://api.muctool.de"
fun whois() {
    val ipAddressContainer = document.getElementById("ipAddress") as HTMLInputElement
    val ipAddress = ipAddressContainer.value
    val queryIP = "&queryIP=$ipAddress"
    val xhr = XMLHttpRequest()
    xhr.open("GET", "$apiUrl/whois?clientId=f5c88067-88f8-4a5b-b43e-bf0e10a8b857$queryIP")
    xhr.onload = {
        if (xhr.status.equals(200)) {
            val whoisInfo = JSON.parse<Json>(xhr.responseText)
            clearPreviousWhoisView()
            val whoisContainer = document.createElement("dl") as HTMLDListElement
            (document.getElementById("whois") as HTMLDivElement).appendChild(whoisContainer)
            traverse(whoisContainer, whoisInfo, js("client.net.loxal.muctool.client.process"))
        } else {
            clearPreviousWhoisView()
            ipAddressContainer.value = Whois.demoIPv6
            ipAddressContainer.dispatchEvent(Event("change"))
            (document.getElementById("status") as HTMLDivElement).textContent =
                    // TODO show IP address that was not found anyway, for user's info
                    "Your IP address was not found. Another, known IP address was used."
        }
    }
    xhr.send()
}

object Whois {
    const val demoIPv6 = "2001:a61:346c:8e00:41ff:1b13:28d4:4236"
}