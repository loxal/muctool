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

import kotlinx.html.*
import kotlinx.html.js.onChangeFunction
import kotlinx.html.js.onClickFunction
import org.jetbrains.common.async
import org.jetbrains.common.inputValue
import org.jetbrains.demo.thinkter.model.Thought
import org.jetbrains.demo.thinkter.model.User
import org.jetbrains.react.RProps
import org.jetbrains.react.RState
import org.jetbrains.react.ReactComponentSpec
import org.jetbrains.react.dom.ReactDOMBuilder
import org.jetbrains.react.dom.ReactDOMComponent

class NewThoughtComponent : ReactDOMComponent<NewThoughtComponent.Props, NewThoughtComponent.State>() {
    companion object : ReactComponentSpec<NewThoughtComponent, Props, State>

    init {
        state = State()
    }

    override fun ReactDOMBuilder.render() {
        form(classes = "pure-form pure-form-stacked") {
            legEnd {
                if (props.replyTo == null) {
                    +"New thought"
                } else {
                    +"Reply"
                }
            }

            props.replyTo?.let { replyTo ->
                div {
                    +"reply to ${replyTo.userId}"
                }
            }

            textArea(classes = "pure-input-1-2") {
                placeholder = "Your thought..."

                onChangeFunction = {
                    setState {
                        text = it.inputValue
                    }
                }
            }

            state.errorMessage?.let { message ->
                p { +message }
            }

            button(classes = "pure-button pure-button-primary") {
                +"Post"

                onClickFunction = {
                    it.preventDefault()
                    doPostThought()
                }
            }
        }
    }

    private fun doPostThought() {
        async {
            val token = postThoughtPrepare()
            val thought = postThought(props.replyTo?.id, state.text, token)
            onSubmitted(thought)
        }.catch { err -> onFailed(err) }
    }

    private fun onSubmitted(thought: Thought) {
        setState {
            errorMessage = null
        }

        props.showThought(thought)
    }

    private fun onFailed(err: Throwable) {
        setState {
            errorMessage = err.message
        }
    }

    class State(var text: String = "", var errorMessage: String? = null) : RState
    class Props(var replyTo: Thought? = null, var replyToUser: User? = null, var showThought: (Thought) -> Unit = {}) : RProps()
}