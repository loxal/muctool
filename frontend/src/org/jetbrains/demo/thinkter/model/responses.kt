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

package org.jetbrains.demo.thinkter.model

interface RpcData

data class IndexResponse(val top: List<Thought>, val latest: List<Thought>) : RpcData
data class PostThoughtToken(val user: String, val date: Long, val code: String) : RpcData
data class PostThoughtResult(val thought: Thought) : RpcData
data class UserThoughtsResponse(val user: User, val thoughts: List<Thought>) : RpcData
data class ViewThoughtResponse(val thought: Thought, val date: Long, val code: String?) : RpcData
data class LoginResponse(val user: User? = null, val error: String? = null) : RpcData