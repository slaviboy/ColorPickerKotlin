package com.slaviboy.colorpicker.models

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
 * Class that represents HWB(HUE, WHITE and BLACK) color model
 * and hold individual value for given color.
 * @param h hue [0,360]
 * @param w white [0,100]
 * @param b black [0,100]
 */
class HWB(var h: Int = 0, var w: Int = 0, var b: Int = 0) {

    var hSuffix = H_SUFFIX
    var wSuffix = W_SUFFIX
    var bSuffix = B_SUFFIX

    /**
     * Constructor that set values using HWB object.
     * @param hwb - hwb object
     */
    constructor(hwb: HWB) : this(hwb.h, hwb.w, hwb.b)

    /**
     * Public setter that sets initial values using HWB object.
     * @param hwb - existing hwb object
     */
    fun setHWB(hwb: HWB) {
        h = hwb.h
        w = hwb.w
        b = hwb.b
    }

    /**
     * Public setter that sets HWB object using individual values.
     * @param h - hue
     * @param w - white
     * @param b - black
     */
    fun setHWB(h: Int, w: Int, b: Int) {
        this.h = h
        this.w = w
        this.b = b
    }

    /**
     * Set suffix for each value, separately.
     * @param hSuffix - hue suffix
     * @param wSuffix - white suffix
     * @param bSuffix - black suffix
     */
    fun setSuffix(
        hSuffix: String,
        wSuffix: String,
        bSuffix: String
    ) {
        this.hSuffix = hSuffix
        this.wSuffix = wSuffix
        this.bSuffix = bSuffix
    }

    /**
     * Get HWB values as an array.
     * @return array with corresponding values
     */
    fun getArray(): IntArray {
        return intArrayOf(h, w, b)
    }

    override fun toString(): String {
        return getString()
    }

    /**
     * Return string, with all corresponding value, where you can specify whether or not to
     * use suffix after each value.
     * @param withSuffix - flag showing if suffix should be used
     */
    fun getString(withSuffix: Boolean = true): String {
        return if (withSuffix) {
            "$h$hSuffix$w$wSuffix$b$bSuffix"
        } else {
            "$h $w $b"
        }
    }

    companion object {

        // max and min range values for each variable
        const val H_MIN = 0
        const val H_MAX = 360
        const val W_MIN = 0
        const val W_MAX = 100
        const val B_MIN = 0
        const val B_MAX = 100

        // default suffix for each variable, when returning string
        const val H_SUFFIX = "°, "
        const val W_SUFFIX = ", "
        const val B_SUFFIX = ""

        /**
         * Check if hue value is in range [0,360].
         * @param h - hue value to be checked
         * @return boolean if value is in range
         */
        fun inRangeH(h: Int): Boolean {
            return h >= H_MIN && h <= H_MAX
        }

        /**
         * Check if white value is in range [0,100].
         * @param w - white value to be checked
         * @return boolean if value is in range
         */
        fun inRangeW(w: Int): Boolean {
            return w >= W_MIN && w <= W_MAX
        }

        /**
         * Check if black value is in range [0,100].
         * @param b - black value to be checked
         * @return boolean if value is in range
         */
        fun inRangeB(b: Int): Boolean {
            return b >= B_MIN && b <= B_MAX
        }
    }
}