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
 * Class that hold HEX(HEXADECIMAL) representation for a given color, both as hex string
 * and as integer representation for the current color.
 *
 * @param hexString hex string in format #RRGGBB
 */
class HEX(var hexString: String = "#000000") {

    var hex = Color.parseColor(hexString)

    /**
     * Constructor that set values using hex object.
     * @param hex - existing hexadecimal object
     */
    constructor(hex: HEX) : this(hex.hexString)

    /**
     * Set hex values using existing hex object.
     * @param hex
     */
    fun setHEX(hex: HEX) {
        this.hex = hex.hex
        this.hexString = hex.hexString
    }

    /**
     * Set hex values using given hexadecimal string.
     * @param hex - hex string in formats: #RRGGBB
     */
    fun setHEX(hex: String) {
        this.hex = Color.parseColor(hex) // get integer representation
        this.hexString = hex
    }

    override fun toString(): String {
        return hexString
    }

    companion object {

        /**
         * Check if hex string values is correct, by removing all non-hexadecimal character and
         * check for the new string length.
         * @param hex - hex string to be checked
         * @return - boolean flag showing if hex string is correct
         */
        fun isHEX(hex: String): Boolean {
            val newHEX = hex.replace("[^a-f0-9A-F]+".toRegex(), "")
            return newHEX.length == 6
        }
    }
}