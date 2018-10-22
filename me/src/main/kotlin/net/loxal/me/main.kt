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

package net.loxal.me

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
    log("main")
    init()
    window.onload = {
        log("onload window")
    }
    document.addEventListener("DOMContentLoaded", {
        log("document")
    })
    window.addEventListener("DOMContentLoaded", {
        log("window")
    })
}

class My {
    fun add(left: Int, right: Int): Int {
        return left + right
    }
    companion object {
        fun my() {
            log("test companion")
        }
    }

    object Test {
        fun my() {
            log("test object")
        }
    }
}