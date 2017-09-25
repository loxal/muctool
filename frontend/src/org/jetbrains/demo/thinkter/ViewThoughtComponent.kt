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

import kotlinx.html.a
import kotlinx.html.div
import kotlinx.html.js.onClickFunction
import kotlinx.html.span
import kotlinx.html.style
import org.jetbrains.common.jsstyle
import org.jetbrains.common.launch
import org.jetbrains.demo.thinkter.model.Thought
import org.jetbrains.demo.thinkter.model.User
import org.jetbrains.react.RProps
import org.jetbrains.react.ReactComponentNoState
import org.jetbrains.react.ReactComponentSpec
import org.jetbrains.react.dom.ReactDOMBuilder
import org.jetbrains.react.dom.ReactDOMComponent
import kotlin.browser.window

class ViewThoughtComponent : ReactDOMComponent<ViewThoughtComponent.Props, ReactComponentNoState>() {

    companion object : ReactComponentSpec<ViewThoughtComponent, Props, ReactComponentNoState>

    init {
        state = ReactComponentNoState()
    }

    override fun ReactDOMBuilder.render() {
        val userId = props.thought.userId
        val text = props.thought.text
        val date = props.thought.date

        div(classes = "pure-g") {
            div(classes = "pure-u-1 pure-u-md-1-3") {
                +userId
                props.thought.replyTo?.let { id ->
                    +" replies to $id"
                }
            }
            div(classes = "pure-u-1 pure-u-md-2-3") {
                +date
            }
            div(classes = "pure-u-2 pure-u-md-1-1") {
                ReactMarkdownComponent {
                    source = text
                }
            }

            if (props.currentUser != null) {
                div(classes = "pure-u-3 pure-u-md-2-3") {
                    +""
                }
                div(classes = "pure-u-3 pure-u-md-1-3") {
                    a(href = "javascript:void(0)") {
                        +"Delete"

                        onClickFunction = {
                            delete()
                        }
                    }

                    span {
                        style = jsstyle { margin = "0 5px 0 5px" }
                        +" "
                    }

                    a(href = "javascript:void(0)") {
                        +"Reply"

                        onClickFunction = {
                            props.reply(props.thought)
                        }
                    }
                }
            }
        }
    }

    private fun delete() {
        if (window.confirm("Do you want to delete the thought?")) {
            launch {
                val token = postThoughtPrepare()
                deleteThought(props.thought.id, token.date, token.code)
                props.leave
            }
        }
    }

    class Props(var thought: Thought, var currentUser: User? = null, var reply: (Thought) -> Unit = {}, var leave: () -> Unit = {}) : RProps()
}