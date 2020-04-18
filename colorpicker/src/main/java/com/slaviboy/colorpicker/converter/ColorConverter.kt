package com.slaviboy.colorpicker.converter

import android.graphics.Color
import com.slaviboy.colorpicker.models.*
import kotlin.math.roundToInt

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
 * ColorConverter class is responsible for the conversion from one color model to another
 * (by default all color models are used in the conversion). There are also public static
 * methods that can convert color model to color-int(integer representation of color).
 */
class ColorConverter {

    var rgba: RGBA   // rgba(red, green, blue, alpha) model object
    var cmyk: CMYK   // cmyk(cyan, magenta, yellow, black) model object
    var hsl: HSL     // hsl(hue, saturation, lightness) model object
    var hsv: HSV     // hsv(hue, saturation, value) model object
    var hwb: HWB     // hwb(hue, white, black) model object
    var hex: HEX     // hex(hexadecimal) model object

    private lateinit var onConvertListener:   // listener with method that is called when, conversion for all used models is done
            OnConvertListener

    var isConvertMode: Boolean = true

    // show which model will be used in the conversion from one model to another
    private val usedModels =
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
     * @param r red [0-255]
     * @param g green [0-255]
     * @param b blue [0-255]
     * @param a alpha [0-100]
     * @param h hue [0-360]
     * @param s saturation [0-100]
     * @param v value [0-100]
     * @param l lightness [0-100]
     * @param c cyan [0-100]
     * @param m magenta [0-100]
     * @param s yellow [0-100]
     * @param k black [0-100]
     */
    constructor(
        r: Int = -1, g: Int = -1, b: Int = -1, a: Int = -1, h: Int = -1, s: Int = -1, v: Int = -1,
        l: Int = -1, c: Int = -1, m: Int = -1, y: Int = -1, k: Int = -1
    ) {

        if (r != -1 && g != -1 && b != -1 && a != -1) {
            rgba.setRGBA(r, g, b, a)
        } else if (r != -1 && g != -1 && b != -1) {
            rgba.setRGBA(r, g, b)
        } else if (h != -1 && s != -1 && v != -1) {
            hsv.setHSV(h, s, v)
        } else if (h != -1 && s != -1 && l != -1) {
            hsl.setHSL(h, s, l)
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
     * Constructor that sets r, g and b values for current selected color
     * @param r red [0-255]
     * @param g green [0-255]
     * @param b blue [0-255]
     */
    constructor(r: Int = 0, g: Int = 0, b: Int = 0) {
        rgba.setRGBA(r, g, b)
    }

    /**
     * Constructor that sets r, g, b and a values for current selected color
     * @param r red [0-255]
     * @param g green [0-255]
     * @param b blue [0-255]
     * @param a alpha [0-100]
     */
    constructor(r: Int, g: Int, b: Int, a: Int) {
        rgba.setRGBA(r, g, b, a)
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
     */
    constructor(hsv: HSV) {
        hsv.setHSV(hsv)
    }

    /**
     * Constructor that sets HSL object for current selected color.
     * @param hsl HSL object
     */
    constructor(hsl: HSL) {
        hsl.setHSL(hsl)
    }

    /**
     * Constructor that sets HWB object for current selected color.
     * @param hwb HWB object
     */
    constructor(hwb: HWB) {
        hwb.setHWB(hwb)
    }

    /**
     * Constructor that sets CMYK object for current selected color.
     * @param cmyk CMYK object
     */
    constructor(cmyk: CMYK) {
        cmyk.setCMYK(cmyk)
    }

    /**
     * Constructor that sets HEX object for current selected color.
     * @param hex HEX
     */
    constructor(hex: HEX) {
        this.hex.hexString = hex.hexString
    }

    /**
     * Constructor that sets hex string value for current selected color
     * @param hexString hexadecimal string value in format #RRGGBB or #AARRGGBB
     */
    constructor(hexString: String) {
        this.hex.hexString = hexString
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
    private fun setColorModel(model: Any) {

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
    private fun getModelType(model: Any): Int {
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
    private fun HSVtoHSL() {
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
    private fun HSLtoHSV() {
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
     * Do not use the alpha value from the HEX!!!
     */
    private fun HEXtoRGB() {
        val r = Color.red(hex.color)
        val g = Color.green(hex.color)
        val b = Color.blue(hex.color)

        // set alpha [0-100]
        val a = (Color.alpha(hex.color) * (100 / 255f)).toInt()

        rgba.setRGBA(r, g, b, a)
    }

    /**
     * Convert HSV to RGB for current object.
     */
    private fun HSVtoRGB() {
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
    private fun HWBtoRGB() {
        var w = hwb.w / 100.0
        var b = hwb.b / 100.0

        // get base color
        val rgb = HSLtoColor(hwb.h, 100, 50)
        var r = Color.red(rgb) / 255.0
        var g = Color.green(rgb) / 255.0
        var bl = Color.blue(rgb) / 255.0
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
    private fun HSLtoRGB() {
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
    private fun CMYKtoRGB() {
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
    private fun RGBtoHEX() {
        if (!usedModels[MODEL_HEX]) {
            return
        }
        val hex = String.format(
            "%02x%02x%02x",
            rgba.r,
            rgba.g,
            rgba.b
        ).toUpperCase()
        this.hex.hexString = "#$hex"
    }

    /**
     * Convert RGB to HSV for current object.
     */
    private fun RGBtoHSV() {
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
    private fun RGBtoHSL() {
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
    private fun RGBtoHWB() {
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
    private fun RGBtoCMYK() {
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
        for (i in usedModels.indices) {
            usedModels[i] = false
        }
    }

    /**
     * Set suffixes that will be attached, to multiple values color model,
     * when toString() method is called and the string value is returned.
     * @param model color model
     * @param suffixes suffixes that will be attach directly to each value
     */
    fun setSuffix(model: Int, vararg suffixes: String) {
        if (suffixes.size == 3) {
            when (model) {
                MODEL_RGB -> {
                    rgba.setSuffix(suffixes[0], suffixes[1], suffixes[2])
                }
                MODEL_HSV -> {
                    hsv.setSuffix(suffixes[0], suffixes[1], suffixes[2])
                }
                MODEL_HSL -> {
                    hsl.setSuffix(suffixes[0], suffixes[1], suffixes[2])
                }
                MODEL_HWB -> {
                    hwb.setSuffix(suffixes[0], suffixes[1], suffixes[2])
                }
            }
        } else if (suffixes.size == 4) {
            if (model == MODEL_RGBA) {
                rgba.setSuffix(
                    suffixes[0],
                    suffixes[1],
                    suffixes[2],
                    suffixes[3]
                )
            } else if (model == MODEL_CMYK) {
                cmyk.setSuffix(
                    suffixes[0],
                    suffixes[1],
                    suffixes[2],
                    suffixes[3]
                )
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

    /**
     * Set OnConvertListener listener, that will be triggered once a conversion
     * is made, from one color to another.
     */
    fun setOnConvertListener(onConvertListener: OnConvertListener) {
        this.onConvertListener = onConvertListener
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

        /**
         * Static method hue to r, g, and b separately used by HSLtoColor and HSLtoRGB methods.
         */
        private fun HUEtoRGB(p: Double, q: Double, t: Double): Double {
            var t = t
            if (t < 0.0) t += 1.0
            if (t > 1.0) t -= 1.0
            if (t < 1.0 / 6.0) return p + (q - p) * 6.0 * t
            if (t < 1.0 / 2.0) return q
            return if (t < 2.0 / 3.0) p + (q - p) * (2.0 / 3.0 - t) * 6.0 else p
        }

        /**
         * Static method convert RGB to color represented as integer value.
         * @param r red
         * @param g green
         * @param b blue
         * @return
         */
        fun RGBtoColor(r: Int, g: Int, b: Int): Int {
            return RGBAtoColor(r, g, b, 100)
        }

        /**
         * Static method convert RGBA to color represented as integer value.
         * @param r red
         * @param g green
         * @param b blue
         * @param a alpha
         * @return color-int (integer representation)
         */
        fun RGBAtoColor(r: Int, g: Int, b: Int, a: Int): Int {
            return Color.argb(
                (255.0 * a / 100).roundToInt(),
                r, g, b
            )
        }

        /**
         * Static method convert HSV to color represented as integer value.
         * @param h hue
         * @param s saturation
         * @param v value
         * @return color-int (integer representation)
         */
        fun HSVtoColor(h: Int, s: Int, v: Int): Int {
            return HSVAtoColor(h, s, v, 100)
        }

        /**
         * Static method convert HSVA to color represented as integer value.
         * @param h hue
         * @param s saturation
         * @param v value
         * @param a alpha
         * @return color-int (integer representation)
         */
        fun HSVAtoColor(h: Int, s: Int, v: Int, a: Int): Int {
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
            return Color.argb(
                (255.0 * a / 100).roundToInt(),
                (r * 255).roundToInt(),
                (g * 255).roundToInt(),
                (b * 255).roundToInt()
            )
        }

        /**
         * Static method convert HSL to color represented as integer value.
         * @param h hue
         * @param s saturation
         * @param l lightness
         * @return color-int (integer representation)
         */
        fun HSLtoColor(h: Int, s: Int, l: Int): Int {
            return HSLAtoColor(h, s, l, 100)
        }

        /**
         * Static method convert HSLA to color represented as integer value.
         * @param h hue
         * @param s saturation
         * @param l lightness
         * @param a alpha
         * @return color-int (integer representation)
         */
        fun HSLAtoColor(h: Int, s: Int, l: Int, a: Int): Int {
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
            return Color.argb(
                (255.0 * a / 100).roundToInt(),
                (r * 255).roundToInt(),
                (g * 255).roundToInt(),
                (b * 255).roundToInt()
            )
        }

        /**
         * Static method convert HWB to color represented as integer value.
         * @param h hue
         * @param w white
         * @param b black
         * @return color-int (integer representation)
         */
        fun HWBtoColor(h: Int, w: Int, b: Int): Int {
            return HWBAtoColor(h, w, b, 100)
        }

        /**
         * Static method convert HWBA to color represented as integer value.
         * @param h hue
         * @param w white
         * @param b black
         * @param a alpha
         * @return color-int (integer representation)
         */
        fun HWBAtoColor(h: Int, w: Int, b: Int, a: Int): Int {
            var w2 = w / 100.0
            var b2 = b / 100.0

            // get base color
            val rgb = HSLtoColor(h, 100, 50)
            var r = Color.red(rgb) / 255.0
            var g = Color.green(rgb) / 255.0
            var bl = Color.blue(rgb) / 255.0
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

            return Color.argb(
                (255.0 * a / 100).roundToInt(),
                (r * 255).roundToInt(),
                (g * 255).roundToInt(),
                (bl * 255).roundToInt()
            )
        }

        /**
         * Static method convert CMYK to color represented as integer value.
         * @param c cyan
         * @param m magenta
         * @param y yellow
         * @param k black
         * @return color-int (integer representation)
         */
        fun CMYKtoColor(c: Int, m: Int, y: Int, k: Int): Int {
            return CMYKAtoColor(c, m, y, k, 100)
        }

        /**
         * Static method convert CMYKA to color represented as integer value.
         * @param c cyan
         * @param m magenta
         * @param y yellow
         * @param k black
         * @param a alpha
         * @return color-int (integer representation)
         */
        fun CMYKAtoColor(c: Int, m: Int, y: Int, k: Int, a: Int): Int {
            val c2 = c / 100.0
            val m2 = m / 100.0
            val y2 = y / 100.0
            val k2 = k / 100.0
            val r = 1 - Math.min(1.0, c2 * (1 - k2) + k2)
            val g = 1 - Math.min(1.0, m2 * (1 - k2) + k2)
            val b = 1 - Math.min(1.0, y2 * (1 - k2) + k2)
            return Color.argb(
                (255.0 * a / 100).roundToInt(),
                (r * 255).roundToInt(),
                (g * 255).roundToInt(),
                (b * 255).roundToInt()
            )
        }

        /**
         * Static method convert RGB to Hue represented as integer value.
         * @param r red
         * @param g green
         * @param b blue
         * @return hue value
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