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
     * @param upperLeft  - upper left radius
     * @param upperRight - upper right radius
     * @param lowerLeft  - lower left radius
     * @param lowerRight - lower right radius
     */
    operator fun set(
        upperLeft: Float, upperRight: Float,
        lowerLeft: Float, lowerRight: Float
    ) {
        this.upperLeft = upperLeft
        this.upperRight = upperRight
        this.lowerLeft = lowerLeft
        this.lowerRight = lowerRight
    }

    /**
     * Add values to each corner radius separately.
     * @param addUpperLeft - add value to the upper left radius
     * @param addUpperRight - add value to the upper right radius
     * @param addLowerLeft - add value to the lower left radius
     * @param addLowerRight - add value to the lower right radius
     */
    fun add(
        addUpperLeft: Float, addUpperRight: Float,
        addLowerLeft: Float, addLowerRight: Float
    ) {
        upperLeft += addUpperLeft
        upperRight += addUpperRight
        lowerLeft += addLowerLeft
        lowerRight += addLowerRight
    }

    /**
     * Add value to all four corner radii.
     * @param value - value to be added to all four corner radii
     */
    fun add(value: Float) {
        this.add(value, value, value, value)
    }

    override fun toString(): String {
        return "upperLeft: $upperLeft, upperRight: $upperRight, lowerLeft: $lowerLeft, lowerRight: $lowerRight"
    }
}