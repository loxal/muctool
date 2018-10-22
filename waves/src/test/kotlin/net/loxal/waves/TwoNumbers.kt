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

package net.loxal.waves

import kotlin.test.Test
import kotlin.test.assertEquals

class TwoNumbers {
    @Test
    fun canBeAdded() {
        val adder = Adder()
        assertEquals(10, adder.add(5, 5))
        assertEquals(23, adder.add(5, 5))
    }

    @Test
    fun canBeAdded_whenInputIsNegative() {
        val adder = Adder()
        assertEquals(-10, adder.add(-5, -5))
    }
}