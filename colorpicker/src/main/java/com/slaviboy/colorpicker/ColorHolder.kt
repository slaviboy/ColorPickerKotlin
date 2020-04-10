package com.slaviboy.colorpicker

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
 * ColorHolder class holds the base color, and the current selected color by the
 * color pickers, that have this object attached to them.
 * @param baseColor base H(Hue) color that is currently selected color
 * @param baseColorTransparent base H(Hue) color that is currently selected color, with 0 transparency
 * @param selectedColor current selected color by the color pickers
 * @param selectedColorTransparent current selected color by the color pickers with 0 transparency
 */
class ColorHolder(
    var baseColor: Int = 0,
    var baseColorTransparent: Int = 0,
    var selectedColor: Int = 0,
    var selectedColorTransparent: Int = 0
) : ColorConverter.OnConvertListener {

    /**
     * Update selected color, that is the color that is currently being selected by the color pickers
     * that use this color holder object. And base color, that is the HUE color, used by the color pickers,
     * to update the UI part.
     * @param colorConverter color convert that holds the converted color channels
     */
    override fun onConvert(colorConverter: ColorConverter) {

        // get the channels red, green, blue and hue
        val r: Int = colorConverter.r
        val g: Int = colorConverter.g
        val b: Int = colorConverter.getB(ColorConverter.MODEL_RGBA)
        val h: Int = colorConverter.h

        // set selector fill color (color picker color)
        selectedColor = ColorConverter.RGBtoColor(r, g, b)
        selectedColorTransparent = ColorConverter.RGBAtoColor(r, g, b, 0)

        // set base color (HUE)
        baseColor = ColorConverter.HSVtoColor(h, 100, 100)
        baseColorTransparent = ColorConverter.HSVAtoColor(h, 100, 100, 0)
    }
}