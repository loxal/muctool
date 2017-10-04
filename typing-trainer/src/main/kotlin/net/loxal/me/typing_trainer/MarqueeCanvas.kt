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

package net.loxal.me.typing_trainer

import org.w3c.dom.CanvasRenderingContext2D
import org.w3c.dom.HTMLCanvasElement
import kotlin.browser.document
import kotlin.browser.window

class MarqueeCanvas(var text: String) {
    private val canvas = document.getElementById("marquee") as HTMLCanvasElement
    private val context: CanvasRenderingContext2D = canvas.getContext("2d")!! as CanvasRenderingContext2D
    private var xPos: Double = 800.0 // or window.innerWidth
    private val yPos: Double = 70.0
    private val steppingProgressPerFrame: Double = 1.9
    private var errorStatus = false
    private val fontColor = "#119"
    private val goBackStepping: Double = 20.0

    private fun renderBackground() {
        context.save()
        canvas.width = xPos.toInt()
        canvas.height = 100
        context.font = "66px serif"

        context.fillStyle = fontColor
        context.fillRect(0.toDouble(), 0.toDouble(), canvas.width.toDouble(), canvas.height.toDouble())
        context.restore()
    }

    fun goBack() {
        xPos += goBackStepping * steppingProgressPerFrame
    }

    private fun drawTextContainer() {
        context.save()
        if (errorStatus) {
            context.fillStyle = "#911"
        } else {
            context.fillStyle = fontColor
        }

        context.fillRect(0.toDouble(), 0.toDouble(), canvas.width.toDouble(), canvas.height.toDouble())
        context.restore()
    }

    fun restart() {
        xPos = canvas.width.toDouble()
        errorStatus = false
    }

    fun showMistake() {
        errorStatus = true
    }

    fun continueAsCorrect() {
        errorStatus = false
    }

    private fun draw() {
        drawOnCanvas()
        if (xPos < 0) {
            xPos += steppingProgressPerFrame
        }
        makeProgressivelySlower()
    }

    private fun drawOnCanvas(): Unit {
        drawTextContainer()

        context.save()
        context.fillStyle = "#ee1"
        context.fillText(text, xPos, yPos)
        context.restore()

        xPos -= steppingProgressPerFrame
    }

    init {
        renderBackground()

        window.setInterval({
            draw()
        }, 10)
    }

    private fun makeProgressivelySlower() {
        // TODO implement
    }
}