package com.slaviboy.colorpicker

// Copyright (C) 2020 Stanislav Georgiev
//  https://github.com/slaviboy
//
//	This program is free software: you can redistribute it and/or modify
//	it under the terms of the GNU Affero General Public License as
//	published by the Free Software Foundation, either version 3 of the
//	License, or (at your option) any later version.
//
//	This program is distributed in the hope that it will be useful,
//	but WITHOUT ANY WARRANTY; without even the implied warranty of
//	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//	GNU Affero General Public License for more details.
//
//	You should have received a copy of the GNU Affero General Public License
//	along with this program.  If not, see <http://www.gnu.org/licenses/>.

/**
 * Simple range class with upper and lower limit, also current value that is bound to the limit.
 * (lower and upper values can be positive or negative)
 *
 * Ranges are values that are specific for each color window, depending on selector position in the
 * color window, the current range value is changed responsively.
 *
 * For example if we have horizontal range [lower:0, upper:100]  and the color window has width
 * of 200px, then if selector is on position.
 * X: 0px   => current range value is 0
 * X: 100px => current range value is 50
 * X: 200px => current range value is 100
 *
 * @param lower set lower bound value for the range
 * @param upper set upper bound value for the range
 */
class Range(var lower: Float, var upper: Float) {

    var current = 0.0f // current value that is bound to [lower, upper]
        set(value) {

            // get min and max
            val min = Math.min(lower, upper)
            val max = Math.max(lower, upper)

            // set current with check
            field = when {
                value < min -> {
                    min
                }
                value > max -> {
                    max
                }
                else -> {
                    value
                }
            }
        }

    /**
     * Set current value, using total and current allowed values, for example if a total side length
     * is 10cm and current position is 2cm, and we have range [0,100]. Then current range value is 20.
     * @param total total value
     * @param current current value
     */
    fun setCurrent(total: Float, current: Float) {
        this.current = lower - (lower - upper) * (current / total)
    }

    override fun toString(): String {
        return "lower: $lower, upper: $upper, current: $current"
    }
}