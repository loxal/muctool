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

import org.jetbrains.common.toPlainObjectStripNull

interface ReactElement

internal object ReactWrapper {
    fun normalize(child: Any?): List<Any> = when (child) {
        null -> listOf()
        is Iterable<*> -> child.filterNotNull()
        is Array<*> -> child.filterNotNull()
        else -> listOf(child)
    }

    fun createRaw(type: Any, props: dynamic, child: Any? = null): ReactElement =
            React.createElement(type, toPlainObjectStripNull(props), *normalize(child).toTypedArray())
}
