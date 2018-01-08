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

package net.loxal.waves

import org.w3c.dom.HTMLLabelElement
import org.w3c.dom.HTMLOptionElement
import org.w3c.dom.HTMLSelectElement
import org.w3c.dom.get
import org.w3c.dom.url.URL
import org.w3c.xhr.XMLHttpRequest
import kotlin.browser.document
import kotlin.browser.window

private fun log(msg: Any?) {
    val debugView = window.location.search.contains("debug-view")
    if (debugView) {
        println(msg)
    }
}

private fun init() {
    log("init")
}

private fun main(args: Array<String>) {
    console.warn(args)
    console.info("%c%s", "color: hsla(222, 99%, 44%, .9); background: #eef; font-size: 2em; font-weight: bold; border-radius: 1em;", " Don't Panic😊")
    log("main")
    window.onload = {
        log("onload window")
    }
    document.addEventListener("DOMContentLoaded", {
        log("document")
        Waves().fetchBalance()
        Waves().assets()
        Waves().height()
        Waves().alias("alex")
    })
    window.addEventListener("DOMContentLoaded", {
        Waves.Companion.mainnetAddress = obtainMetaValue("mainnet-address")!!
        Waves.Companion.testnetAddress = obtainMetaValue("testnet-address")!!
    })
}

private val waves = Waves()
fun my() {
    waves.fetchBalance()
    console.warn(Waves.mainnetAddress)
    console.warn(Waves.testnetAddress)
}

class Waves {
    private val blockHeight = document.getElementById("block-height") as HTMLLabelElement
    private val network = document.getElementById("network") as HTMLSelectElement
    private val networkSelected = network.selectedOptions[0] as HTMLOptionElement
    private val wavesAPI = URL(networkSelected.value)

    internal fun height() {
        val xhr = XMLHttpRequest()
        xhr.open("GET", "${wavesAPI}blocks/height")
        xhr.send()
        xhr.onload = {
            console.warn(xhr.response)
            val result: dynamic = JSON.parse(xhr.responseText)
            blockHeight.textContent = result.height
            ""
        }
    }

    fun fetchBalance() {
        console.warn("selectedIndex: ${network.selectedIndex}")
        console.warn("OPTION_NAME ${network.selectedOptions[0]?.textContent}")
        console.warn("SELECT_NAME: ${network.name}")
        console.warn("OPTION_VALUE ${network.value}")
        val xhr = XMLHttpRequest()
        xhr.open("GET", "${wavesAPI}addresses/balance/3P7qtv5Z7AMhwyvf5sM6nLuWWypyjVKb7Us")
//        xhr.open("GET", "${wavesAPI}addresses/balance/3NCJg865jMNDJE6PBYWGQkUw4hvzejUzbk4")
        xhr.onload = {
            console.warn(xhr.response)
        }
        xhr.send()
    }

    internal fun alias(name: String) {
        val xhr = XMLHttpRequest()
        xhr.open("GET", "${wavesAPI}alias/by-alias/$name")
        xhr.send()
        xhr.onload = {
            console.warn(xhr.response)
        }
    }

    internal fun assets() {
        val xhr = XMLHttpRequest()

        xhr.open("GET", "${wavesAPI}assets/balance/3P7qtv5Z7AMhwyvf5sM6nLuWWypyjVKb7Us")
        xhr.onload = {
            console.warn(xhr.response)
        }
        xhr.send()
    }

    companion object {
        fun test() {
            log("test companion")
        }

        lateinit var mainnetAddress: String
        lateinit var testnetAddress: String
    }

    object Test {
        fun test() {
            log("test object")
        }
    }
}

private fun obtainMetaValue(metaName: String) =
        document.head?.getElementsByTagName("meta")?.get(metaName)?.getAttribute("content")