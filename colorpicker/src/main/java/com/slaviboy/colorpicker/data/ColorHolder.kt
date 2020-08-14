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
package com.slaviboy.colorpicker.data

import com.slaviboy.colorpicker.main.ColorConverter

/**
 * ColorHolder class holds the base color, and the current selected color by the
 * color pickers, that have this object attached to them.
 * @param baseColor base H(Hue) color that is currently selected color
 * @param baseColorTransparent base H(Hue) color that is currently selected color, with 0 transparency
 * @param selectedColor current selected color by the color pickers
 * @param selectedColorTransparent current selected color by the color pickers with 0 transparency
 */
class ColorHolder(
    var baseColor: Int = ColorConverter.TRANSPARENT,
    var baseColorTransparent: Int = ColorConverter.TRANSPARENT,
    var selectedColor: Int = ColorConverter.TRANSPARENT,
    var selectedColorTransparent: Int = ColorConverter.TRANSPARENT
) : ColorConverter.OnConvertListener {

    /**
     * Update selected color, that is the color that is currently being selected by the color pickers
     * that use this color holder object. And base color, that is the HUE color, used by the color pickers,
     * to update the UI part.
     * @param colorConverter color convert that holds the converted color channels
     */
    override fun onConvert(colorConverter: ColorConverter) {

        // get the channels red, green, blue and hue
        val r: Int = colorConverter.rgba.r
        val g: Int = colorConverter.rgba.g
        val b: Int = colorConverter.rgba.b
        val h: Int = colorConverter.hsv.h

        // set selector fill color (color picker color)
        selectedColor = ColorConverter.RGBAtoColor(r, g, b)
        selectedColorTransparent = ColorConverter.RGBAtoColor(r, g, b, 0)

        // set base color (HUE)
        baseColor = ColorConverter.HSVAtoColor(h, 100, 100)
        baseColorTransparent = ColorConverter.HSVAtoColor(h, 100, 100, 0)
    }
}