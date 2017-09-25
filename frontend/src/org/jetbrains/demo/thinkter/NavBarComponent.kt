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

import kotlinx.html.UL
import kotlinx.html.a
import kotlinx.html.js.onClickFunction
import kotlinx.html.li
import kotlinx.html.ul
import org.jetbrains.common.launch
import org.jetbrains.demo.thinkter.model.User
import org.jetbrains.react.RProps
import org.jetbrains.react.RState
import org.jetbrains.react.ReactComponentSpec
import org.jetbrains.react.dom.ReactDOMBuilder
import org.jetbrains.react.dom.ReactDOMComponent

class NavBarComponent : ReactDOMComponent<NavBarComponent.NavBarHandlerProps, NavBarComponent.NavBarState>() {

    companion object : ReactComponentSpec<NavBarComponent, NavBarHandlerProps, NavBarState>

    init {
        state = NavBarState()
    }

    override fun componentDidMount() {
        props.poller?.let { p ->
            p.listeners.add(pollerHandler)
            p.start()
        }

        super.componentDidMount()
    }

    override fun componentWillUnmount() {
        super.componentWillUnmount()
        props.poller?.listeners?.remove(pollerHandler)
    }

    override fun ReactDOMBuilder.render() {
        val user = props.user
        val newMessages = state.newMessages

        ul(classes = "nav-list") {
            val timelineText = "Timeline" + when (newMessages) {
                Polling.NewMessages.None -> ""
                is Polling.NewMessages.Few -> "(${newMessages.n})"
                is Polling.NewMessages.MoreThan -> "(${newMessages.n}+"
            }

            navItem(timelineText) {
                props.poller?.start()
                timeline()
                setState {
                    this.newMessages = Polling.NewMessages.None
                }
            }

            if (user != null) {
                navItem("New thought") {
                    postNew()
                }
                navItem("Sign out, ${user.displayName.takeIf(String::isNotBlank) ?: user.userId}") {
                    logout()
                }
            } else {
                navItem("Sign up") {
                    register()
                }
                navItem("Sign in") {
                    login()
                }
            }
        }
    }

    private val pollerHandler = { count: Polling.NewMessages ->
        setState {
            newMessages = count
        }
    }

    private fun timeline() {
        props.handler(MainView.Home)
    }

    private fun register() {
        props.handler(MainView.Register)
    }

    private fun login() {
        props.handler(MainView.Login)
    }

    private fun logout() {
        launch {
            logoutUser()
            props.logoutHandler()
        }
    }

    private fun postNew() {
        props.handler(MainView.PostThought)
    }

    private fun UL.navItem(title: String, function: () -> Unit = {}) {
        li(classes = "nav-item") {
            a(classes = "pure-button", href = "javascript:void(0)") {
                +title
                onClickFunction = { function() }
            }
        }
    }

    class NavBarState(var newMessages: Polling.NewMessages = Polling.NewMessages.None) : RState

    class NavBarHandlerProps : RProps() {
        var user: User? = null
        var logoutHandler: () -> Unit = {}
        var handler: (MainView) -> Unit = { }
        var poller: Polling? = null
    }
}