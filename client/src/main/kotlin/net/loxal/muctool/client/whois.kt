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

private fun showAsQueryIpAddress(key: String, value: String) {
    if (key == "ip") {
        (document.getElementById("ipAddress") as HTMLInputElement).value = value
    }
}

@JsName("process")
fun process(dlE: HTMLDListElement, key: String, value: String, jsonEntryEnd: String): Promise<HTMLElement> {
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

@JsName("traverse")
fun traverse(dlE: HTMLDListElement, obj: Json, process: () -> HTMLElement) {
    val beginContainer = document.createElement("dt") as HTMLElement
    beginContainer.textContent = "{"
    dlE.appendChild(beginContainer)

    val objEntries = js("Object.entries(obj);")
    val objLength = objEntries.length
    objEntries.forEach { entry: Array<dynamic>, index: Int ->
        val parentDdE: Promise<HTMLElement> = process(dlE, entry[0], entry[1], if (objLength.equals(index + 1)) "" else ",")
        if (entry[1] !== null && jsTypeOf(entry[1]) === "object") {
            val subDl = document.createElement("dl") as HTMLDListElement
            parentDdE.then({ element: HTMLElement ->
                element.appendChild(subDl)
                traverse(subDl, entry[1], process)
            })
        }
    }
}

fun whois() {
    val ipAddressContainer = document.getElementById("ipAddress") as HTMLInputElement
    val ipAddress = ipAddressContainer.value
    val queryIP = "&queryIP=$ipAddress"
    val xhr = XMLHttpRequest()
    xhr.open("GET", "/whois?clientId=f5c88067-88f8-4a5b-b43e-bf0e10a8b857$queryIP")
    xhr.onload = {
        if (xhr.status.equals(200)) {
            val whoisInfo = JSON.parse<Json>(xhr.responseText)
            clearPreviousWhoisView()
            val whoisContainer = document.createElement("dl") as HTMLDListElement
            (document.getElementById("whois") as HTMLDivElement).appendChild(whoisContainer)
            val promiseContainer = traverse(whoisContainer, whoisInfo, js("client.net.loxal.muctool.client.process"))
        } else {
            clearPreviousWhoisView()
            ipAddressContainer.value = "185.17.205.98"
            ipAddressContainer.dispatchEvent(Event("change"))
            (document.getElementById("status") as HTMLDivElement).textContent =
                    // TODO show IP address that was not found anyway, for user's info
                    "Your IP address was not found. Another, known IP address was used."
        }
    }
    xhr.send()
}