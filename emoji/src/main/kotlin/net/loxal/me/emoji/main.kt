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

package net.loxal.me.emoji

import org.w3c.dom.*
import kotlin.browser.document
import kotlin.dom.clear

private val charCode = document.getElementById("pictogramId") as HTMLInputElement
private val start = document.getElementById("from") as HTMLInputElement
private val end = document.getElementById("to") as HTMLInputElement
private val rangeSelector = document.getElementById("rangeSelector") as HTMLSelectElement
private val view1 = document.getElementById("view1") as HTMLDivElement
private val view2 = document.getElementById("view2") as HTMLDivElement
private val view3 = document.getElementById("view3") as HTMLDivElement
private val view4 = document.getElementById("view4") as HTMLDivElement
private val showTrigger = document.getElementById("show") as HTMLButtonElement
private val selectTrigger = document.getElementById("select") as HTMLButtonElement
private val charContainer = document.getElementById("list") as HTMLTableSectionElement
private val containerColumnWidth: Int = 9
private val charRanges = mapOf(
        "arrow" to 8582..8705,
        "math" to 8764..9193,
        "corner" to 9472..9908,
        "star" to 9900..9985,
        "ascii" to 33..128,
        "other" to 9000..10000
)

private fun initListeners() {
    showTrigger.onclick = {
        showChar()
    }

    selectTrigger.onclick = {
        charContainer.clear()
        val charRange = obtainSelectedRange()

        updateInputFields(charRange)

        listChars(start = charRange.start, end = charRange.endInclusive)
    }
}

private fun obtainSelectedRange(): IntRange {
    val selectedOption = rangeSelector.options.item(rangeSelector.selectedIndex) as HTMLOptionElement
    val charRange = charRanges.get(selectedOption.value) as IntRange

    return charRange
}

private fun updateInputFields(charRange: IntRange) {
    start.value = charRange.start.toString()
    end.value = charRange.endInclusive.toString()
}

private fun init() {
    start.type = "number"
    end.type = "number"

    initListeners()
}

private fun showChar() {
    view1.innerHTML = "&#${charCode.value};"
    view2.innerHTML = "&#${charCode.value};"
    view3.innerHTML = "&#${charCode.value};"
    view4.innerHTML = "&#${charCode.value};"
}

/**
 * http://codepoints.net/basic_multilingual_plane
 */
fun main(vararg args: String) {
    init()

    listChars(start = start.value.toInt(), end = end.value.toInt())
}

private fun listChars(start: Int = 9900, end: Int = 9985) {
    for (char: Int in start..end) {
        if (needsRow()) appendCharRow()

        val charCol = document.createElement("td") as HTMLTableCellElement
        charCol.onclick = {
            charCode.value = char.toString()
            showChar()
        }

        charCol.textContent = char.toChar().toString()

        charContainer.lastChild?.appendChild(charCol)
    }
}

private fun needsRow() =
        charContainer.lastChild == null
                || charContainer.lastChild!!.childNodes.length.rem(containerColumnWidth) == 0

private fun appendCharRow() {
    val charRow = document.createElement("tr") as HTMLTableRowElement
    charContainer.appendChild(charRow)
}
