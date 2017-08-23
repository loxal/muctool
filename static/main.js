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

"use strict";

const navTo = async function () {
    const handlers = {
        "#": "whois.html",
        "": "whois.html",
        "#whois": "whois.html",
        "#test": "test.html",
        "#license": "license.html",
        "#cryptocurrency-coin-support": "cryptocurrency-coin-support.html",
        "#pricing": "pricing.html",
        "#simple-sitesearch": "simple-sitesearch.html",
        "#simple-sitesearch-gadget": "simple-sitesearch-gadget.html",
        "#tos": "tos.html",
        "#sla": "sla.html",
        "#privacy": "privacy.html",
        "#imprint": "imprint.html"
    };
    const xhr = new XMLHttpRequest();
    xhr.open("GET", handlers[location.hash]);
    xhr.onload = async function () {
        const main = document.getElementById("main");
        if (main !== null) {
            if (this.status === 200) {
                main.innerHTML = this.responseText;
                if (location.hash === "#whois" || location.hash === "") callWhois();
            } else {
                main.innerHTML = '<div class="info warn">Page Not Found</div>';
            }
        }
    };
    xhr.send();
};

const loadPageIntoContainer = async function () {
    location.hash = location.pathname.substring(1, location.pathname.lastIndexOf(".html"));
    location.pathname = "";

    // nextGenPageLoader(); // TODO activate this once moving to NG
};

const nextGenPageLoader = async function () {
    const xhr = new XMLHttpRequest();
    xhr.open("GET", "main.html");
    xhr.onload = async function () {
        const pageContent = document.documentElement.innerHTML;
        document.documentElement.innerHTML = this.responseText;
        document.getElementById("main").innerHTML = pageContent;
        if (location.pathname === "/whois.html") callWhois();
    };
    xhr.send();
};

const applySiteProperties = async function applySiteProperties() {
    const xhr = new XMLHttpRequest();
    xhr.open("GET", "stats");
    xhr.onload = async function () {
        const version = document.getElementById("version");
        if (version !== null && this.status === 200) {
            const stats = JSON.parse(this.responseText);
            version.innerHTML = "b" + stats.buildNumber + "-" + stats.scmHash;
        }
    };
    xhr.send();
};

navTo();
applySiteProperties();
console.info("%c%s", "color: hsla(222, 99%, 44%, .9); background: #eef; font-size: 2em; font-weight: bold; border-radius: 1em;", " Don't Panic😊");

const callWhois = async function () {
    const ipAddress = document.getElementById("ipAddress").value;
    let queryIP;
    if (ipAddress) {
        queryIP = "&queryIP=" + ipAddress;
    } else {
        queryIP = location.hostname === "localhost" ? "&queryIP=185.17.205.98" : "";
    }
    const xhr = new XMLHttpRequest();
    xhr.open("GET", "/whois?clientId=f5c88067-88f8-4a5b-b43e-bf0e10a8b857" + queryIP);
    xhr.onload = async function () {
        const clearPreviousWhoisView = function () {
            document.getElementById("whois").innerHTML = "";
        };
        if (this.status === 200) {
            const whoisInfo = JSON.parse(this.responseText);

            const process = async function (dlE, key, value, jsonEntryEnd) {
                const dtE = document.createElement("dt");
                dtE.style = "display:inline-flex; text-indent: 1em;";
                const ddE = document.createElement("dd");
                ddE.style = "display: inline-flex; text-indent: -2.5em;";
                dtE.textContent = '"' + key + '":';
                const showAsQueryIpAddress = function () {
                    if (key === "ip") document.getElementById("ipAddress").value = value;
                };
                showAsQueryIpAddress();
                if (typeof(value) !== "object") {
                    let ddEcontent;
                    if (typeof(value) === "string") {
                        ddEcontent = '"' + value + '"';
                    } else {
                        ddEcontent = value;
                    }
                    ddE.textContent = ddEcontent + jsonEntryEnd;
                }
                dlE.appendChild(dtE);
                dlE.appendChild(ddE);
                dlE.appendChild(document.createElement("br"));

                return ddE;
            };

            const traverse = async function (dlE, obj, process) {
                const beginContainer = document.createElement("dt");
                beginContainer.textContent = "{";
                dlE.appendChild(beginContainer);
                const objLength = Object.entries(obj).length;
                let objEntryIndex = 1;
                Object.entries(obj).forEach(([key, value]) => {
                    const parentDdE = process.apply(this, [dlE, key, value, objLength === objEntryIndex++ ? "" : ","]);
                    if (value !== null && typeof(value) === "object") {
                        const dlE = document.createElement("dl");
                        parentDdE.then(parentDdE => {
                            parentDdE.appendChild(dlE);
                            const innerPromise = traverse(dlE, value, process);
                        });
                    }
                });
                const endContainer = document.createElement("dt");
                endContainer.textContent = "}";
                dlE.appendChild(endContainer);
            };

            clearPreviousWhoisView();
            const dlWhoisContainer = document.getElementById("whois");
            const whoisContainer = document.createElement("dl");
            dlWhoisContainer.appendChild(whoisContainer);
            const promiseContainer = traverse(whoisContainer, whoisInfo, process);
        } else {
            clearPreviousWhoisView();
            document.getElementById("whois").textContent = " IP Address " + this.statusText;
        }
    };
    xhr.send();
};

const isServiceWorkerAvailable = function () {
    return location.hostname.endsWith("localhost") ^ location.protocol.endsWith("https:");
};
if ("serviceWorker" in navigator && isServiceWorkerAvailable()) {
    navigator.serviceWorker.register("service-worker.js", {scope: "/"})
        .then(async function (registration) {
            registration.update();
            // console.warn("Service Worker Registered");
        })
        .catch(async function (error) {
            console.warn("Registration failed with " + error);
        });
}