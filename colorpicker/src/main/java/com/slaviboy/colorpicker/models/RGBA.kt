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
 * Class that represents RGBA(RED, GREEN, BLUE and ALPHA) color model
 * and hold individual value for given color. This is the only model that
 * holds the alpha value, so when needed its is get from here.
 * @param colorConverter color converter that is used to update the other color models
 */
class RGBA(val colorConverter: ColorConverter) {

    // red [0,255]
    var r: Int = 0
        set(value) {
            field = value
            colorConverter.convert(ColorConverter.MODEL_RGBA)
        }

    // green [0,255]
    var g: Int = 0
        set(value) {
            field = value
            colorConverter.convert(ColorConverter.MODEL_RGBA)
        }

    // blue [0,255]
    var b: Int = 0
        set(value) {
            field = value
            colorConverter.convert(ColorConverter.MODEL_RGBA)
        }

    // alpha [0,255], used only in RGBA model thus its not used in the conversion
    var a: Int = 255

    // suffix for each value, used when toString method is returned
    var rSuffix: String = R_SUFFIX
    var gSuffix: String = G_SUFFIX
    var bSuffix: String = B_SUFFIX
    var aSuffix: String = A_SUFFIX

    /**
     * Constructor that set values using r, g, b and a values.
     * @param colorConverter color converter that is used to update the other color models
     * @param r red [0,255]
     * @param g green [0,255]
     * @param b blue [0,255]
     * @param a alpha [0,255]
     */
    constructor(colorConverter: ColorConverter, r: Int, g: Int, b: Int, a: Int) : this(colorConverter) {
        setRGBA(r, g, b, a)
    }

    /**
     * Constructor that set values using RGBA object.
     * @param colorConverter color converter that is used to update the other color models
     * @param rgba rgba object
     */
    constructor(colorConverter: ColorConverter, rgba: RGBA) : this(colorConverter, rgba.r, rgba.g, rgba.b, rgba.a)

    /**
     * Public setter that sets initial values using RGBA object.
     * @param rgba existing rgba object
     */
    fun setRGBA(rgba: RGBA) {
        setRGBA(rgba.r, rgba.g, rgba.b, rgba.a)
    }

    /**
     * Public setter that sets initial values using integer representation
     * @param rgba existing rgba object
     */
    fun setRGBA(color: Int) {

        val r = ColorConverter.red(color)      // red   [0-255]
        val g = ColorConverter.green(color)    // green [0-255]
        val b = ColorConverter.blue(color)     // blue  [0-255]
        val a = ColorConverter.alpha(color)    // alpha [0-255]

        setRGBA(r, g, b, a)
    }

    /**
     * Public setter that sets RGBA object using individual values.
     * @param r red [0-255]
     * @param g green [0-255]
     * @param b blue [0-255]
     * @param a alpha [0-255]
     */
    fun setRGBA(r: Int = this.r, g: Int = this.g, b: Int = this.b, a: Int = this.a) {

        val isConvertModeLast = colorConverter.isConvertMode

        // do not convert models for each set value separately
        colorConverter.isConvertMode = false

        this.r = r
        this.g = g
        this.b = b
        this.a = a

        // update after all values are set
        colorConverter.isConvertMode = isConvertModeLast
        colorConverter.convert(ColorConverter.MODEL_RGBA)
    }

    /**
     * Get the current alpha channel value in range between [0,100]
     */
    fun alphaInRangeToHundred(): Int {
        return (a * (100f / 255f)).toInt()
    }

    /**
     * Set suffix for each value, separately.
     * @param suffixes red, green, blue and alpha suffix
     */
    fun setSuffix(vararg suffixes: String = arrayOf(this.rSuffix, this.gSuffix, this.bSuffix, this.aSuffix)) {
        if (suffixes.size >= 0) {
            this.rSuffix = suffixes[0]
        }
        if (suffixes.size >= 1) {
            this.gSuffix = suffixes[1]
        }
        if (suffixes.size >= 2) {
            this.bSuffix = suffixes[2]
        }
        if (suffixes.size >= 3) {
            this.aSuffix = suffixes[3]
        }
    }

    /**
     * Get RGBA values as an array.
     * @return array with corresponding values
     */
    fun getArray(): IntArray {
        return intArrayOf(r, g, b, a)
    }

    /**
     * Get integer representation of the color
     */
    fun getInt(): Int {
        return ColorConverter.RGBAtoColor(r, g, b, a)
    }

    override fun toString(): String {
        return getString()
    }

    /**
     * Return string, with all corresponding value, where you can specify whether or not to
     * use suffix after each value.
     * @param includeAlpha if alpha value should be included in the string
     * @param withSuffix flag showing if suffix should be used
     */
    fun getString(includeAlpha: Boolean = true, withSuffix: Boolean = true): String {
        return if (includeAlpha) {
            if (withSuffix) {
                "$r$rSuffix$g$gSuffix$b$bSuffix$a$aSuffix"
            } else {
                "$r $g $b $a"
            }
        } else {
            if (withSuffix) {

                // if default suffix is used do not add it at the end
                if (bSuffix == B_SUFFIX) {
                    "$r$rSuffix$g$gSuffix$b"
                } else {
                    "$r$rSuffix$g$gSuffix$b$bSuffix"
                }
            } else {
                "$r $g $b"
            }
        }
    }

    companion object {

        // max and min values for each variable
        const val R_MIN = 0
        const val R_MAX = 255
        const val G_MIN = 0
        const val G_MAX = 255
        const val B_MIN = 0
        const val B_MAX = 255
        const val A_MIN = 0
        const val A_MAX = 255

        // default suffix for each variable, when returning string
        const val R_SUFFIX = ", "
        const val G_SUFFIX = ", "
        const val B_SUFFIX = ", "
        const val A_SUFFIX = ""

        /**
         * Check if red value is in range [0,255].
         * @param r red value to be checked
         * @return boolean if value is in range
         */
        fun inRangeR(r: Int): Boolean {
            return r >= R_MIN && r <= R_MAX
        }

        /**
         * Check if green value is in range [0,255].
         * @param g green value to be checked
         * @return boolean if value is in range
         */
        fun inRangeG(g: Int): Boolean {
            return g >= G_MIN && g <= G_MAX
        }

        /**
         * Check if blue value is in range [0,255].
         * @param b blue value to be checked
         * @return boolean if value is in range
         */
        fun inRangeB(b: Int): Boolean {
            return b >= B_MIN && b <= B_MAX
        }

        /**
         * Check if alpha value is in range [0,255].
         * @param a alpha value to be checked
         * @return boolean if value is in range
         */
        fun inRangeA(a: Int): Boolean {
            return a >= A_MIN && a <= A_MAX
        }
    }
}