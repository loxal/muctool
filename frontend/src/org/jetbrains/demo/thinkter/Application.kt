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

import kotlinx.html.div
import kotlinx.html.h1
import kotlinx.html.js.onClickFunction
import kotlinx.html.nav
import org.jetbrains.common.async
import org.jetbrains.demo.thinkter.model.Thought
import org.jetbrains.demo.thinkter.model.User
import org.jetbrains.react.RProps
import org.jetbrains.react.RState
import org.jetbrains.react.ReactComponentNoProps
import org.jetbrains.react.ReactComponentSpec
import org.jetbrains.react.dom.ReactDOM
import org.jetbrains.react.dom.ReactDOMBuilder
import org.jetbrains.react.dom.ReactDOMComponent
import org.jetbrains.react.dom.render
import kotlin.browser.document

fun main(args: Array<String>) {
//    org.jetbrains.common.require("pure-blog.css")
    org.jetbrains.common.require("main.css")

    ReactDOM.render(document.getElementById("content")) {
        div {
            Application {}
        }
    }
}

class Application : ReactDOMComponent<ReactComponentNoProps, ApplicationPageState>() {
    companion object : ReactComponentSpec<Application, ReactComponentNoProps, ApplicationPageState>

    val polling = Polling()

    init {
        state = ApplicationPageState(MainView.Home)
        checkUserSession()
    }

    override fun componentWillUnmount() {
        polling.stop()
        super.componentWillUnmount()
    }

    override fun ReactDOMBuilder.render() {
        div("pure-g") {
            div("sidebar pure-u-1 pure-u-md-1-4") {
                div("header") {
                    div("brand-title") {
                        +"Thinkter"

                        if (state.selected != MainView.Loading) {
                            onClickFunction = { mainViewSelected() }
                        }
                    }
                    nav("nav") {
                        if (state.selected != MainView.Loading) {
                            NavBarComponent {
                                user = state.currentUser
                                handler = { navBarSelected(it) }
                                logoutHandler = { onLoggedOut() }
                                poller = this@Application.polling
                            }
                        }
                    }
                }
            }

            div("content pure-u-1 pure-u-md-3-4") {
                when (state.selected) {
                    MainView.Loading -> h1 { +"Loading..." }
                    MainView.Home -> HomeView {
                        showThought = { t -> onShowThought(t) }
                        polling = this@Application.polling
                    }
                    MainView.Login -> LoginComponent {
                        userAssigned = { onUserAssigned(it) }
                    }
                    MainView.Register -> RegisterComponent {
                        userAssigned = { onUserAssigned(it) }
                    }
                    MainView.PostThought -> NewThoughtComponent {
                        showThought = { t -> onShowThought(t) }
                        replyTo = state.replyTo
                    }
                    MainView.User -> {
                    }
                    MainView.Thought -> ViewThoughtComponent {
                        thought = state.currentThought ?: Thought(0, "?", "?", "?", null)
                        currentUser = state.currentUser
                        reply = { onReplyTo(it) }
                        leave = { mainViewSelected() }
                    }
                }
            }

            div("footer") {
                +"Thinkter kotlin frontend + react + ktor example"
            }
        }
    }

    private fun onReplyTo(t: Thought) {
        setState {
            replyTo = t
            selected = MainView.PostThought
        }
    }

    private fun onLoggedOut() {
        val oldSelected = state.selected

        setState {
            currentUser = null
            selected = when (oldSelected) {
                MainView.Home, MainView.Thought, MainView.Login, MainView.Register -> oldSelected
                else -> MainView.Home
            }
        }
    }

    private fun onShowThought(t: Thought) {
        setState {
            currentThought = t
            selected = MainView.Thought
        }
    }

    private fun navBarSelected(newSelected: MainView) {
        setState {
            selected = newSelected
        }
    }

    private fun onUserAssigned(user: User) {
        setState {
            currentUser = user
            selected = MainView.Home
        }
    }

    private fun mainViewSelected() {
        setState {
            selected = MainView.Home
        }
    }

    private fun checkUserSession() {
        async {
            val user = checkSession()
            onUserAssigned(user)
        }.catch {
            setState {
                selected = MainView.Home
            }
        }
    }
}

enum class MainView {
    Loading,
    Register,
    Login,
    User,
    PostThought,
    Thought,
    Home
}

class ApplicationPageState(var selected: MainView, var currentUser: User? = null, var currentThought: Thought? = null, var replyTo: Thought? = null) : RState
class UserProps : RProps() {
    var userAssigned: (User) -> Unit = {}
}
