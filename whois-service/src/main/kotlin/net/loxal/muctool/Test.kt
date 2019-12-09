/*
 * MUCtool Web Toolkit
 *
 * Copyright 2019 Alexander Orlov <alexander.orlov@loxal.net>. All rights reserved.
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

package net.loxal.muctool

object Test {
    @JvmStatic
    fun main(args: Array<String>) {
        val placeholder = "Java 10 Syntax - complied and executed with openjdk:10-jdk \uD83E\uDD13"
        System.out.println(placeholder)
    }
}


//docker run -v /root:/root -w /root -it openjdk:10-jdk sh -c 'javac Test.java'
//docker run -v /root:/root -w /root -it openjdk:10-jdk sh -c 'java Test'