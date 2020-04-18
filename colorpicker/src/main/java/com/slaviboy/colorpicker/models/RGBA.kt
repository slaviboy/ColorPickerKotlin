package com.slaviboy.colorpicker.models

import android.graphics.Color

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
 * Class that represents RGBA(RED, GREEN, BLUE and ALPHA) color model
 * and hold individual value for given color. This is the only model that
 * holds the alpha value, so when needed its is get from here.
 * @param r red [0,255]
 * @param g green [0,255]
 * @param b blue [0,255]
 * @param a alpha [0,100]
 */
class RGBA(var r: Int = 0, var g: Int = 0, var b: Int = 0, var a: Int = 100) {

    var rSuffix = R_SUFFIX
    var gSuffix = G_SUFFIX
    var bSuffix = B_SUFFIX
    var aSuffix = A_SUFFIX

    /**
     * Constructor that set values using RGBA object.
     * @param rgba - rgba object
     */
    constructor(rgba: RGBA) : this(rgba.r, rgba.g, rgba.b, rgba.a)

    /**
     * Public setter that sets initial values using RGBA object.
     * @param rgba - existing rgba object
     */
    fun setRGBA(rgba: RGBA) {
        r = rgba.r
        g = rgba.g
        b = rgba.b
        a = rgba.a
    }

    /**
     * Public setter that sets RGBA object using individual values.
     * @param r - red
     * @param g - green
     * @param b - blue
     */
    fun setRGBA(r: Int, g: Int, b: Int) {
        setRGBA(r, g, b, a)
    }

    /**
     * Public setter that sets RGBA object using individual values.
     * @param r - red
     * @param g - green
     * @param b - blue
     * @param a - alpha
     */
    fun setRGBA(r: Int, g: Int, b: Int, a: Int) {
        this.r = r
        this.g = g
        this.b = b
        this.a = a
    }

    /**
     * Set suffix for each value, separately.
     * @param rSuffix - red suffix
     * @param gSuffix - green suffix
     * @param bSuffix - blue suffix
     * @param aSuffix - alpha suffix
     */
    fun setSuffix(
        rSuffix: String,
        gSuffix: String,
        bSuffix: String,
        aSuffix: String = ""
    ) {
        setSuffix(rSuffix, gSuffix, bSuffix)
        this.aSuffix = aSuffix
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
        return Color.argb((a * (255 / 100f)).toInt(), r, g, b)
    }

    override fun toString(): String {
        return getString()
    }

    /**
     * Return string, with all corresponding value, where you can specify whether or not to
     * use suffix after each value.
     * @param includeAlpha - if alpha value should be included in the string
     * @param withSuffix - flag showing if suffix should be used
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
                "$r$rSuffix$g$gSuffix$b"
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
        const val A_MAX = 100

        // default suffix for each variable, when returning string
        const val R_SUFFIX = ", "
        const val G_SUFFIX = ", "
        const val B_SUFFIX = ", "
        const val A_SUFFIX = ""

        /**
         * Check if red value is in range [0,255].
         * @param r - red value to be checked
         * @return boolean if value is in range
         */
        fun inRangeR(r: Int): Boolean {
            return r >= R_MIN && r <= R_MAX
        }

        /**
         * Check if green value is in range [0,255].
         * @param g - green value to be checked
         * @return boolean if value is in range
         */
        fun inRangeG(g: Int): Boolean {
            return g >= G_MIN && g <= G_MAX
        }

        /**
         * Check if blue value is in range [0,255].
         * @param b - blue value to be checked
         * @return boolean if value is in range
         */
        fun inRangeB(b: Int): Boolean {
            return b >= B_MIN && b <= B_MAX
        }

        /**
         * Check if alpha value is in range [0,100].
         * @param a - alpha value to be checked
         * @return boolean if value is in range
         */
        fun inRangeA(a: Int): Boolean {
            return a >= A_MIN && a <= A_MAX
        }
    }
}