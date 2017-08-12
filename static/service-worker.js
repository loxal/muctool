/*
 * MUCtool Web Toolkit
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

self.addEventListener("install", function (event) {
    event.waitUntil(
        caches.open("muctool").then(function (cache) {
            return cache.addAll([
                "/",
                "/index.html",
                "/imprint.html",
                "/whois.html",
                "/pricing.html",
                "/cryptocurrency-coin-support.html",
                "/main.js",
                "/main.html"
            ]);
        })
    );
});

self.addEventListener("fetch", function (event) {
    // console.warn(event.request.url);
    event.respondWith(
        caches.match(event.request).then(function (response) {
            return response || fetch(event.request);
        })
    );
});