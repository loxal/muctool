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

//@file:JsQualifier("mylib.pkg2")
//@file:JsModule("Whois")
package net.loxal.muctool.whois

import org.w3c.dom.HTMLDListElement
import org.w3c.dom.HTMLDivElement
import org.w3c.dom.HTMLElement
import org.w3c.dom.HTMLInputElement
import org.w3c.dom.events.Event
import org.w3c.xhr.XMLHttpRequest
import kotlin.browser.document
import kotlin.browser.window
import kotlin.js.Json
import kotlin.js.Promise

private fun main() {      
    document.addEventListener("InitContainer", {
        Whois()
    })

    document.addEventListener("DOMContentLoaded", {
        Whois()
    })
}

class Whois {
    private fun clearPreviousWhoisView() {
        (document.getElementById("whois") as HTMLDivElement).innerHTML = ""
    }

    private val isDebugView = window.location.search.contains("debug-view")
    private fun log(msg: Any?) {
        if (isDebugView) {
            println(msg)
        }
    }

    private fun traverse(dlE: HTMLDListElement, obj: Json = JSON.parse(""), process: () -> HTMLElement) {
        fun process(dlE: HTMLDListElement, key: String, value: String, jsonEntryEnd: String): Promise<HTMLElement> {
            fun showAsQueryIpAddress(key: String, value: String) {
                if (key === "ip") {
                    ipAddressContainer.value = value
                }
            }

            val dtE = document.createElement("dt") as HTMLElement
            dtE.setAttribute("style", "display: inline-block; text-indent: 1em;")
            val ddE = document.createElement("dd") as HTMLElement
            ddE.setAttribute("style", "display: inline-block; text-indent: -2.5em;")
            dtE.textContent = "\"$key\":"
            showAsQueryIpAddress(key, value)
            if (jsTypeOf(value) !== "object") {
                val ddEcontent: String =
                    if (jsTypeOf(value) === "string") {
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

        val objEntries = js("Object.entries(obj);") as Array<Array<String>>
        log(objEntries)
        objEntries.forEachIndexed { index, entry: Array<dynamic> ->
            val parentDdE: Promise<HTMLElement> =
                process(dlE, entry[0] as String, entry[1], if (objEntries.size == index + 1) "" else ",")
            if (entry[1] !== null && jsTypeOf(entry[1]) === "object") {
                val subDl = document.createElement("dl") as HTMLDListElement
                parentDdE.then { element: HTMLElement ->
                    element.appendChild(subDl)
                    traverse(subDl, entry[1], process)
                }
            }
        }

        val endContainer = document.createElement("dd") as HTMLElement
        endContainer.setAttribute("style", "display: block; text-indent: -3.0em;")
        endContainer.textContent = "}"
        dlE.appendChild(endContainer)
    }

    private fun whoisLookup(ipAddress: String = ""): XMLHttpRequest {
        val xhr = XMLHttpRequest()
        xhr.open("GET", "$apiUrl/whois?clientId=f5c88067-88f8-4a5b-b43e-bf0e10a8b857&queryIP=$ipAddress")
        xhr.send()
        return xhr
    }

    @JsName("autoWhoisOnEntry")
    internal fun autoWhoisOnEntry() {
        whoisLookup().onload = {
            val whoisResponse: XMLHttpRequest = it.target as XMLHttpRequest

            if (whoisResponse.status.equals(200)) {
                showWhois(whoisResponse)
            } else {
                whoisCustomWithDefaultFallback()
            }
        }
    }

    private var ipAddressContainer: HTMLInputElement = document.getElementById("ipAddress") as HTMLInputElement

    @JsName("whoisCustomWithDefaultFallback")
    internal fun whoisCustomWithDefaultFallback() {
        val ipAddress = ipAddressContainer.value
        whoisLookup(ipAddress).onload = {
            val whoisIpResponse: XMLHttpRequest = it.target as XMLHttpRequest
            if (whoisIpResponse.status.equals(200)) {
                showWhois(whoisIpResponse)
            } else {
                whoisDefault()
            }
        }
    }

    private fun whoisDefault() {
        ipAddressContainer.value = demoIPv6
        ipAddressContainer.dispatchEvent(Event("change"))
        (document.getElementById("status") as HTMLDivElement).textContent =
            "Your IP address was not found. Another, known IP address was used."
    }

    private fun showWhois(whoisRequest: XMLHttpRequest) {
        val whoisInfo = JSON.parse<Json>(whoisRequest.responseText)
        clearPreviousWhoisView()
        val whoisContainer = document.createElement("dl") as HTMLDListElement
        (document.getElementById("whois") as HTMLDivElement).appendChild(whoisContainer)
        traverse(whoisContainer, whoisInfo, js("whois.net.loxal.muctool.whois.process"))
    }

    companion object Whois {
        internal const val apiUrl = "https://api.muctool.de"
        const val demoIPv6 = "2001:a61:346c:8e00:41ff:1b13:28d4:1"
    }

    init {
        ipAddressContainer.addEventListener("change", {
            whoisCustomWithDefaultFallback()
        })
        autoWhoisOnEntry()
    }
}