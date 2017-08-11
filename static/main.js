/*
 * Copyright 2017 Alexander Orlov <alexander.orlov@loxal.net>. All rights reserved.
 */

"use strict";

const navTo = function (hash) {
    location.hash = hash;
    const handlerMap = {
        "": "whois.html",
        "#whois": "whois.html",
        "#pricing": "pricing.html",
        "#tos": "tos.html",
        "#sla": "sla.html",
        "#privacy": "privacy.html",
        "#imprint": "imprint.html"
    };
    if (!handlerMap[location.hash]) {
        document.getElementById("main").innerHTML = '<div class="info warn">Page Not Found</div>';
    } else {
        const xhr = new XMLHttpRequest();
        xhr.open("GET", handlerMap[location.hash]);
        xhr.onload = function () {
            document.getElementById("main").innerHTML = this.response;
            if (location.hash === "#whois" || location.hash === "") callWhois();
        };
        xhr.send();
    }
};

const loadPageIntoContainer = function () {
    console.warn("TRIGGERED, but should never appear!");
    location.hash = location.pathname.substring(1, location.pathname.lastIndexOf(".html"));
    location.pathname = "";
};

const applySiteProperties = function () {
    const xhr = new XMLHttpRequest();
    xhr.open("GET", "properties.json");
    xhr.onload = function () {
        const siteProperties = JSON.parse(this.responseText);
        document.getElementById("signature").innerHTML = siteProperties["year"] + " " + siteProperties["copyright"];
        document.getElementById("header-description").innerHTML = siteProperties["titleDesc"];
    };
    xhr.send();

    console.info("location.hash: " + location.hash);
    navTo(location.hash);
};

// TODO make all functions async-prefixed
const callWhois = function () {
    // TODO display response in case IP address is not found
    const ipAddress = document.getElementById("ipAddress").value;
    let queryIP;
    if (ipAddress) {
        queryIP = "?queryIP=" + ipAddress;
    } else {
        queryIP = (location.hostname === "localhost" ? "?queryIP=185.17.205.98" : "");
    }
    const xhr = new XMLHttpRequest();
    xhr.open("GET", "/whois/city" + queryIP);
    xhr.onload = function () {
        const whoisInfo = JSON.parse(this.response);

        function process(dlE, key, value) {
            const dtE = document.createElement("dt");
            const ddE = document.createElement("dd");
            dtE.textContent = key;
            if (typeof(value) !== "object") {
                ddE.textContent = value;
            }
            dlE.appendChild(dtE);
            dlE.appendChild(ddE);

            return ddE;
        }

        function traverse(dlE, obj, process) {
            Object.keys(obj).forEach(function (key) {
                const parentDdE = process.apply(this, [dlE, key, obj[key]]);
                if (obj[key] !== null && typeof(obj[key]) === "object") {
                    const dlE = document.createElement("dl");
                    parentDdE.appendChild(dlE);
                    traverse(dlE, obj[key], process);
                }
            })
        }

        const dlWhoisContainer = document.createElement("dl");
        traverse(dlWhoisContainer, whoisInfo, process);
        document.getElementById("main").appendChild(dlWhoisContainer);
    };
    xhr.send();
};

console.info("%c%s", "color: hsla(222, 99%, 44%, .9); background: #eef; font-size: 2em; font-weight: bold; border-radius: 1em;", " Don't Panic😊");

const isServiceWorkerAvailable = function () {
    return location.hostname.endsWith("localhost") ^ location.protocol.endsWith("https:");
};
//    https://github.com/mdn/sw-test/blob/gh-pages/app.js
if ("serviceWorker" in navigator && isServiceWorkerAvailable()) {
    navigator.serviceWorker.register("/service-worker.js", {scope: "/"})
        .then(function () {
            console.info("Service Worker Registered");
        }).catch(function (error) {
        console.warn("Registration failed with " + error);
    });
}