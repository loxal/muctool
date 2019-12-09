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

self.addEventListener("install", event => {
    console.warn("install");
    event.waitUntil(
        caches.open("muctool")
            .then(cache => {
                // function (cache) {
                // return cache.addAll([
                //     "/",
                //     "/index.html",
                //     "/imprint.html",
                //     "/whois.html",
                //     "/pricing.html",
                //     "/cryptocurrency-coin-support.html",
                //     "/main.html"
                // ]);
                // }
            })
    );
});

self.addEventListener("activate", event => {
    console.warn("activate");
    console.warn("Service Worker is now ready to handle fetches.");
});

self.addEventListener("fetch", event => {
    // console.warn(event.request.url);

    event.respondWith(
        caches.match(event.request).then(function (response) {
            return response || fetch(event.request);
        })
    );
});

self.addEventListener("uninstall", event => {
    console.warn("uninstall");
});

self.addEventListener("unregister", event => {
    console.warn("unregister");
});

self.addEventListener("sync", event => {
    console.warn("sync");
});

self.addEventListener("push", event => {
    console.warn("push");
});

self.addEventListener("fetch", event => {
    console.warn("fetch");
});

self.addEventListener("focus", event => {
    console.warn("focus");
});

self.addEventListener("stop", event => {
    console.warn("stop");
});

self.addEventListener("start", event => {
    console.warn("start");
});