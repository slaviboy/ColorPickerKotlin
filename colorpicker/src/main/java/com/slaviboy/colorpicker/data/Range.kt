/*
* Copyright (C) 2020 Stanislav Georgiev
* https://github.com/slaviboy
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*      http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package com.slaviboy.colorpicker.data

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
class Range(var lower: Float = 0f, var upper: Float = 100f, current: Float = Math.min(lower, upper)) {

    // current value that is bound to [lower, upper]
    var current: Float = current
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
        val fact = (current / total)
        this.current = lower - (lower - upper) * fact
    }

    /**
     * Get the current value in a range between [expectedLower, expectedUpper], this is the new bound.
     * For example if the original range is [0,100] and current value is 20. Then if we want to now the
     * current value in new range with values between [-100,100], that will return the current value as -60.
     */
    fun getCurrent(expectedLower: Float, expectedUpper: Float): Float {
        val lowerDistance = Math.abs(current - lower)
        val upperDistance = Math.abs(current - upper)
        val distance = lowerDistance + upperDistance
        val fact = (lowerDistance / distance)
        val newCurrent = expectedLower - (expectedLower - expectedUpper) * fact
        return newCurrent
    }

    override fun toString(): String {
        return "lower: $lower, upper: $upper, current: $current"
    }
}