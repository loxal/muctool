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

const loadPageIntoContainer = async function () {
    const xhr = new XMLHttpRequest();
    xhr.open("GET", "main.html");
    xhr.onload = async function () {
        const pageContent = document.documentElement.innerHTML;
        document.documentElement.innerHTML = this.responseText;
        document.getElementById("main").innerHTML = pageContent;
        applySiteProperties();
    };
    xhr.send();
};

const applySiteProperties = async function () {
    const xhr = new XMLHttpRequest();
    xhr.open("GET", "stats");
    xhr.onload = async function () {
        const version = document.getElementById("version");
        const versionContainer = document.getElementById("title");
        if (version !== null && this.status === 200) {
            const stats = JSON.parse(this.responseText);
            // version.innerHTML = "b" + stats.buildNumber + "-" + stats.scmHash;
            versionContainer.setAttribute("title", "App version: b" + stats.buildNumber + "-" + stats.scmHash);
        }
    };
    xhr.send();
};

console.info("%c%s", "color: hsla(222, 99%, 44%, .9); background: #eef; font-size: 2em; font-weight: bold; border-radius: 1em;", " Don't Panic😊");

// const isServiceWorkerAvailable = function () {
//     return location.hostname.endsWith("localhost") ^ location.protocol.endsWith("https:");
// };
// if ("serviceWorker" in navigator && isServiceWorkerAvailable()) {
//     navigator.serviceWorker.register("service-worker.js", {scope: "/"})
//         .then(async function (registration) {
//             registration.update();
//         })
//         .catch(async function (error) {
//             console.warn("Registration failed with " + error);
//         });
// }