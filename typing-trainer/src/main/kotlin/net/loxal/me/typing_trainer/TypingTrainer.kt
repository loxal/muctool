/*
 * MUCtool Web Toolkit
 *
 * Copyright 2018 Alexander Orlov <alexander.orlov@loxal.net>. All rights reserved.
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

package net.loxal.me.typing_trainer

import org.w3c.dom.HTMLCanvasElement
import org.w3c.dom.HTMLDivElement
import org.w3c.dom.HTMLInputElement
import org.w3c.dom.HTMLTextAreaElement
import org.w3c.dom.events.KeyboardEvent
import kotlin.browser.document
import kotlin.browser.window
import kotlin.js.Math.random
import kotlin.math.floor

class TypingTrainer {
    private val cursor = '>'
    private val theEndChar = '<'
    private var typoCount: Int = 0
    private var text: String
    private val marqueeCanvas: MarqueeCanvas
    private val idxOfComparable = 1
    private val startHint = "Go ahead and start typing these letters :)"
    private val doneHint = "Done!"
    private val canvas = document.getElementById("marquee") as HTMLCanvasElement
    private val status = document.getElementById("status") as HTMLDivElement
    private val statistics = document.getElementById("statistics") as HTMLDivElement
    private val statisticsContainer = document.getElementById("statisticsContainer") as HTMLDivElement
    private val textSize = document.getElementById("textSize") as HTMLInputElement
    private val spaceCharAfter = document.getElementById("spaceCharAfter") as HTMLInputElement
    private val customText = document.getElementById("customText") as HTMLTextAreaElement
    private val textSizeLabel = document.getElementById("textSizeLabel") as HTMLDivElement
    private val typoCountLabel = document.getElementById("typoCountLabel") as HTMLDivElement
    private val typoRateLabel = document.getElementById("typoRateLabel") as HTMLDivElement

//    private val useTheseSettings = document.getElementById("useTheseSettings") as HTMLButtonElement
//    private val useThisText = document.getElementById("useThisText") as HTMLButtonElement

    private fun showTypo() {
        typoCount++
        marqueeCanvas.showMistake()
    }

    private fun validateChar(pressedChar: Char) {
        if (marqueeCanvas.text[idxOfComparable] == pressedChar) {
            continueAsCorrect()
        } else {
            showTypo()
        }
    }

    private fun updateText(text: String) {
        marqueeCanvas.text = text
    }

    private fun hasFinished(): Boolean {
        return marqueeCanvas.text.length == 2
    }

    private fun finish() {
        status.textContent = "Done!"
        showStats()
    }

    private fun continueAsCorrect() {
        marqueeCanvas.continueAsCorrect()
        updateText("$cursor${marqueeCanvas.text.substring(idxOfComparable + 1)}")
        marqueeCanvas.goBack()
        if (hasFinished()) finish()
    }

    private fun generateText(): String {
        val space = ' '
        val textSize = textSize.value.toInt()
        val spaceCharAfter = spaceCharAfter.value.toInt()

        val text = StringBuilder()
        for (idx in 1..textSize) {
            text.append(generateRandomChar())
            if (idx.rem(spaceCharAfter) == 0 && idx != textSize)
                text.append(space)
        }

        return text.toString()
    }

    private fun attachControlChars(text: String): String {
        return cursor + text + theEndChar
    }

    private fun generateRandomChar(): Char {
        val alphabetCount = 26

        return 'a' + floor(alphabetCount * random()).toInt()
    }

    init {
        initListener()

        text = generateText()
        marqueeCanvas = MarqueeCanvas(attachControlChars(text))
    }

    private fun restart() {
        status.textContent = startHint
        typoCount = 0
        updateText(attachControlChars(text))
        marqueeCanvas.restart()
    }

    private fun initListener() {
        addCharValidator()

        customText.onclick = {
            text = customText.value.toLowerCase()
            marqueeCanvas.text = "Click to Start"
            "".asDynamic()
        }

        textSize.onclick = {
            text = generateText()
            marqueeCanvas.text = "Click to Start"
            "".asDynamic()
        }

        spaceCharAfter.onclick = {
            text = generateText()
            marqueeCanvas.text = "Click to Start"
            "".asDynamic()
        }

        canvas.onclick = {
            restart()
        }
    }

    private fun addCharValidator() {
        window.onload = {
            document.body?.onkeydown = { pressedKey ->
                if (!hasFinished())
                    validateChar((pressedKey as KeyboardEvent).key.toLowerCase()[0])
            }
            "".asDynamic()
        }
    }

    private fun showStats() {
        val ofPercent = 100
        val typoRate: Double = typoCount.toDouble().div(text.length) * ofPercent

        textSizeLabel.textContent = "Text Size: ${text.length} characters"
        typoCountLabel.textContent = "Overall Typos: ${typoCount}"
        typoRateLabel.textContent = "Typo Rate: ${typoRate.toString().substring(0, 5)}%"
    }
}
