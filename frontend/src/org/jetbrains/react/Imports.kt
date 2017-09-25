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

external interface ReactUpdater {
    fun enqueueSetState(dest: Any, state: Any?)
    fun enqueueReplaceState(dest: Any, state: Any?)
    fun enqueueCallback(dest: Any, callback: Any, method: String)
}

@JsModule("react")
@JsNonModule
external object React {
    fun createElement(type: Any, props: dynamic, vararg child: Any): ReactElement
}
