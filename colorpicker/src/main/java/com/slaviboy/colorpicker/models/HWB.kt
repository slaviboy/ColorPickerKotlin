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
package com.slaviboy.colorpicker.models

import com.slaviboy.colorpicker.main.ColorConverter

/**
 * Class that represents HWB(HUE, WHITE and BLACK) color model
 * and hold individual value for given color.
 * @param colorConverter color converter that is used to update the other color models
 */
class HWB(var colorConverter: ColorConverter) {

    // hue [0,360]
    var h: Int = 0
        set(value) {
            field = value
            colorConverter.convert(ColorConverter.MODEL_HWB)
        }

    // white [0,100]
    var w: Int = 0
        set(value) {
            field = value
            colorConverter.convert(ColorConverter.MODEL_HWB)
        }

    // black [0,100]
    var b: Int = 0
        set(value) {
            field = value
            colorConverter.convert(ColorConverter.MODEL_HWB)
        }

    var hSuffix: String = H_SUFFIX
    var wSuffix: String = W_SUFFIX
    var bSuffix: String = B_SUFFIX

    /**
     * Constructor that set values using HWB values.
     * @param colorConverter color converter that is used to update the other color models
     * @param h hue [0,360]
     * @param w white [0,100]
     * @param b black [0,100]
     */
    constructor(colorConverter: ColorConverter, h: Int = 0, w: Int = 0, b: Int = 0) : this(colorConverter) {
        setHWB(h, w, b)
    }

    /**
     * Constructor that set values using HWB object.
     * @param colorConverter color converter that is used to update the other color models
     * @param hwb hwb object
     */
    constructor(colorConverter: ColorConverter, hwb: HWB) : this(colorConverter, hwb.h, hwb.w, hwb.b)

    /**
     * Public setter that sets initial values using HWB object.
     * @param hwb existing hwb object
     */
    fun setHWB(hwb: HWB) {
        setHWB(hwb.h, hwb.w, hwb.b)
    }

    /**
     * Public setter that sets HWB object using individual values.
     * @param h hue
     * @param w white
     * @param b black
     */
    fun setHWB(h: Int = this.h, w: Int = this.w, b: Int = this.b) {

        // do not convert models for each set value separately
        colorConverter.isConvertMode = false

        this.h = h
        this.w = w
        this.b = b

        // update after all values are set
        colorConverter.isConvertMode = true
        colorConverter.convert(ColorConverter.MODEL_HWB)
    }

    /**
     * Set suffix for each value, separately.
     * @param suffixes hue, saturation and value suffix
     */
    fun setSuffix(vararg suffixes: String = arrayOf(this.hSuffix, this.wSuffix, this.bSuffix)) {
        if (suffixes.size >= 0) {
            this.hSuffix = suffixes[0]
        }
        if (suffixes.size >= 1) {
            this.wSuffix = suffixes[1]
        }
        if (suffixes.size >= 2) {
            this.bSuffix = suffixes[2]
        }
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
        const val W_SUFFIX = "%, "
        const val B_SUFFIX = "%"

        /**
         * Check if hue value is in range [0,360].
         * @param h hue value to be checked
         * @return boolean if value is in range
         */
        fun inRangeH(h: Int): Boolean {
            return h >= H_MIN && h <= H_MAX
        }

        /**
         * Check if white value is in range [0,100].
         * @param w white value to be checked
         * @return boolean if value is in range
         */
        fun inRangeW(w: Int): Boolean {
            return w >= W_MIN && w <= W_MAX
        }

        /**
         * Check if black value is in range [0,100].
         * @param b black value to be checked
         * @return boolean if value is in range
         */
        fun inRangeB(b: Int): Boolean {
            return b >= B_MIN && b <= B_MAX
        }
    }
}