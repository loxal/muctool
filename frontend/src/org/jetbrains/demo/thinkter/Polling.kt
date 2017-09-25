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

package org.jetbrains.demo.thinkter

import kotlinx.coroutines.experimental.launch
import kotlin.browser.window
import kotlin.js.Date

class Polling(val period: Int = 20000) {
    private var timerId = 0
    var lastTime: Long = Date().getTime().toLong()
    var listeners: MutableList<(NewMessages) -> Unit> = ArrayList()

    fun start() {
        lastTime = Date().getTime().toLong()
        listeners.forEach { it(NewMessages.None) }

        if (timerId == 0) {
            stop()
            timerId = window.setInterval({ tick() }, period)
        }
    }

    fun stop() {
        if (timerId > 0) {
            window.clearInterval(timerId)
            timerId = 0
        }
    }

    fun tick() {
        launch {
            val newMessagesText = pollFromLastTime(lastTime.toString())
            val newMessages = when {
                newMessagesText == "0" || newMessagesText.isBlank() -> NewMessages.None
                newMessagesText.endsWith("+") -> NewMessages.MoreThan(newMessagesText.removeSuffix("+").toInt())
                else -> NewMessages.Few(newMessagesText.toInt())
            }
            listeners.forEach { it(newMessages) }
        }
    }

    sealed class NewMessages {
        object None : NewMessages()
        data class Few(val n: Int) : NewMessages()
        data class MoreThan(val n: Int) : NewMessages()
    }
}
