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
 * Class that represents CMYK(CYAN, MAGENTA, YELLOW and BLACK) color model
 * and hold individual value for given color.
 * @param c cyan [0,100]
 * @param m magenta [0,100]
 * @param y yellow [0,100]
 * @param k black [0,100]
 */
class CMYK(
    var c: Int = 0,
    var m: Int = 0,
    var y: Int = 0,
    var k: Int = 0
) {

    var cSuffix = C_SUFFIX
    var mSuffix = M_SUFFIX
    var ySuffix = Y_SUFFIX
    var kSuffix = K_SUFFIX

    /**
     * Constructor that set values using CMYK object.
     * @param cmyk - existing cmyk object
     */
    constructor(cmyk: CMYK) : this(cmyk.c, cmyk.m, cmyk.y, cmyk.k)

    /**
     * Public setter that sets initial values using CMYK object.
     * @param cmyk - existing cmyk object
     */
    fun setCMYK(cmyk: CMYK) {
        c = cmyk.c
        m = cmyk.m
        y = cmyk.y
        k = cmyk.k
    }

    /**
     * Public setter that sets CMYK object using individual values.
     * @param c - cyan
     * @param m - magenta
     * @param y - yellow
     * @param k - black
     */
    fun setCMYK(c: Int, m: Int, y: Int, k: Int) {
        this.c = c
        this.m = m
        this.y = y
        this.k = k
    }

    /**
     * Set suffix for each value, separately.
     * @param cSuffix - cyan suffix
     * @param mSuffix - magenta suffix
     * @param ySuffix - yellow suffix
     * @param kSuffix - black suffix
     */
    fun setSuffix(
        cSuffix: String,
        mSuffix: String,
        ySuffix: String,
        kSuffix: String
    ) {
        this.cSuffix = cSuffix
        this.mSuffix = mSuffix
        this.ySuffix = ySuffix
        this.kSuffix = kSuffix
    }

    /**
     * Get CMYK values as an array.
     * @return array with corresponding values
     */
    fun getArray(): IntArray {
        return intArrayOf(c, m, y, k)
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
            "$c$cSuffix$m$mSuffix$y$ySuffix$k$kSuffix"
        } else {
            "$c $m $y $k"
        }
    }

    companion object {

        // max and min range values for each variable
        const val C_MIN = 0
        const val C_MAX = 100
        const val M_MIN = 0
        const val M_MAX = 100
        const val Y_MIN = 0
        const val Y_MAX = 100
        const val K_MIN = 0
        const val K_MAX = 100

        // default suffix for each variable, used when returning string
        const val C_SUFFIX = "%, "
        const val M_SUFFIX = "%, "
        const val Y_SUFFIX = "%, "
        const val K_SUFFIX = "%"

        /**
         * Check if cyan value is in range [0,100].
         * @param c - cyan value to be checked
         * @return boolean if value is in range
         */
        fun inRangeC(c: Int): Boolean {
            return c >= C_MIN && c <= C_MAX
        }

        /**
         * Check if magenta value is in range [0,100].
         * @param m - magenta value to be checked
         * @return boolean if value is in range
         */
        fun inRangeM(m: Int): Boolean {
            return m >= M_MIN && m <= M_MAX
        }

        /**
         * Check if yellow value is in range [0,100].
         * @param y - yellow value to be checked
         * @return boolean if value is in range
         */
        fun inRangeY(y: Int): Boolean {
            return y >= Y_MIN && y <= Y_MAX
        }

        /**
         * Check if black value is in range [0,100].
         * @param k - black value to be checked
         * @return boolean if value is in range
         */
        fun inRangeK(k: Int): Boolean {
            return k >= K_MIN && k <= K_MAX
        }
    }
}