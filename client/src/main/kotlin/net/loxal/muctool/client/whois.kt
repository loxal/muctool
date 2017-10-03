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
import org.w3c.dom.HTMLElement
import org.w3c.dom.HTMLInputElement
import kotlin.browser.document

fun clearPreviousWhoisView() {
    document.getElementById("whois")?.innerHTML = ""
}

@JsName("showAsQueryIpAddress")
fun showAsQueryIpAddress(key: String, value: String) {
    if (key == "ip") {
        (document.getElementById("ipAddress") as HTMLInputElement).value = value
    }
}

@JsName("process")
fun process(dlE: HTMLDListElement, key: String, value: String, jsonEntryEnd: String): HTMLElement {
    val dtE = document.createElement("dt") as HTMLElement
    dtE.setAttribute("style", "display:inline-flex; text-indent: 1em;")
    val ddE = document.createElement("dd") as HTMLElement
    ddE.setAttribute("style", "display: inline-flex; text-indent: -2.5em;")
    dtE.textContent = "\"$key\":"
    showAsQueryIpAddress(key, value)
    if (jsTypeOf(value) != "object") {
        val ddEcontent: String =
                if (jsTypeOf(value) == "string") {
                    "\"$value\""
                } else {
                    value
                }
        ddE.textContent = ddEcontent + jsonEntryEnd
    }

    dlE.appendChild(dtE)
    dlE.appendChild(ddE)
    dlE.appendChild(document.createElement("br"))

    return ddE
}

//const process = async function (dlE, key, value, jsonEntryEnd) {
//    const dtE = document.createElement("dt");
//    dtE.style = "display:inline-flex; text-indent: 1em;";
//    const ddE = document.createElement("dd");
//    ddE.style = "display: inline-flex; text-indent: -2.5em;";
//    dtE.textContent = '"' + key + '":';
//    const showAsQueryIpAddress = function () {
//        if (key === "ip") document.getElementById("ipAddress").value = value;
//    };
//    showAsQueryIpAddress();
//    if (typeof(value) !== "object") {
//        let ddEcontent;
//        if (typeof(value) === "string") {
//            ddEcontent = '"' + value + '"';
//        } else {
//            ddEcontent = value;
//        }
//        ddE.textContent = ddEcontent + jsonEntryEnd;
//    }
//    dlE.appendChild(dtE);
//    dlE.appendChild(ddE);
//    dlE.appendChild(document.createElement("br"));
//
//    return ddE;
//};

//fun traverse(dlE: dynamic, obj: dynamic, process: dynamic){
//    val beginContainer = document.createElement("dt")
//    beginContainer.textContent = "{"
//    dlE.appendChild(beginContainer)
//    val objLength = obj.length
//    var objEntryIndex = 1
//    for (b in obj) {
//        b.key
//    }
//    obj.forEach(key, value -> {
//        val parentDdE = process.apply(this, [dlE, key, value, objLength == objEntryIndex++ ? "" : ","]);
//        if (value !== null && typeof(value) === "object") {
//            val dlE = document.createElement("dl");
//            parentDdE.then(parentDdE -> {
//                parentDdE.appendChild(dlE);
//                val innerPromise = traverse(dlE, value, process);
//            })
//        }
//    })
//        val endContainer = document.createElement("dt")
//        endContainer.textContent = "}"
//        dlE.appendChild(endContainer)
//}


//const traverse = async function (dlE, obj, process) {
//    const beginContainer = document.createElement("dt");
//    beginContainer.textContent = "{";
//    dlE.appendChild(beginContainer);
//    const objLength = Object.entries(obj).length;
//    let objEntryIndex = 1;
//    Object.entries(obj).forEach(([key, value]) => {
//        const parentDdE = process.apply(this, [dlE, key, value, objLength === objEntryIndex++ ? "" : ","]);
//        if (value !== null && typeof(value) === "object") {
//            const dlE = document.createElement("dl");
//            parentDdE.then(parentDdE => {
//                parentDdE.appendChild(dlE);
//                const innerPromise = traverse(dlE, value, process);
//            });
//        }
//    });
//    const endContainer = document.createElement("dt");
//    endContainer.textContent = "}";
//    dlE.appendChild(endContainer);
//};