/*
 * MUCtool Web Toolkit
 *
 * Copyright 2018 Alexander Orlov <alexander.orlov@loxal.net>. All rights reserved.
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

#!/usr/bin/env kscript
//#!/usr/bin/env kotlinc -script

println("Initializing...")

if (args.isNotEmpty()) {
    println(args.joinToString())
}

println(Runtime.getRuntime().maxMemory() / 1024)
println(Runtime.getRuntime().totalMemory() / 1024)
println(Runtime.getRuntime().availableProcessors())
println(Runtime.getRuntime().freeMemory())
println(Runtime.getRuntime().freeMemory())
println(Runtime.getRuntime().runFinalization())
println(Runtime.getRuntime().exit(-1))

println("Instance initialized")