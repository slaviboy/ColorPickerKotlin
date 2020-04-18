package com.slaviboy.colorpicker.models

import com.slaviboy.colorpicker.converter.ColorConverter

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
 * Class that represents HSV(HUE, SATURATION and VALUE) color model
 * and hold individual value for given color.
 * @param colorConverter color converter that is used to update the other color models
 */
class HSV(var colorConverter: ColorConverter) {

    // hue [0,360]
    var h: Int = 0
        set(value) {
            field = value
            colorConverter.convert(ColorConverter.MODEL_HSV)
        }

    // saturation [0,100]
    var s: Int = 0
        set(value) {
            field = value
            colorConverter.convert(ColorConverter.MODEL_HSV)
        }

    // value [0,100]
    var v: Int = 0
        set(value) {
            field = value
            colorConverter.convert(ColorConverter.MODEL_HSV)
        }

    var hSuffix = H_SUFFIX
    var sSuffix = S_SUFFIX
    var vSuffix = V_SUFFIX

    /**
     * Constructor that set values using HSV values.
     * @param colorConverter color converter that is used to update the other color models
     * @param h hue [0,360]
     * @param s saturation [0,100]
     * @param v value [0,100]
     */
    constructor(colorConverter: ColorConverter, h: Int = 0, s: Int = 0, v: Int = 0) : this(colorConverter) {
        setHSV(h, s, v)
    }

    /**
     * Constructor that set values using HSV object.
     * @param colorConverter color converter that is used to update the other color models
     * @param hsv hsv object
     */
    constructor(colorConverter: ColorConverter, hsv: HSV) : this(colorConverter, hsv.h, hsv.s, hsv.v)

    /**
     * Public setter that sets initial values using HSV object.
     * @param hsv existing hsv object
     */
    fun setHSV(hsv: HSV) {
        setHSV(hsv.h, hsv.s, hsv.v)
    }

    /**
     * Public setter that sets HSV object using individual values.
     * @param h hue
     * @param s saturation
     * @param v value
     */
    fun setHSV(h: Int = this.h, s: Int= this.s, v: Int= this.v) {

        // do not convert models for each set value separately
        colorConverter.isConvertMode = false

        this.h = h
        this.s = s
        this.v = v

        // update after all values are set
        colorConverter.isConvertMode = true
        colorConverter.convert(ColorConverter.MODEL_HSV)
    }

    /**
     * Set suffix for each value, separately.
     * @param hSuffix hue suffix
     * @param sSuffix saturation suffix
     * @param vSuffix value suffix
     */
    fun setSuffix(hSuffix: String, sSuffix: String, vSuffix: String) {
        this.hSuffix = hSuffix
        this.sSuffix = sSuffix
        this.vSuffix = vSuffix
    }

    /**
     * Get HSV values as an array.
     * @return array with corresponding values
     */
    fun getArray(): IntArray {
        return intArrayOf(h, s, v)
    }

    override fun toString(): String {
        return getString()
    }

    /**
     * Return string, with all corresponding value, where you can specify whether or not to
     * use suffix after each value.
     * @param withSuffix flag showing if suffix should be used
     */
    fun getString(withSuffix: Boolean = true): String {
        return if (withSuffix) {
            "$h$hSuffix$s$sSuffix$v$vSuffix"
        } else {
            "$h $s $v"
        }
    }

    companion object {

        // max and min range values for each variable
        const val H_MIN = 0
        const val H_MAX = 360
        const val S_MIN = 0
        const val S_MAX = 100
        const val V_MIN = 0
        const val V_MAX = 100

        // default suffix for each variable, when returning string
        const val H_SUFFIX = "°, "
        const val S_SUFFIX = "%, "
        const val V_SUFFIX = "%"

        /**
         * Check if hue value is in range [0,360].
         * @param h hue value to be checked
         * @return boolean if value is in range
         */
        fun inRangeH(h: Int): Boolean {
            return h >= H_MIN && h <= H_MAX
        }

        /**
         * Check if saturation value is in range [0,100].
         * @param s saturation value to be checked
         * @return boolean if value is in range
         */
        fun inRangeS(s: Int): Boolean {
            return s >= S_MIN && s <= S_MAX
        }

        /**
         * Check if 'value' value is in range [0,100].
         * @param v 'value' value to be checked
         * @return boolean if value is in range
         */
        fun inRangeV(v: Int): Boolean {
            return v >= V_MIN && v <= V_MAX
        }
    }
}