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
 * Class that hold HEX(HEXADECIMAL) representation for a given color, both as hex string
 * and as integer representation for the current color.
 * @param colorConverter color converter that is used to update the other color models
 */
class HEX(var colorConverter: ColorConverter) {

    var r: Int                          // red color channel, that was set by the hex value
    var g: Int                          // green color channel, that was set by the hex value
    var b: Int                          // blue color channel, that was set by the hex value
    var usePreviousAlphaValue: Boolean  // if previous alpha should be used when getting the hex value, or use FF=255 when alpha channel is required

    init {
        r = 0
        g = 0
        b = 0
        usePreviousAlphaValue = true
    }

    /**
     * Set hexadecimal string in the following formats  #RRGGBB or #RRGGBBAA
     * @param hexString hexadecimal string values
     */
    fun setHexString(hexString: String) {

        // get r,g,b,a channels
        r = hexString.substring(1, 3).toInt(16)
        g = hexString.substring(3, 5).toInt(16)
        b = hexString.substring(5, 7).toInt(16)

        // set the alpha
        colorConverter.rgba.a = if (hexString.length == 7) {
            if (usePreviousAlphaValue) {
                colorConverter.rgba.a
            } else {
                255
            }
        } else {
            hexString.substring(7, 9).toInt(16)
        }

        colorConverter.convert(ColorConverter.MODEL_HEX)
    }

    /**
     * Constructor that set values using hex object.
     * @param colorConverter color converter that is used to update the other color models
     * @param hex hexadecimal object
     */
    constructor(colorConverter: ColorConverter, hex: HEX) : this(colorConverter, hex.toString(true))

    /**
     * Constructor that set values using hex object.
     * @param colorConverter color converter that is used to update the other color models
     * @param hexString hexadecimal string in format #RRGGBB or #RRGGBBAA
     */
    constructor(colorConverter: ColorConverter, hexString: String) : this(colorConverter) {
        setHexString(hexString)
    }

    /**
     * Set hex values using existing hex object.
     * @param hex hex object
     */
    fun setHEX(hex: HEX) {
        setHexString(hex.toString(true))
    }

    /**
     * Get the integer representation of the hex color
     * @param withAlpha if alpha channel values should be included or not when calculating the integer representation
     */
    fun getColor(withAlpha: Boolean = false): Int {
        return if (withAlpha) {
            ColorConverter.RGBAtoColor(r, g, b, colorConverter.rgba.a)
        } else {
            ColorConverter.RGBAtoColor(r, g, b)
        }
    }

    /**
     * Return the hexadecimal representation of the color
     * @param withAlpha if alpha channel values should be included or not returning the hex representation
     */
    fun toString(withAlpha: Boolean = false): String {
        return if (withAlpha) {
            ColorConverter.RGBAtoHEXA(r, g, b, colorConverter.rgba.a)
        } else {
            ColorConverter.RGBAtoHEXA(r, g, b)
        }
    }

    companion object {

        /**
         * Check if hex string values is correct, by removing all non-hexadecimal character and
         * check for the new string length.
         * @param hex hexadecimal string to be checked
         * @return whether hexadecimal string is correct
         */
        fun isHEX(hex: String): Boolean {
            val newHEX = hex.replace("[^a-f0-9A-F]+".toRegex(), "")
            return (newHEX.length == 6 || newHEX.length == 8)
        }
    }
}