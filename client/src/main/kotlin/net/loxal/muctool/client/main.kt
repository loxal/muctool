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

package net.loxal.muctool.client

import org.w3c.dom.*
import org.w3c.dom.url.URL
import org.w3c.workers.ServiceWorkerRegistration
import org.w3c.xhr.XMLHttpRequest
import kotlin.browser.document
import kotlin.browser.localStorage
import kotlin.browser.window
import kotlin.dom.clear

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

private fun main() {
    document.addEventListener("DOMContentLoaded", {
        console.info("%c%s", "color: hsla(222, 99%, 44%, .9); background: #eef; font-size: 2em; font-weight: bold; border-radius: 1em;", " Don't Panic😊")
        init()
    })
}

private fun init() {
    if (localStorage["accessToken"] != null) {
        fetchUser(localStorage["accessToken"])
    } else if (window.location.search.indexOf("accessToken=") != -1) { // fragile because of Edge-safe implementation, use URLSearchParams once Edge supports them
        val accessToken = window.location.search.substring(window.location.search.indexOf("=") + 1) // once again, Edge proofs, it's an oddball
        console.warn(accessToken)
        store(accessToken)
        fetchUser(accessToken)
    }

    if (!window.location.search.contains("standalone")) {
        loadPageIntoContainer()
    }
    setupServiceWorker()
}

private fun setupServiceWorker() {
    val isServiceWorkerAvailable: () -> Boolean = {
        window.location.hostname.endsWith("localhost") xor window.location.protocol.endsWith("https:")
    }
    if (isServiceWorkerAvailable()) {
        val serviceWorker = window.navigator.serviceWorker
        serviceWorker.ready.then(onFulfilled = { serviceWorkerRegistration: ServiceWorkerRegistration ->
            console.warn(serviceWorkerRegistration.scope)
            console.warn(serviceWorkerRegistration)
            serviceWorkerRegistration.update()
        }).catch { throwable: Throwable ->
            console.warn("Registration failed with ${throwable.message}")
            console.warn("Registration failed with $throwable")
        }
//        val registration: Promise<ServiceWorkerRegistration> = serviceWorker.register("service-worker.js", options)
//        registration.then(onFulfilled = { serviceWorkerRegistration ->
//            console.warn(serviceWorkerRegistration)
//            serviceWorkerRegistration.update()
//        }).catch {
//            console.warn("Registration failed with $it")
//        }
    }
}

private val DEBUG = window.location.search.contains("debug-view")
private fun log(msg: Any?) {
    if (DEBUG) {
        println(msg)
    }
}

@JsName("validateSite")
private fun validateSite() {
    validateHtml()
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
            auditEntry.title = "$url - ${xhr.status}"
            auditEntry.textContent = if (xhr.status.equals(200)) "Found: $url" else "Missing: $url"
            auditReport.appendChild(auditEntry)
        }
        xhr.send()
    }
}

private fun validateHtml() {
    val auditSite = document.getElementById("auditSite") as HTMLInputElement
    val auditReport = document.getElementById("auditReport") as HTMLUListElement
    auditReport.clear()
    val xhr = XMLHttpRequest()
    val htmlValidatorUrl = "https://validator.w3.org/nu/?doc=${auditSite.value}"
    xhr.open("GET", htmlValidatorUrl)
    xhr.send()
    xhr.onload = {
        if (xhr.status.equals(200)) {
            val isUtf8Html = xhr.responseText.contains("Used the HTML parser. Externally specified character encoding was UTF-8.")
            val isValidHtml = xhr.responseText.contains("<p class=\"success\">")
            val auditEntry = document.createElement("li") as HTMLLIElement
            auditEntry.title = "$htmlValidatorUrl - ${xhr.status} - Parsed as UTF-8 HTML: $isUtf8Html - Valit HTML: $isValidHtml"
            auditEntry.textContent = if (isUtf8Html && isValidHtml) "Valid HTML" else "Invalid HTML"
            auditReport.appendChild(auditEntry)
        }
    }
}

private fun loadPageIntoContainer() {
    val pageContainer = "/main.html"
    if (window.location.pathname != pageContainer) {
        val xhr = XMLHttpRequest()
        xhr.open("GET", pageContainer)
        xhr.onload = {
            initContainer(xhr.responseText)
        }
        xhr.send()
    }
}

private fun initContainer(containerText: String) {
    val documentElement = document.documentElement as Element
    val previousPageContent = documentElement.innerHTML
    documentElement.innerHTML = containerText
    document.getElementById("main")?.innerHTML = previousPageContent
    applySiteProperties()

    val base = document.getElementsByTagName("base")[0] as HTMLBaseElement
    if (window.location.origin.contains("localhost"))
        base.href = window.location.origin
    else
        base.href = MUCtool.baseUrl
}

private fun applySiteProperties() {
    val xhr = XMLHttpRequest()
    xhr.open("GET", "${MUCtool.serviceApi}/stats")
    xhr.onload = {
        val versionContainer = document.getElementById("title") as HTMLElement
        if (xhr.status.equals(200)) {
            val stats = JSON.parse<dynamic>(xhr.responseText)
            versionContainer.title = "App version: b" + stats.buildNumber + "-" + stats.scmHash
        }
    }
    xhr.send()
}

@JsName("loginByButton")
private fun loginByButton() {
    val xhr = XMLHttpRequest()
    xhr.open("GET", "https://localhost:1180/login/github?redirect_uri=https://localhost:1180/login/github")
    xhr.setRequestHeader("Access-Control-Allow-Origin", "*")

    xhr.onload = {
    }
    xhr.send()
}

@JsName("logout")
private fun logout() {
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
            loginLink.innerHTML = "<span class=\"fa fa-sign-out\"></span> Logout: ${user.name}"
            loginLink.title = "${user.login} - ${user.id}"
        }
    }
    xhr.send()
}

private fun store(accessToken: String) {
    localStorage.setItem("accessToken", accessToken)
    window.location.search = ""
}

class MUCtool {
    companion object {
        const val baseUrl = "https://muctool.de"
        const val serviceApi = "https://api.muctool.de"
    }
}
