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
 * Class that represents HSL(HUE, SATURATION and LIGHTNESS) color model
 * and hold individual value for given color.
 * @param h hue [0,360]
 * @param s saturation [0,100]
 * @param l lightness [0,100]
 */
class HSL(
    var h: Int = 0,
    var s: Int = 0,
    var l: Int = 0
) {

    var hSuffix = H_SUFFIX
    var sSuffix = S_SUFFIX
    var lSuffix = L_SUFFIX

    /**
     * Constructor that set values using HSL object.
     * @param hsl - hsl object
     */
    constructor(hsl: HSL) : this(hsl.h, hsl.s, hsl.l)

    /**
     * Public setter that sets initial values using HSL object.
     * @param hsl - existing hsl object
     */
    fun setHSL(hsl: HSL) {
        h = hsl.h
        s = hsl.s
        l = hsl.l
    }

    /**
     * Public setter that sets HSL object using individual values.
     * @param h - hue
     * @param s - saturation
     * @param l - lightness
     */
    fun setHSL(h: Int, s: Int, l: Int) {
        this.h = h
        this.s = s
        this.l = l
    }

    /**
     * Set suffix for each value, separately.
     * @param hSuffix - hue suffix
     * @param sSuffix - saturation suffix
     * @param lSuffix - lightness suffix
     */
    fun setSuffix(
        hSuffix: String,
        sSuffix: String,
        lSuffix: String
    ) {
        this.hSuffix = hSuffix
        this.sSuffix = sSuffix
        this.lSuffix = lSuffix
    }

    /**
     * Get HSL values as an array.
     * @return array with corresponding values
     */
    fun getArray(): IntArray {
        return intArrayOf(h, s, l)
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
            "$h$hSuffix$s$sSuffix$l$lSuffix"
        } else {
            "$h $s $l"
        }
    }

    companion object {

        // max and min range values for each variable
        const val H_MIN = 0
        const val H_MAX = 360
        const val S_MIN = 0
        const val S_MAX = 100
        const val L_MIN = 0
        const val L_MAX = 100

        // default suffix for each variable, when returning string
        const val H_SUFFIX = "°, "
        const val S_SUFFIX = "%, "
        const val L_SUFFIX = "%"

        /**
         * Check if hue value is in range [0,360].
         * @param h - hue value to be checked
         * @return boolean if value is in range
         */
        fun inRangeH(h: Int): Boolean {
            return h >= H_MIN && h <= H_MAX
        }

        /**
         * Check if saturation value is in range [0,100].
         * @param s - saturation value to be checked
         * @return boolean if value is in range
         */
        fun inRangeS(s: Int): Boolean {
            return s >= S_MIN && s <= S_MAX
        }

        /**
         * Check if cyan value is in range [0,100].
         * @param l - lightness value to be checked
         * @return boolean if value is in range
         */
        fun inRangeL(l: Int): Boolean {
            return l >= L_MIN && l <= L_MAX
        }
    }
}