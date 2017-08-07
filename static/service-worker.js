/*
 * Copyright 2017 Alexander Orlov <alexander.orlov@loxal.net>. All rights reserved.
 */

self.addEventListener("install", function (event) {
    event.waitUntil(
        caches.open("muctool").then(function (cache) {
            return cache.addAll([
                "/",
                "/index.html",
                "/imprint.html",
                "/whois.html",
                "/main.js",
                "/main.html"
            ]);
        })
    );
});


self.addEventListener("fetch", function (event) {
    console.info(event.request.url);
    event.respondWith(
        caches.match(event.request).then(function (response) {
            return response || fetch(event.request);
        })
    );
});