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

package net.loxal.finder.client

import org.w3c.dom.*
import org.w3c.dom.events.Event
import org.w3c.dom.events.KeyboardEvent
import org.w3c.xhr.XMLHttpRequest
import kotlin.browser.document
import kotlin.browser.window
import kotlin.dom.clear

private data class Finding(
    val title: String = "",
    val body: String = "",
    val url: String = "",
    val urlRaw: String = "",
    val sisLabels: List<String> = listOf()
)

private data class Findings(val query: String, val results: Array<Finding> = arrayOf())

private val debugView = document.createElement("dl") as HTMLDListElement
private val selfTestTrigger = document.createElement("button") as HTMLButtonElement

private fun selfTest() {
    debugView.clear()
    validateServiceCall("search")
    validateServiceCall("autocomplete")
}

private fun validateServiceCall(apiEndpoint: String) {
    val xhr = XMLHttpRequest()
    xhr.open("GET", "$finderService/$siteId/$apiEndpoint?query=sites")
    xhr.onload = {
        val dt = document.createElement("dt") as HTMLElement
        debugView.appendChild(dt)
        val dd = document.createElement("dd")
        if (xhr.status.equals(200) && JSON.parse<dynamic>(xhr.responseText).results.length > 0) {
            dt.style.color = "#191"
            dt.textContent = "PASSED: $apiEndpoint"
        } else {
            dt.style.color = "#911"
            dt.textContent = "FAILED: $apiEndpoint"
        }
        dd.textContent = "Status: ${xhr.status} | Response Length: ${xhr.responseText.length}"
        debugView.appendChild(dd)
    }
    xhr.send()
}

private fun init() {
    log("init")
    buildPageFinder()
    injectPageFinderIntoWebsite()

    if (isDebugView) {
        selfTestTrigger.addEventListener("click", { selfTest() })
        selfTestTrigger.innerText = "Self Test"
        selfTestTrigger.style.display = "block"
        pageFinderContainer.appendChild(selfTestTrigger)
        pageFinderContainer.appendChild(debugView)
    }
}

private fun injectPageFinderIntoWebsite() {
    val hiddenBehindFlag: String? = pageFinderInit.getAttribute("data-hidden-behind-query-flag")

    if (hiddenBehindFlag.isNullOrBlank()) {
        showPageFinder()
    } else if (window.location.search.contains(hiddenBehindFlag)) {
        showPageFinder()
    }
}

private fun showPageFinder() {
    val style = document.createElement("style") as HTMLStyleElement
    style.innerText = ".if-teaser-highlight {font-weight: bold;}"
    finder.appendChild(style)

    findingsContainer.style.cssText =
        "box-shadow: 0 2px 2px 0 gray, 0 1px 4px 0 gray, 0 3px 1px -2px gray; " +
                "letter-spacing: .02em; " +
                "min-height: 50px; max-height: 400px;" +
                "margin-top: 2px; overflow-y: auto; overflow-x: hidden;" +
                "width: ${finder.getAttribute("width")?.toInt()!! - 9}px;" +
                "padding-left: 8px;"

    findingsContainer.style.background = "#fff"
    findingsContainer.style.zIndex = "99"
    findingsContainer.style.position = "relative"

    pageFinderContainer.id = "sitesearch-page-finder"
    encapsulateAsComponent()

    pageFinderContainer.appendChild(finder)
    pageFinderContainer.appendChild(findingsContainer)
    findingsContainer.style.display = "none"
}

private val pageFinderComponent = document.createElement("page-finder") as HTMLElement
private fun encapsulateAsComponent() {
    val parentContainerId: String? = pageFinderInit.getAttribute("data-append-as-child-to")
    val parent: Element? =
        if (parentContainerId.isNullOrBlank()) {
            pageFinderInit.parentElement
        } else {
            val parentContainer = document.querySelector(parentContainerId)
            log("Inserted into: ${parentContainer?.outerHTML}")
            parentContainer ?: pageFinderInit.parentElement
        }

    window.onload = {
        resetInheritedStyleProperties(pageFinderComponent)
        pageFinderComponent.appendChild(pageFinderContainer)
        parent?.attachShadow(ShadowRootInit(ShadowRootMode.OPEN))
            ?.appendChild(pageFinderComponent)
        document.dispatchEvent(Event("page-finder-loaded"))
    }
}

private val pageFinderContainer = document.createElement("div") as HTMLDivElement
private val findingsContainer = document.createElement("dl") as HTMLDListElement
private val finderService = "https://finder.muctool.de/sites"
//private val finderService = "${window.location.origin}/sites"
private val pageFinderInit = document.currentScript as HTMLScriptElement
private val siteId = pageFinderInit.getAttribute("data-siteId")
private val finderEndpoint = "search"
private val autocompleteEndpoint = "autocomplete"
private val finder = document.createElement("input") as HTMLInputElement
private fun buildPageFinder() {
    val finderStyle = pageFinderInit.getAttribute("data-search-style")
    finder.type = "search"
    finder.spellcheck = true
    finder.title = "Finder"
    finder.placeholder = "Find..."
    finder.style.cssText =
        if (finderStyle.isNullOrBlank())
            "width: 500px; font-size: 2em; text-indent: .5em;"
        else
            finderStyle
    finder.width = finder.style.width.substringBeforeLast("px").toInt()
}

private val isDebugView = window.location.search.contains("debug-view")
private fun log(msg: Any?) {
    if (isDebugView) {
        println(msg)
    }
}

private fun main() {
    init()
    document.addEventListener("DOMContentLoaded", {
        log("DOMContentLoaded")
    })

    log("Query: ${finder.value}")
    finder.addEventListener("change", {
        log("change")
        log(finder.value)
    })
    finder.addEventListener("keydown", { event: Event ->
        debugView.remove()
        val keyboardEvent = event as KeyboardEvent
        if (keyboardEvent.key.equals("Enter")) {
            if (finder.value.isBlank()) {
                findingsContainer.style.display = "none"
            } else {
                search()
            }
        } else {
            autocomplete()
        }
    })
}

private fun autocomplete() {
    val req = XMLHttpRequest()
    req.open("GET", "$finderService/$siteId/$autocompleteEndpoint?query=${finder.value}")
    req.onload = {
        findingsContainer.clear()
        if (req.status.equals(200)) {
            val suggestions = JSON.parse<dynamic>(req.responseText)
            for (suggestion: String in suggestions.results) {
                val suggestionEntry = document.createElement("dd") as HTMLElement
                suggestionEntry.style.borderBottom = "1px dotted #000"
                suggestionEntry.style.marginLeft = "0"
                suggestionEntry.style.padding = "0.2em"
                suggestionEntry.style.fontSize = "1.5em"
                suggestionEntry.innerHTML = "$suggestion&hellip;"
                suggestionEntry.onclick = {
                    log(suggestionEntry.innerText)
                    finder.value = suggestionEntry.innerText
                    search()
                }
                findingsContainer.appendChild(suggestionEntry)
            }
            if (suggestions.results.length > 0) {
                findingsContainer.style.display = "block"
            }
        }
    }
    req.onerror = {
        log("Error: ${req.response}")
    }
    req.send()
}

private fun search() {
    if (finder.value.equals("/selftest")) {
        findingsContainer.remove()
        finder.parentNode?.appendChild(debugView)
        selfTest()
        return
    }
    val req = XMLHttpRequest()
    req.open("GET", "$finderService/$siteId/$finderEndpoint?query=${finder.value}")
    req.onload = {
        findingsContainer.clear()
        if (req.status.equals(200)) {
            val findings = JSON.parse<Findings>(req.responseText)
            findings.results.forEach { finding ->
                val dtTitle = document.createElement("dt") as HTMLElement
                dtTitle.innerHTML = finding.title
                dtTitle.setAttribute(
                    "style",
                    "margin-bottom: .5em; padding-top: 1rem; font-style: italic; border-top: 1px dashed #ccc;"
                )
                findingsContainer.appendChild(dtTitle)
                val ddBody = document.createElement("dd") as HTMLElement
                ddBody.innerHTML = finding.body
                ddBody.setAttribute("style", "margin-bottom: .5em")
                findingsContainer.appendChild(ddBody)
                val ddLabels = document.createElement("dd") as HTMLElement
                ddLabels.innerHTML = if (finding.sisLabels.asDynamic().length === 0) "" else "\uD83C\uDFF7️"
                ddLabels.innerHTML += finding.sisLabels
                ddLabels.setAttribute("style", "margin-bottom: .5em; float: right;")
                findingsContainer.appendChild(ddLabels)
                val ddUrl = document.createElement("dd") as HTMLElement
                ddUrl.innerHTML = "<a style=\"text-decoration:none\" href=\"${finding.urlRaw}\">${finding.url}</a>"
                ddUrl.setAttribute("style", "margin-bottom: 1em;")
                findingsContainer.appendChild(ddUrl)
            }
            if (findings.results.isEmpty()) {
                val dtTitle = document.createElement("dt") as HTMLElement
                dtTitle.innerHTML = "..."
                dtTitle.setAttribute(
                    "style", "margin-bottom: .5em;" +
                            "text-align: center;" +
                            "font-size: 2em;"
                )
                findingsContainer.appendChild(dtTitle)
            }
            findingsContainer.style.display = "block"
        }
    }
    req.onerror = {
        log("Error: ${req.response}")
    }
    req.send()
}

private fun resetInheritedStyleProperties(dirtyElement: HTMLElement) { // very expensive operation!!!
    window.getComputedStyle(dirtyElement).cssText.split(": ; ")
        .forEach { cssProperty -> dirtyElement.style.setProperty(cssProperty, "initial") }
}