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
import org.jetbrains.demo.thinkter.model.User
import org.jetbrains.react.RState
import org.jetbrains.react.ReactComponentSpec
import org.jetbrains.react.dom.ReactDOMBuilder
import org.jetbrains.react.dom.ReactDOMComponent

class LoginComponent : ReactDOMComponent<UserProps, LoginFormState>() {
    companion object : ReactComponentSpec<LoginComponent, UserProps, LoginFormState>

    init {
        state = LoginFormState("", "", false, "")
    }

    override fun ReactDOMBuilder.render() {
        div {
            form(classes = "pure-form pure-form-stacked") {
                legEnd { +"Login" }

                fieldSet(classes = "pure-group") {
                    input(type = InputType.text, name = "login") {
                        value = state.login
                        placeholder = "Login"
                        disabled = state.disabled

                        onChangeFunction = {
                            setState {
                                login = it.inputValue
                            }
                        }
                    }
                    input(type = InputType.password, name = "password") {
                        value = state.password
                        placeholder = "Password"
                        disabled = state.disabled

                        onChangeFunction = {
                            setState {
                                password = it.inputValue
                            }
                        }
                    }
                }

                state.errorMessage?.takeIf(String::isNotEmpty)?.let { message ->
                    label {
                        +message
                    }
                }

                button(classes = "pure-button pure-button-primary") {
                    +"Login"
                    disabled = state.disabled

                    onClickFunction = {
                        it.preventDefault()
                        doLogin()
                    }
                }
            }
        }
    }

    private fun doLogin() {
        setState {
            disabled = true
        }
        async {
            val user = login(state.login, state.password)
            loggedIn(user)
        }.catch { err -> loginFailed(err) }
    }

    private fun loggedIn(user: User) {
        props.userAssigned(user)
    }

    private fun loginFailed(err: Throwable) {
        if (err is LoginOrRegisterFailedException) {
            setState {
                disabled = false
                errorMessage = err.message
            }
        } else {
            console.error("Login failed", err)
            setState {
                disabled = false
                errorMessage = "Login failed: please reload page and try again"
            }
        }
    }
}

class LoginFormState(var login: String, var password: String, var disabled: Boolean, var errorMessage: String?) : RState