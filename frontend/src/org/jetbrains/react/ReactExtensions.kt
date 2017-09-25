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

package org.jetbrains.react

import kotlin.properties.ReadOnlyProperty
import kotlin.properties.ReadWriteProperty

interface ReactComponentLifecycleListener {
    fun reactComponentWillUpdate()

    fun reactComponentWillUnmount()

    fun reactComponentWillMount()

    fun reactComponentDidMount()
}

interface ReactExtensionProvider {
    fun subscribe(listener: ReactComponentLifecycleListener)
    fun unsubscribe(listener: ReactComponentLifecycleListener)
}

abstract class BaseReactExtension(val provider: ReactExtensionProvider) {

    private val listener = object : ReactComponentLifecycleListener {
        override fun reactComponentWillUpdate() {
            componentWillUpdate()
        }

        override fun reactComponentWillUnmount() {
            provider.unsubscribe(this)
            componentWillUnmount()
        }

        override fun reactComponentWillMount() {
            componentWillMount()
        }

        override fun reactComponentDidMount() {
            componentDidMount()
        }
    }

    init {
        provider.subscribe(listener)
    }

    open fun componentWillUpdate() {}

    open fun componentWillUnmount() {}

    open fun componentWillMount() {}

    open fun componentDidMount() {}
}

abstract class BaseReactExtensionReadWriteProperty<T>(provider: ReactExtensionProvider) : BaseReactExtension(provider), ReadWriteProperty<Any, T>

abstract class BaseReactExtensionReadOnlyProperty<T>(provider: ReactExtensionProvider) : BaseReactExtension(provider), ReadOnlyProperty<Any, T>

