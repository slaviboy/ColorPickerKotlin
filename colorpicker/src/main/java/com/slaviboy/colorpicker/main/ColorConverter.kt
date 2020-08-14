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
package com.slaviboy.colorpicker.main

import com.slaviboy.colorpicker.models.*
import java.util.*
import kotlin.math.roundToInt

/**
 * ColorConverter class is responsible for the conversion from one color model to another
 * (by default all color models are used in the conversion). There are also public static
 * methods that can convert color model to color-int(integer representation of color).
 */
open class ColorConverter {

    var rgba: RGBA   // rgba(red, green, blue, alpha) model object
    var cmyk: CMYK   // cmyk(cyan, magenta, yellow, black) model object
    var hsl: HSL     // hsl(hue, saturation, lightness) model object
    var hsv: HSV     // hsv(hue, saturation, value) model object
    var hwb: HWB     // hwb(hue, white, black) model object
    var hex: HEX     // hex(hexadecimal) model object

    lateinit var onConvertListener: OnConvertListener  // listener with method that is called when, conversion for all used models is done

    var isConvertMode: Boolean = true

    // show which model will be used in the conversion from one model to another
    internal var usedModels: BooleanArray =
        booleanArrayOf(
            false,      // none
            true,       // rgb
            true,       // rgba
            true,       // hsv
            true,       // hsl
            true,       // hwb
            true,       // cmyk
            true        // hex
        )

    /**
     * Constructor that supports all value, from all color models RGB, RGBA, HSV, HSL and CMYK
     * and you can set only the one you need example: ColorConverter(h = 32, s = 41, v = 88)
     * @param r red [0,255]
     * @param g green [0,255]
     * @param b blue [0,255]
     * @param a alpha [0,255]
     * @param h hue [0,360]
     * @param s saturation [0,100]
     * @param v value [0,100]
     * @param l lightness [0,100]
     * @param c cyan [0,100]
     * @param m magenta [0,100]
     * @param y yellow [0,100]
     * @param k black [0,100]
     * @param w white [0,100]
     * @param b black [0,100]
     */
    constructor(
        r: Int = -1, g: Int = -1, b: Int = -1, a: Int = -1,     // rgba
        h: Int = -1, s: Int = -1, v: Int = -1,                  // hsv
        c: Int = -1, m: Int = -1, y: Int = -1, k: Int = -1,     // cmyk
        l: Int = -1,                                            // hsl
        w: Int = -1                                             // hwb
    ) {

        if (a != -1) {
            rgba.a = a
        }

        if (r != -1 && g != -1 && b != -1 && a != -1) {
            rgba.setRGBA(r, g, b, a)
        } else if (r != -1 && g != -1 && b != -1) {
            rgba.setRGBA(r, g, b)
        } else if (h != -1 && s != -1 && v != -1) {
            hsv.setHSV(h, s, v)
        } else if (h != -1 && s != -1 && l != -1) {
            hsl.setHSL(h, s, l)
        } else if (h != -1 && w != -1 && b != -1) {
            hwb.setHWB(h, w, b)
        } else if (c != -1 && m != -1 && y != -1 && k != -1) {
            cmyk.setCMYK(c, m, y, k)
        }
    }

    /**
     * Constructor that sets r, g b and a values for current selected color
     * as a integer representation.
     * @param color integer representation of a color
     */
    constructor(color: Int) {
        rgba.setRGBA(color)
    }

    /**
     * Constructor that sets RGBA object for current selected color.
     * @param rgba RGBA object
     */
    constructor(rgba: RGBA) {
        rgba.setRGBA(rgba)
    }

    /**
     * Constructor that sets HSV object for current selected color.
     * @param hsv HSV object
     * @param a alpha channel value
     */
    constructor(hsv: HSV, a: Int = -1) {
        if (a != -1) {
            this.rgba.a = a
        }
        hsv.setHSV(hsv)
    }

    /**
     * Constructor that sets HSL object for current selected color.
     * @param hsl HSL object
     * @param a alpha channel value
     */
    constructor(hsl: HSL, a: Int = -1) {
        if (a != -1) {
            this.rgba.a = a
        }
        hsl.setHSL(hsl)
    }

    /**
     * Constructor that sets HWB object for current selected color.
     * @param hwb HWB object
     * @param a alpha channel value
     */
    constructor(hwb: HWB, a: Int = -1) {
        if (a != -1) {
            this.rgba.a = a
        }
        hwb.setHWB(hwb)
    }

    /**
     * Constructor that sets CMYK object for current selected color.
     * @param cmyk CMYK object
     * @param a alpha channel value
     */
    constructor(cmyk: CMYK, a: Int = -1) {
        if (a != -1) {
            this.rgba.a = a
        }
        cmyk.setCMYK(cmyk)
    }

    /**
     * Constructor that sets HEX object for current selected color.
     * @param hex HEX
     */
    constructor(hex: HEX) {
        val hexString = hex.toString(true)
        this.hex.setHexString(hexString)
    }

    /**
     * Constructor that sets hex string value for current selected color
     * @param hexString hexadecimal string value in format #RRGGBB or #RRGGBBAA
     */
    constructor(hexString: String) {
        this.hex.setHexString(hexString)
    }

    init {
        // init color model objects
        rgba = RGBA(this)
        cmyk = CMYK(this)
        hsl = HSL(this)
        hsv = HSV(this)
        hwb = HWB(this)
        hex = HEX(this)
    }

    /**
     * Set color model object, using it instance determine which type it is and set
     * the corresponding color model.
     * @param model current color model
     */
    internal fun setColorModel(model: Any) {

        // transfer current model values, so it does not keep reference
        when (model) {
            is RGBA -> {
                rgba.setRGBA(model)
            }
            is HSV -> {
                hsv.setHSV(model)
            }
            is HSL -> {
                hsl.setHSL(model)
            }
            is HWB -> {
                hwb.setHWB(model)
            }
            is HEX -> {
                hex.setHEX(model)
            }
            is CMYK -> {
                cmyk.setCMYK(model)
            }
        }

        // convert to other models
        val modelType = getModelType(model)
        convert(modelType)
    }

    /**
     * Get color model integer representation, determine by the instance it supposed to be representing.
     * @param model object model
     * @return integer representation for the model type
     */
    internal fun getModelType(model: Any): Int {
        when (model) {
            is RGBA -> {
                return MODEL_RGBA
            }
            is HSV -> {
                return MODEL_HSV
            }
            is HSL -> {
                return MODEL_HSL
            }
            is HWB -> {
                return MODEL_HWB
            }
            is HEX -> {
                return MODEL_HEX
            }
            is CMYK -> {
                return MODEL_CMYK
            }
            else -> return MODEL_NONE
        }
    }

    /**
     * Convert current color model into all used color models, those are all color model
     * that are needed, and set by the user (by default all model types are made available).
     * @param currentModelType current model type that will be converted to the other models
     */
    fun convert(currentModelType: Int) {

        if (!isConvertMode) {
            return
        }

        val isConvertModeLast = isConvertMode

        // disable conversion mode since it is already in, to prevent infinite loop
        isConvertMode = false

        when (currentModelType) {
            MODEL_RGB, MODEL_RGBA -> {
                RGBtoCMYK()
                RGBtoHEX()
                RGBtoHSV()
                RGBtoHSL()
                RGBtoHWB()
            }
            MODEL_HSV -> {
                HSVtoRGB()
                RGBtoCMYK()
                RGBtoHEX()
                RGBtoHWB()
                HSVtoHSL()
            }
            MODEL_HSL -> {
                HSLtoRGB()
                RGBtoCMYK()
                RGBtoHEX()
                RGBtoHWB()
                HSLtoHSV()
            }
            MODEL_HWB -> {
                HWBtoRGB()
                RGBtoCMYK()
                RGBtoHEX()
                RGBtoHSV()
                RGBtoHSL()
            }
            MODEL_CMYK -> {
                CMYKtoRGB()
                RGBtoHEX()
                RGBtoHSV()
                RGBtoHSL()
                RGBtoHWB()
            }
            MODEL_HEX -> {
                HEXtoRGB()
                RGBtoCMYK()
                RGBtoHSV()
                RGBtoHSL()
                RGBtoHWB()
            }
        }

        // call showing that update was made
        if (::onConvertListener.isInitialized) {
            onConvertListener.onConvert(this)
        }

        // restore
        isConvertMode = isConvertModeLast
    }

    /**
     * Convert HSV to HSL for current object.
     */
    internal fun HSVtoHSL() {
        val s = hsv.s / 100.0
        val v = hsv.v / 100.0
        val hslL = (2.0 - s) * v / 2.0

        val hslS = if (hslL != 0.0) {
            when {
                hslL == 1.0 -> {
                    0.0
                }
                hslL < 0.5 -> {
                    s * v / (hslL * 2.0)
                }
                else -> {
                    s * v / (2.0 - hslL * 2.0)
                }
            }
        } else {
            0.0
        }

        // normalize
        hsl.h = hsv.h
        hsl.s = (hslS * 100.0).roundToInt()
        hsl.l = (hslL * 100.0).roundToInt()
    }

    /**
     * Convert HSL to HSV for current object.
     */
    internal fun HSLtoHSV() {
        if (!usedModels[MODEL_HSV]) {
            return
        }
        val s = hsl.s / 100.0
        val l = hsl.l / 100.0
        val t = s * if (l < 0.5) l else 1 - l
        val hsvV = l + t
        val hsvS = (if (l > 0.0) 2.0 * t / hsvV else hsv.s / 100.0).toDouble()

        // normalize
        hsv.h = hsl.h
        hsv.s = (hsvS * 100).roundToInt()
        hsv.v = (hsvV * 100).roundToInt()
    }

    /**
     * Convert HEX to RGB for current object.
     */
    internal fun HEXtoRGB() {
        rgba.setRGBA(hex.r, hex.g, hex.b)
    }

    /**
     * Convert HSV to RGB for current object.
     */
    internal fun HSVtoRGB() {
        val h = hsv.h / 360.0
        val s = hsv.s / 100.0
        val v = hsv.v / 100.0
        var r = 0.0
        var g = 0.0
        var b = 0.0
        if (s == 0.0) {
            b = v
            g = b
            r = g
        } else {
            var i = (h * 6).toInt()
            val f = h * 6 - i
            val p = v * (1 - s)
            val q = v * (1 - f * s)
            val t = v * (1 - (1 - f) * s)
            i %= 6
            when (i) {
                0 -> {
                    r = v
                    g = t
                    b = p
                }
                1 -> {
                    r = q
                    g = v
                    b = p
                }
                2 -> {
                    r = p
                    g = v
                    b = t
                }
                3 -> {
                    r = p
                    g = q
                    b = v
                }
                4 -> {
                    r = t
                    g = p
                    b = v
                }
                5 -> {
                    r = v
                    g = p
                    b = q
                }
            }
        }

        // normalize
        rgba.r = (r * 255).roundToInt()
        rgba.g = (g * 255).roundToInt()
        rgba.b = (b * 255).roundToInt()
    }

    /**
     * Convert HWB to RGB for current object.
     */
    internal fun HWBtoRGB() {
        var w = hwb.w / 100.0
        var b = hwb.b / 100.0

        // get base color
        val rgb = HSLAtoColor(hwb.h, 100, 50)
        var r = red(rgb) / 255.0
        var g = green(rgb) / 255.0
        var bl = blue(rgb) / 255.0
        val tot = w + b
        if (tot > 1) {
            w /= tot
            b /= tot
        }
        r *= 1 - w - b
        r += w
        g *= 1 - w - b
        g += w
        bl *= 1 - w - b
        bl += w

        // normalize
        rgba.r = (r * 255).roundToInt()
        rgba.g = (g * 255).roundToInt()
        rgba.b = (bl * 255).roundToInt()
    }

    /**
     * Convert HSL to RGB for current object.
     */
    internal fun HSLtoRGB() {
        val h = hsl.h / 360.0
        val s = hsl.s / 100.0
        val l = hsl.l / 100.0
        val r: Double
        val g: Double
        val b: Double
        if (s == 0.0) {
            b = l
            g = b
            r = g // achromatic
        } else {
            val q = if (l < 0.5) {
                l * (1 + s)
            } else {
                l + s - l * s
            }
            val p = 2 * l - q
            r = HUEtoRGB(p, q, h + 1.0 / 3.0)
            g = HUEtoRGB(p, q, h)
            b = HUEtoRGB(p, q, h - 1.0 / 3.0)
        }

        // normalize
        rgba.r = (r * 255).roundToInt()
        rgba.g = (g * 255).roundToInt()
        rgba.b = (b * 255).roundToInt()
    }

    /**
     * Convert CMYK to RGB for current object.
     */
    internal fun CMYKtoRGB() {
        val c = cmyk.c / 100.0
        val m = cmyk.m / 100.0
        val y = cmyk.y / 100.0
        val k = cmyk.k / 100.0
        val r = 1 - Math.min(1.0, c * (1 - k) + k)
        val g = 1 - Math.min(1.0, m * (1 - k) + k)
        val b = 1 - Math.min(1.0, y * (1 - k) + k)

        // normalize
        rgba.r = (r * 255).roundToInt()
        rgba.g = (g * 255).roundToInt()
        rgba.b = (b * 255).roundToInt()
    }

    /**
     * Convert RGB to HEX for current object.
     */
    internal fun RGBtoHEX() {
        if (!usedModels[MODEL_HEX]) {
            return
        }

        val hexString = RGBAtoHEXA(rgba.r, rgba.g, rgba.b)
        this.hex.setHexString(hexString)
    }

    /**
     * Convert RGB to HSV for current object.
     */
    internal fun RGBtoHSV() {
        if (!usedModels[MODEL_HSV]) {
            return
        }
        val r = rgba.r / 255.0
        val g = rgba.g / 255.0
        val b = rgba.b / 255.0
        val min = Math.min(Math.min(r, g), b)
        val max = Math.max(Math.max(r, g), b)
        val delta = max - min
        var h = 0.0
        val s: Double = if (max == 0.0) 0.0 else delta / max
        if (max != min) {
            if (max == r) {
                h = (g - b) / delta + if (g < b) 6.0 else 0.0
            } else if (max == g) {
                h = (b - r) / delta + 2.0
            } else if (max == b) {
                h = (r - g) / delta + 4.0
            }
            h /= 6.0
        }

        // normalize
        hsv.h = (h * 360).roundToInt()
        hsv.s = (s * 100).roundToInt()
        hsv.v = (max * 100).roundToInt()
    }

    /**
     * Convert RGB to HSL for current object.
     */
    internal fun RGBtoHSL() {
        if (!usedModels[MODEL_HSL]) {
            return
        }
        val r = rgba.r / 255.0
        val g = rgba.g / 255.0
        val b = rgba.b / 255.0
        val min = Math.min(Math.min(r, g), b)
        val max = Math.max(Math.max(r, g), b)
        val delta = max - min
        var h = 0.0
        var s = 0.0
        val l = (max + min) / 2.0
        if (max != min) {
            s = if (l > 0.5) delta / (2.0 - max - min) else delta / (max + min)
            when (max) {
                r -> {
                    h = (g - b) / delta + if (g < b) 6 else 0
                }
                g -> {
                    h = (b - r) / delta + 2
                }
                b -> {
                    h = (r - g) / delta + 4
                }
            }
            h /= 6.0
        }

        // normalize
        hsl.h = (h * 360).roundToInt()
        hsl.s = (s * 100).roundToInt()
        hsl.l = (l * 100).roundToInt()
    }

    /**
     * Convert RGB to HWB for current object.
     */
    internal fun RGBtoHWB() {
        if (!usedModels[MODEL_HWB]) {
            return
        }
        val r = rgba.r / 255.0
        val g = rgba.g / 255.0
        val b = rgba.b / 255.0
        val max = Math.max(Math.max(r, g), b)
        val min = Math.min(Math.min(r, g), b)
        var h = 0.0
        val bl = 1 - max
        val delta = max - min
        if (max != min) {
            if (max == r) {
                h = (g - b) / delta + if (g < b) 6.0 else 0.0
            } else if (max == g) {
                h = (b - r) / delta + 2.0
            } else if (max == b) {
                h = (r - g) / delta + 4.0
            }
            h /= 6.0
        }

        // normalize
        hwb.h = (h * 360).roundToInt()
        hwb.w = (min * 100).roundToInt()
        hwb.b = (bl * 100).roundToInt()
    }

    /**
     * Convert RGB to CMYK for current object.
     */
    internal fun RGBtoCMYK() {
        if (!usedModels[MODEL_CMYK]) {
            return
        }
        val r = rgba.r / 255.0
        val g = rgba.g / 255.0
        val b = rgba.b / 255.0
        var c = 0.0
        var m = 0.0
        var y = 0.0
        val k = Math.min(Math.min(1 - r, 1 - g), 1 - b)
        if (k != 1.0) {
            c = (1 - r - k) / (1 - k)
            m = (1 - g - k) / (1 - k)
            y = (1 - b - k) / (1 - k)
        }

        // normalize
        cmyk.c = (c * 100).roundToInt()
        cmyk.m = (m * 100).roundToInt()
        cmyk.y = (y * 100).roundToInt()
        cmyk.k = (k * 100).roundToInt()
    }

    /**
     * Set which color models will be updated(color model used when converted from one color model
     * to another). Models that are not included are not used in the conversion for faster performance.
     * @param models color model that are included
     */
    fun setUsedModels(vararg models: Int) {

        // set which color models to be used in the conversion
        for (i in models.indices) {
            val index = models[i]
            if (index > 0 && index < usedModels.size) {
                usedModels[index] = true
            }
        }
    }

    /**
     * Set which color model will be used in the conversion between color model, pass
     * argument as an array.
     * @param usedModels array with used color models
     */
    fun setUsedModels(usedModels: BooleanArray) {
        for (i in this.usedModels.indices) {
            if (i < usedModels.size) {
                this.usedModels[i] = usedModels[i]
            }
        }
    }

    /**
     * Clear all used color models, by setting there values to false.
     * That way they wont be used in the color conversion.
     */
    fun clearUsedModels() {
        usedModels = booleanArrayOf(
            false,       // none
            false,       // rgb
            false,       // rgba
            false,       // hsv
            false,       // hsl
            false,       // hwb
            false,       // cmyk
            false        // hex
        )
    }

    /**
     * Set suffixes that will be attached, to multiple values color model,
     * when toString() method is called and the string value is returned.
     * @param model color model
     * @param suffixes suffixes that will be attach directly to each value
     */
    fun setSuffix(model: Int, vararg suffixes: String) {

        when (model) {
            MODEL_RGB, MODEL_RGBA -> {
                rgba.setSuffix(*suffixes)
            }
            MODEL_HSV -> {
                hsv.setSuffix(*suffixes)
            }
            MODEL_HSL -> {
                hsl.setSuffix(*suffixes)
            }
            MODEL_HWB -> {
                hwb.setSuffix(*suffixes)
            }
            MODEL_CMYK -> {
                cmyk.setSuffix(*suffixes)
            }
        }
    }

    interface OnConvertListener {

        /**
         * Method called when conversion is made from a specific color to all color
         * models included in the array -usedModels, containing all models used in
         * the conversion.
         * @param colorConverter - current color converter object, from which you can get the new converted values
         */
        fun onConvert(colorConverter: ColorConverter)
    }

    override fun toString(): String {
        return "RGBA($rgba), HSL($hsl), HSV($hsv), HWB($hwb), CMYK($cmyk), HEX($hex)"
    }

    companion object {

        // supported color models used bt the class
        const val MODEL_NONE = 0
        const val MODEL_RGB = 1
        const val MODEL_RGBA = 2
        const val MODEL_HSV = 3
        const val MODEL_HSL = 4
        const val MODEL_HWB = 5
        const val MODEL_CMYK = 6
        const val MODEL_HEX = 7

        // most used colors
        val BLACK = -0x1000000
        val DKGRAY = -0xbbbbbc
        val GRAY = -0x777778
        val LTGRAY = -0x333334
        val WHITE = -0x1
        val RED = -0x10000
        val GREEN = -0xff0100
        val BLUE = -0xffff01
        val YELLOW = -0x100
        val CYAN = -0xff0001
        val MAGENTA = -0xff01
        val AQUA = 0xFF00FFFF.toInt()
        val FUCHSIA = 0xFFFF00FF.toInt()
        val LIME = 0xFF00FF00.toInt()
        val MAROON = 0xFF800000.toInt()
        val NAVY = 0xFF000080.toInt()
        val OLIVE = 0xFF808000.toInt()
        val PURPLE = 0xFF800080.toInt()
        val SILVER = 0xFFC0C0C0.toInt()
        val TEAL = 0xFF008080.toInt()
        val TRANSPARENT = 0

        var colorNameMap: HashMap<String, Int> = hashMapOf(
            "black" to BLACK,
            "darkgray" to DKGRAY,
            "gray" to GRAY,
            "lightgray" to LTGRAY,
            "white" to WHITE,
            "red" to RED,
            "green" to GREEN,
            "blue" to BLUE,
            "yellow" to YELLOW,
            "cyan" to CYAN,
            "magenta" to MAGENTA,
            "aqua" to AQUA,
            "fuchsia" to FUCHSIA,
            "darkgrey" to DKGRAY,
            "grey" to GRAY,
            "lightgrey" to LTGRAY,
            "lime" to LIME,
            "maroon" to MAROON,
            "navy" to NAVY,
            "olive" to OLIVE,
            "purple" to PURPLE,
            "silver" to SILVER,
            "teal" to TEAL
        )


        /**
         * Parse the color string, and return the corresponding color-int.
         * If the string cannot be parsed, throws an IllegalArgumentException
         * exception. Supported formats are: #RRGGBB, #AARRGGBB
         *
         * The following names are also accepted: red, blue, green, black, white,
         * gray, cyan, magenta, yellow, lightgray, darkgray, grey, lightgrey, darkgrey,
         * aqua, fuchsia, lime, maroon, navy, olive,  purple, silver, and teal.
         * @param colorString string with the color
         */
        fun parseColor(colorString: String): Int {
            if (colorString[0] == '#') {
                // Use a long to avoid rollovers on #ffXXXXXX
                var color = colorString.substring(1).toLong(16)
                if (colorString.length == 7) {
                    // Set the alpha value
                    color = color or -0x1000000
                } else require(colorString.length == 9) { "Unknown color" }
                return color.toInt()
            } else {
                val key = colorString.toLowerCase(Locale.ROOT)
                if (colorNameMap.containsKey(key)) {
                    return colorNameMap[key]!!
                }
            }
            throw IllegalArgumentException("Unknown color")
        }

        /**
         * Return the alpha component of a color int. This is the same as saying
         * color >>> 24
         */
        fun alpha(color: Int): Int {
            return color ushr 24
        }

        /**
         * Return the red component of a color int. This is the same as saying
         * (color >> 16) & 0xFF
         */
        fun red(color: Int): Int {
            return color shr 16 and 0xFF
        }

        /**
         * Return the green component of a color int. This is the same as saying
         * (color >> 8) & 0xFF
         */
        fun green(color: Int): Int {
            return color shr 8 and 0xFF
        }

        /**
         * Return the blue component of a color int. This is the same as saying
         * color & 0xFF
         */
        fun blue(color: Int): Int {
            return color and 0xFF
        }

        /**
         * Static method hue to r, g, and b separately used by HSLtoColor and HSLtoRGB methods.
         */
        internal fun HUEtoRGB(p: Double, q: Double, t: Double): Double {
            var t = t
            if (t < 0.0) t += 1.0
            if (t > 1.0) t -= 1.0
            if (t < 1.0 / 6.0) return p + (q - p) * 6.0 * t
            if (t < 1.0 / 2.0) return q
            return if (t < 2.0 / 3.0) p + (q - p) * (2.0 / 3.0 - t) * 6.0 else p
        }

        /**
         * Static method convert RGBA to its hexadecimal representation.
         * @param r red component of the color [0,255]
         * @param g green component of the color [0,255]
         * @param b blue component of the color [0,255]
         * @param a alpha component of the color [0,255]
         */
        fun RGBAtoHEXA(r: Int, g: Int, b: Int, a: Int = -1, isUpperCase: Boolean = true): String {

            // if alpha is not set use only red, green and blue channels
            val hex = if (a == -1) {
                String.format("%02x%02x%02x", r, g, b)
            } else {
                String.format("%02x%02x%02x%02x", r, g, b, a)
            }
            return "#" + if (isUpperCase) hex.toUpperCase(Locale.ROOT) else hex.toLowerCase(Locale.ROOT)
        }

        /**
         * Static method convert RGBA to color represented as integer value.
         * @param r red component of the color [0,255]
         * @param g green component of the color [0,255]
         * @param b blue component of the color [0,255]
         * @param a alpha component of the color [0,255]
         */
        fun RGBAtoColor(r: Int, g: Int, b: Int, a: Int = 255): Int {
            return a shl 24 or (r shl 16) or (g shl 8) or b
        }

        /**
         * Static method convert HEX string to color represented as integer value.
         * Here the alpha channel values is before the red, green and blues values, this
         * is the same as parse color from the native Color class.
         * @param hex hexadecimal string in the following format ##AARRGGBB, ##RRGGBB
         */
        fun HEXtoColor(hex: String): Int {
            return parseColor(hex)
        }

        /**
         * Static method convert HSVA to color represented as integer value.
         * @param h hue [0,360]
         * @param s saturation [0,100]
         * @param v value [0,100]
         * @param a alpha [0,255]
         * @return color-int (integer representation)
         */
        fun HSVAtoColor(h: Int, s: Int, v: Int, a: Int = 255): Int {
            val h2 = h / 360.0
            val s2 = s / 100.0
            val v2 = v / 100.0
            var r = 0.0
            var g = 0.0
            var b = 0.0
            if (s2 == 0.0) {
                b = v2
                g = b
                r = g
            } else {
                var i = (h2 * 6).toInt()
                val f = h2 * 6 - i
                val p = v2 * (1 - s2)
                val q = v2 * (1 - f * s2)
                val t = v2 * (1 - (1 - f) * s2)
                i %= 6
                if (i == 0) {
                    r = v2
                    g = t
                    b = p
                } else if (i == 1) {
                    r = q
                    g = v2
                    b = p
                } else if (i == 2) {
                    r = p
                    g = v2
                    b = t
                } else if (i == 3) {
                    r = p
                    g = q
                    b = v2
                } else if (i == 4) {
                    r = t
                    g = p
                    b = v2
                } else if (i == 5) {
                    r = v2
                    g = p
                    b = q
                }
            }
            return RGBAtoColor(
                (r * 255).roundToInt(),
                (g * 255).roundToInt(),
                (b * 255).roundToInt(),
                a
            )
        }

        /**
         * Static method convert HSLA to color represented as integer value.
         * @param h hue [0,360]
         * @param s saturation [0,100]
         * @param l lightness [0,100]
         * @param a alpha [0,255]
         * @return color-int (integer representation)
         */
        fun HSLAtoColor(h: Int, s: Int, l: Int, a: Int = 255): Int {
            val h2 = h / 360.0
            val s2 = s / 100.0
            val l2 = l / 100.0
            val r: Double
            val g: Double
            val b: Double
            if (s2 == 0.0) {
                b = l2
                g = b
                r = g // achromatic
            } else {
                val q = if (l2 < 0.5) l2 * (1 + s2) else l2 + s2 - l2 * s2
                val p = 2 * l2 - q
                r = HUEtoRGB(p, q, h2 + 1.0 / 3.0)
                g = HUEtoRGB(p, q, h2)
                b = HUEtoRGB(p, q, h2 - 1.0 / 3.0)
            }
            return RGBAtoColor(
                (r * 255).roundToInt(),
                (g * 255).roundToInt(),
                (b * 255).roundToInt(),
                a
            )
        }

        /**
         * Static method convert HWBA to color represented as integer value.
         * @param h hue [0,360]
         * @param w white [0,100]
         * @param b black [0,100]
         * @param a alpha [0,255]
         * @return color-int (integer representation)
         */
        fun HWBAtoColor(h: Int, w: Int, b: Int, a: Int = 255): Int {
            var w2 = w / 100.0
            var b2 = b / 100.0

            // get base color
            val rgb = HSLAtoColor(h, 100, 50)
            var r = red(rgb) / 255.0
            var g = green(rgb) / 255.0
            var bl = blue(rgb) / 255.0
            val tot = w2 + b2
            if (tot > 1) {
                w2 /= tot
                b2 /= tot
            }
            r *= 1 - w2 - b2
            r += w2
            g *= 1 - w2 - b2
            g += w2
            bl *= 1 - w2 - b2
            bl += w2

            return RGBAtoColor(
                (r * 255).roundToInt(),
                (g * 255).roundToInt(),
                (bl * 255).roundToInt(),
                a
            )
        }

        /**
         * Static method convert CMYKA to color represented as integer value.
         * @param c cyan [0,100]
         * @param m magenta [0,100]
         * @param y yellow [0,100]
         * @param k black [0,100]
         * @param a alpha [0,255]
         * @return color-int (integer representation)
         */
        fun CMYKAtoColor(c: Int, m: Int, y: Int, k: Int, a: Int = 255): Int {
            val c2 = c / 100.0
            val m2 = m / 100.0
            val y2 = y / 100.0
            val k2 = k / 100.0
            val r = 1 - Math.min(1.0, c2 * (1 - k2) + k2)
            val g = 1 - Math.min(1.0, m2 * (1 - k2) + k2)
            val b = 1 - Math.min(1.0, y2 * (1 - k2) + k2)
            return RGBAtoColor(
                (r * 255).roundToInt(),
                (g * 255).roundToInt(),
                (b * 255).roundToInt(),
                a
            )
        }

        /**
         * Static method to get the HUE from a RGB color model.
         * @param r red [0,255]
         * @param g green [0,255]
         * @param b blue [0,255]
         * @return hue value [0,360]
         */
        fun RGBtoH(r: Int, g: Int, b: Int): Int {
            val r2 = r / 255.0
            val g2 = g / 255.0
            val b2 = b / 255.0
            val min = Math.min(Math.min(r2, g2), b2)
            val max = Math.max(Math.max(r2, g2), b2)
            val delta = max - min
            var h = 0.0
            if (max != min) {
                if (max == r2) {
                    h = (g2 - b2) / delta + if (g2 < b2) 6.0 else 0.0
                } else if (max == g2) {
                    h = (b2 - r2) / delta + 2.0
                } else if (max == b2) {
                    h = (r2 - g2) / delta + 4.0
                }
                h /= 6.0
            }

            // normalize
            return (h * 360).roundToInt()
        }
    }
}