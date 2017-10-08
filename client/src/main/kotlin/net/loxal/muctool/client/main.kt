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

import org.w3c.dom.*
import org.w3c.dom.url.URL
import org.w3c.dom.url.URLSearchParams
import org.w3c.workers.RegistrationOptions
import org.w3c.workers.ServiceWorkerRegistration
import org.w3c.xhr.XMLHttpRequest
import kotlin.browser.document
import kotlin.browser.localStorage
import kotlin.browser.window
import kotlin.dom.clear
import kotlin.js.Promise

data class OAuth2User(
        val id: String,
        val login: String,
        val name: String,
        val email: String,
        val company: String,
        val location: String,
        val avatar_url: String,
        val bio: String,
        val blog: String
)

fun main(args: Array<String>) {
    document.addEventListener("DOMContentLoaded", {
        console.info("%c%s", "color: hsla(222, 99%, 44%, .9); background: #eef; font-size: 2em; font-weight: bold; border-radius: 1em;", " Don't Panic😊")
    })
}

fun init() {
    console.info("%c%s", "color: hsla(222, 99%, 44%, .9); background: #eef; font-size: 2em; font-weight: bold; border-radius: 1em;", " INIT ")
    if (localStorage["accessToken"] != null) {
        fetchUser(localStorage["accessToken"])
    } else if (!URLSearchParams(document.location?.search).get("accessToken").isNullOrEmpty()) {
        val accessToken = URLSearchParams(document.location?.search).get("accessToken")!!
        store(accessToken)
        fetchUser(accessToken)
    }

//    setupServiceWorker()
}

private fun setupServiceWorker() {
    val isServiceWorkerAvailable: () -> Boolean = {
        window.location.hostname.endsWith("localhost") xor window.location.protocol.endsWith("https:")
    }
    if (isServiceWorkerAvailable()) {
        val serviceWorker = window.navigator.serviceWorker
        val options = RegistrationOptions("/")
        val registration: Promise<ServiceWorkerRegistration> = serviceWorker.register("service-worker.js", options)
        registration.then(onFulfilled = { serviceWorkerRegistration ->
            console.warn(serviceWorkerRegistration)
            serviceWorkerRegistration.update()
        }).catch {
            console.warn("Registration failed with $it")
        }
    }
}

fun validateSite() {
    val auditSite = document.getElementById("auditSite") as HTMLInputElement
    val urls: List<URL> = listOf(
            URL("${auditSite.value}/robots.txt"),
            URL("${auditSite.value}/manifest.json"),
            URL("${auditSite.value}/sitemap.xml")
    )
    val auditReport = document.getElementById("auditReport") as HTMLUListElement
    auditReport.clear()
    urls.forEach { url: URL ->
        val xhr = XMLHttpRequest()
        xhr.open("GET", url.toString())
        xhr.onload = {
            val auditEntry = document.createElement("li") as HTMLLIElement
            auditEntry.textContent = "$url - ${xhr.status}"
            auditReport.appendChild(auditEntry)
        }
        xhr.send()
    }
}

fun loadPageIntoContainer() {
    val xhr = XMLHttpRequest()
    xhr.open("GET", "/main.html")
    xhr.onload = {
        val documentElement = document.documentElement as Element
        val previousPageContent = documentElement.innerHTML
        documentElement.innerHTML = xhr.responseText
        document.getElementById("main")?.innerHTML = previousPageContent
        applySiteProperties()
    }
    xhr.send()
}

private fun applySiteProperties() {
    val xhr = XMLHttpRequest()
    xhr.open("GET", "/stats")
    xhr.onload = {
        val versionContainer = document.getElementById("title") as Element
        if (xhr.status.equals(200)) {
            val stats = JSON.parse<dynamic>(xhr.responseText)
            versionContainer.setAttribute("title", "App version: b" + stats.buildNumber + "-" + stats.scmHash)
        }
    }
    xhr.send()
}

fun loginByButton() {
    val xhr = XMLHttpRequest()
    xhr.open("GET", "https://localhost:1180/login/github?redirect_uri=https://localhost:1180/login/github")
    xhr.setRequestHeader("Access-Control-Allow-Origin", "*")

    xhr.onload = {
    }
    xhr.send()
}

fun logout() {
    if (document.getElementById("login")?.textContent!!.startsWith(" Logout")) {
        localStorage.clear()
    }
}

private fun fetchUser(accessToken: String?) {
    val xhr = XMLHttpRequest()
    xhr.open("GET", "https://api.github.com/user")
    xhr.setRequestHeader("Authorization", "token $accessToken")
    xhr.onload = {
        if (xhr.status.equals(200)) {
            val user = JSON.parse<OAuth2User>(xhr.responseText)
            val loginLink = document.getElementById("login") as HTMLAnchorElement
            loginLink.href = "/"
            loginLink.innerHTML = "<span class='fa fa-sign-out'></span> Logout: ${user.name}"
            loginLink.setAttribute("title", "${user.login} - ${user.id}")
        }
    }
    xhr.send()
}

private fun store(accessToken: String) {
    localStorage.setItem("accessToken", accessToken)
    document.location?.search = ""
}
