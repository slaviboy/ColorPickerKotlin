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
 * CornerRadius class that hold values for all four corners 'upper: top, left'
 * and 'lower: top, left' used when creating round rectangular paths.
 * @param upperLeft upper left corner radius
 * @param upperRight upper right corner radius
 * @param lowerLeft lower left corner radius
 * @param lowerRight lower right corner radius
 */
class CornerRadius(
    var upperLeft: Float = 0f,
    var upperRight: Float = 0f,
    var lowerLeft: Float = 0f,
    var lowerRight: Float = 0f
) {

    constructor(cornerRadius: CornerRadius) : this(
        cornerRadius.upperLeft,
        cornerRadius.upperRight,
        cornerRadius.lowerLeft,
        cornerRadius.lowerRight
    )

    /**
     * Set all four radii by individual values passed to the function as arguments.
     * @param upperLeft upper left radius
     * @param upperRight upper right radius
     * @param lowerLeft  lower left radius
     * @param lowerRight lower right radius
     */
    fun setCornerRadius(upperLeft: Float = 0f, upperRight: Float = 0f, lowerLeft: Float = 0f, lowerRight: Float = 0f) {
        this.upperLeft = upperLeft
        this.upperRight = upperRight
        this.lowerLeft = lowerLeft
        this.lowerRight = lowerRight
    }

    /**
     * Add values to each corner radius separately.
     * @param addUpperLeft add value to the upper left radius
     * @param addUpperRight add value to the upper right radius
     * @param addLowerLeft add value to the lower left radius
     * @param addLowerRight add value to the lower right radius
     */
    fun add(addUpperLeft: Float = 0f, addUpperRight: Float = 0f, addLowerLeft: Float = 0f, addLowerRight: Float = 0f) {
        upperLeft += addUpperLeft
        upperRight += addUpperRight
        lowerLeft += addLowerLeft
        lowerRight += addLowerRight
    }

    /**
     * Add value to all four corner radii.
     * @param value value to be added to all four corner radii
     */
    fun addAll(value: Float) {
        this.add(value, value, value, value)
    }

    /**
     * Subtract values to each corner radius separately.
     * @param subtractUpperLeft subtract value to the upper left radius
     * @param subtractUpperRight subtract value to the upper right radius
     * @param subtractLowerLeft subtract value to the lower left radius
     * @param subtractLowerRight subtract value to the lower right radius
     */
    fun subtract(subtractUpperLeft: Float = 0f, subtractUpperRight: Float = 0f, subtractLowerLeft: Float = 0f, subtractLowerRight: Float = 0f) {
        upperLeft -= subtractUpperLeft
        upperRight -= subtractUpperRight
        lowerLeft -= subtractLowerLeft
        lowerRight -= subtractLowerRight
    }

    /**
     * Subtract value to all four corner radii.
     * @param value value to be subtracted to all four corner radii
     */
    fun subtractAll(value: Float) {
        subtract(value, value, value, value)
    }

    override fun toString(): String {
        return "upperLeft: $upperLeft, upperRight: $upperRight, lowerLeft: $lowerLeft, lowerRight: $lowerRight"
    }
}