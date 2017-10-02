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
import org.w3c.xhr.XMLHttpRequest
import kotlin.browser.document
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

fun main(args: Array<String>) {
    loginViaGitHub()
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
    xhr.open("GET", "main.html")
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
    xhr.open("GET", "stats")
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
    xhr.open("GET", "https://localhost:1443/login/github?redirect_uri=https://localhost:1443/login/github")
    xhr.setRequestHeader("Access-Control-Allow-Origin", "*")

    xhr.onload = {
    }
    xhr.send()
}

private fun loginViaGitHub() {
    val queryParameters = URLSearchParams(document.location?.search)

    val accessToken = queryParameters.get("accessToken")
    if (accessToken != null) {
        val xhr = XMLHttpRequest()
        xhr.open("GET", "https://api.github.com/user")
        xhr.setRequestHeader("Authorization", "token $accessToken")
        xhr.onload = {
            if (xhr.status.equals(200)) {
                val user = JSON.parse<OAuth2User>(xhr.responseText)
                val login = document.getElementById("login") as HTMLAnchorElement
                login.href = "/"
                login.innerHTML = "<span class='fa fa-sign-out'></span> Logout: ${user.name}"
                login.setAttribute("title", "${user.login} - ${user.id}")
            }
        }
        xhr.send()
    }
}