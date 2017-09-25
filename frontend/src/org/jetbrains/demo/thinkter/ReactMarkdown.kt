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

import org.jetbrains.react.RProps
import org.jetbrains.react.ReactExternalComponentSpec

private val ReactMarkdown: dynamic = org.jetbrains.common.require("react-markdown")

class ReactMarkdownProps(var source: String = "") : RProps()

object ReactMarkdownComponent : ReactExternalComponentSpec<ReactMarkdownProps>(ReactMarkdown)
