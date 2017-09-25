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
import kotlinx.html.js.onClickFunction
import org.jetbrains.demo.thinkter.model.Thought
import org.jetbrains.react.RProps
import org.jetbrains.react.ReactComponentNoState
import org.jetbrains.react.ReactComponentSpec
import org.jetbrains.react.dom.ReactDOMBuilder
import org.jetbrains.react.dom.ReactDOMComponent

class ThoughtsListComponent : ReactDOMComponent<ThoughtsListComponent.Props, ReactComponentNoState>() {
    companion object : ReactComponentSpec<ThoughtsListComponent, Props, ReactComponentNoState>

    init {
        state = ReactComponentNoState()
    }

    override fun ReactDOMBuilder.render() {
        fun UL.thoughtLi(t: Thought) {
            li {
                section(classes = "post") {
                    header(classes = "post-header") {
                        p(classes = "post-meta") {
                            a(href = "javascript:void(0)") {
                                +t.date
                                +" by "
                                +t.userId

                                onClickFunction = {
                                    props.show(t)
                                }
                            }
                        }
                    }
                    div(classes = "post-description") {
                        ReactMarkdownComponent {
                            source = t.text
                        }
                    }
                }
            }
        }

        div {
            ul {
                if (props.thoughts.isEmpty()) {
                    li { +"There are no thoughts yet" }
                } else {
                    for (t in props.thoughts) {
                        thoughtLi(t)
                    }
                }
            }
        }
    }

    class Props(var thoughts: List<Thought> = emptyList(), var show: (Thought) -> Unit = {}) : RProps()
}