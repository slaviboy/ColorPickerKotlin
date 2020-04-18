package com.slaviboy.colorpicker.converter

import android.graphics.Color
import android.util.Log
import com.slaviboy.colorpicker.models.*
import java.util.*
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

    private lateinit var rgba: RGBA                             // rgba(red, green, blue, alpha) model object
    private lateinit var cmyk: CMYK                             // cmyk(cyan, magenta, yellow, black) model object
    private lateinit var hsl: HSL                               // hsl(hue, saturation, lightness) model object
    private lateinit var hsv: HSV                               // hsv(hue, saturation, value) model object
    private lateinit var hwb: HWB                               // hwb(hue, white, black) model object
    private lateinit var hex: HEX                               // hex(hexadecimal) model object
    private lateinit var onConvertListener: OnConvertListener   // listener with method that is called when, conversion for all used models is done

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
     * Constructor that sets r, g b and a values for current selected color
     * as a integer representation.
     * @param color integer representation of a color
     */
    constructor(color: Int) {
        val r = Color.red(color)
        val g = Color.green(color)
        val b = Color.blue(color)
        val a = Color.alpha(color)

        init()
        setRGBA(r, g, b, a)
    }

    /**
     * Constructor that sets r, g and b values for current selected color
     * @param r red
     * @param g green
     * @param b blue
     */
    constructor(r: Int = 0, g: Int = 0, b: Int = 0) {
        init()
        setRGB(r, g, b)
    }

    /**
     * Constructor that sets r, g, b and a values for current selected color
     * @param r red
     * @param g green
     * @param b blue
     * @param a alpha
     */
    constructor(r: Int, g: Int, b: Int, a: Int) {
        init()
        setRGBA(r, g, b, a)
    }

    /**
     * Constructor that sets RGBA object for current selected color.
     * @param rgba RGBA object
     */
    constructor(rgba: RGBA) {
        init()
        setRGBA(rgba)
    }

    /**
     * Constructor that sets HSV object for current selected color.
     * @param hsv HSV object
     */
    constructor(hsv: HSV) {
        init()
        setHSV(hsv)
    }

    /**
     * Constructor that sets HSL object for current selected color.
     * @param hsl HSL object
     */
    constructor(hsl: HSL) {
        init()
        setHSL(hsl)
    }

    /**
     * Constructor that sets HWB object for current selected color.
     * @param hwb HWB object
     */
    constructor(hwb: HWB) {
        init()
        setHWB(hwb)
    }

    /**
     * Constructor that sets CMYK object for current selected color.
     * @param cmyk CMYK object
     */
    constructor(cmyk: CMYK) {
        init()
        setCMYK(cmyk)
    }

    /**
     * Constructor that sets HEX object for current selected color.
     * @param hex HEX
     */
    constructor(hex: HEX) {
        init()
        setHEX(hex)
    }

    /**
     * Constructor that sets hex string value for current selected color
     * @param hex hexadecimal string value in format #RRGGBB
     */
    constructor(hex: String) {
        init()
        HEX = hex
    }

    /**
     * Set color model object, using it instance determine which type it is and set
     * the corresponding color model.
     * @param model current color model
     */
    private fun setColorModel(model: Any) {
        init()

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

    fun init() {

        // init color model objects
        rgba = RGBA()
        cmyk = CMYK()
        hsl = HSL()
        hsv = HSV()
        hwb = HWB()
        hex = HEX()
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
    private fun convert(currentModelType: Int) {
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
     */
    private fun HEXtoRGB() {
        if (!usedModels[MODEL_RGBA]) {
            return
        }
        val hex = hex.hex
        val r = hex shr 16 and 0xFF
        val g = hex shr 8 and 0xFF
        val b = hex shr 0 and 0xFF
        _setR(r)
        _setG(g)
        _setB(b, MODEL_RGBA)
    }

    /**
     * Convert HSV to RGB for current object.
     */
    private fun HSVtoRGB() {
        if (!usedModels[MODEL_RGBA]) {
            return
        }
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
        if (!usedModels[MODEL_RGBA]) {
            return
        }
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
        if (!usedModels[MODEL_RGBA]) {
            return
        }
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
        if (!usedModels[MODEL_RGBA]) {
            return
        }
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
        this.hex.setHEX("#$hex")
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

    /**
     * Set current color using RGBA object.
     * @param rgba rgba object
     */
    fun setRGBA(rgba: RGBA) {
        _setR(rgba.r)
        _setG(rgba.g)
        _setB(rgba.b, MODEL_RGBA)
        _setA(rgba.a)
        convert(MODEL_RGBA)
    }

    /**
     * Set current color using RGB values.
     * @param r red
     * @param g green
     * @param b blue
     */
    fun setRGB(r: Int, g: Int, b: Int) {
        _setR(r)
        _setG(g)
        _setB(b, MODEL_RGB)
        convert(MODEL_RGB)
    }

    /**
     * Set current color using RGBA values.
     * @param r red
     * @param g green
     * @param b blue
     * @param a alpha
     */
    fun setRGBA(r: Int, g: Int, b: Int, a: Int) {
        _setR(r)
        _setG(g)
        _setB(b, MODEL_RGBA)
        _setA(a)
        convert(MODEL_RGBA)
    }

    /**
     * Set current color using CMYK object.
     * @param cmyk cmyk object
     */
    fun setCMYK(cmyk: CMYK) {
        _setC(cmyk.c)
        _setM(cmyk.m)
        _setY(cmyk.y)
        _setK(cmyk.k)
        convert(MODEL_CMYK)
    }

    /**
     * Set current color using CMYK values.
     * @param c cyan
     * @param m magenta
     * @param y yellow
     * @param k black
     */
    fun setCMYK(c: Int, m: Int, y: Int, k: Int) {
        _setC(c)
        _setM(m)
        _setY(y)
        _setK(k)
        convert(MODEL_CMYK)
    }

    /**
     * Set current color using HSV values.
     * @param h hue
     * @param s saturation
     * @param v value
     */
    fun setHSV(h: Int, s: Int, v: Int) {
        _setH(h)
        _setS(s, MODEL_HSV)
        _setV(v)
        convert(MODEL_HSV)
    }

    /**
     * Set current color using HSL values.
     * @param h hue
     * @param s saturation
     * @param l lightness
     */
    fun setHSL(h: Int, s: Int, l: Int) {
        _setH(h)
        _setS(s, MODEL_HSL)
        _setL(l)
        convert(MODEL_HSL)
    }

    /**
     * Set current color using HWB values.
     * @param h hue
     * @param w white
     * @param b black
     */
    fun setHWB(h: Int, w: Int, b: Int) {
        _setH(h)
        _setW(w)
        _setB(b, MODEL_HWB)
        convert(MODEL_HWB)
    }

    /**
     * Set current color using HSV object.
     * @param hsv HSV object
     */
    fun setHSV(hsv: HSV) {
        _setH(hsv.h)
        _setS(hsv.s, MODEL_HSV)
        _setV(hsv.v)
        convert(MODEL_HSV)
    }

    /**
     * Set current color using HSL object.
     * @param hsl HSL object
     */
    fun setHSL(hsl: HSL) {
        _setH(hsl.h)
        _setS(hsl.s, MODEL_HSL)
        _setL(hsl.l)
        convert(MODEL_HSL)
    }

    /**
     * Set current color using HWB object.
     * @param hwb HWB object
     */
    fun setHWB(hwb: HWB) {
        _setH(hwb.h)
        _setW(hwb.w)
        _setB(hwb.b, MODEL_HWB)
        convert(MODEL_HWB)
    }

    /**
     * Set current color using HEX object.
     * @param hex HEX object
     */
    fun setHEX(hex: HEX) {
        _setHEX(hex.hexString)
    }

    /**
     * Get RGB, values as string.
     * @param withSuffix - include suffix, after each value
     * @return string representation
     */
    fun getRGB(withSuffix: Boolean = true): String {
        return rgba.getString(false, withSuffix)
    }

    /**
     * Get RGBA, values as string.
     * @param withSuffix - include suffix, after each value
     * @return string representation
     */
    fun getRGBA(withSuffix: Boolean = true): String {
        return rgba.getString(true, withSuffix)
    }

    /**
     * Get CMYK, values as string.
     * @param withSuffix include suffix, after each value
     * @return string representation
     */
    fun getCMYK(withSuffix: Boolean = true): String {
        return cmyk.getString(withSuffix)
    }

    /**
     * Get HSL, values as string.
     * @param withSuffix include suffix, after each value
     * @return string representation
     */
    fun getHSL(withSuffix: Boolean = true): String {
        return hsl.getString(withSuffix)
    }

    /**
     * Get HWB, values as string.
     * @param withSuffix include suffix, after each value
     * @return string representation
     */
    fun getHWB(withSuffix: Boolean = true): String {
        return hwb.getString(withSuffix)
    }

    /**
     * Get HWB, values as string.
     * @param withSuffix include suffix, after each value
     * @return string representation
     */
    fun getHSV(withSuffix: Boolean = true): String {
        return hsv.getString(withSuffix)
    }

    /**
     * Setter and getter for current color using hexadecimal string.
     * @param hex hexadecimal string
     */
    var HEX: String
        get() = hex.toString()
        set(hex) {
            _setHEX(hex)
        }

    /**
     * Setter and getter for current H(hue) component for the
     * (HSV, HSL, HWB) color models
     */
    var h: Int
        get() = hsv.h
        set(h) {
            _setH(h)
            convert(MODEL_HSV)
        }

    /**
     * Setter and getter for current R(red) component for the
     * (RGBA) color model
     */
    var r: Int
        get() = rgba.r
        set(r) {
            _setR(r)
            convert(MODEL_RGBA)
        }

    /**
     * Setter and getter for current G(green) component for the
     * (RGBA) color model
     */
    var g: Int
        get() = rgba.g
        set(g) {
            _setG(g)
            convert(MODEL_RGBA)
        }

    /**
     * Setter and getter for current A(alpha) component for the
     * (RGBA) color model
     */
    var a: Int
        get() = rgba.a
        set(a) {
            // alpha is not used in other models, so conversion is not needed
            _setA(a)
        }

    /**
     * Setter and getter for current L(lightness) component for the
     * (HSL) color model
     */
    var l: Int
        get() = hsl.l
        set(l) {
            _setL(l)
            convert(MODEL_HSL)
        }

    /**
     * Setter and getter for current C(cyan) component for the
     * (CMYK) color model
     */
    var c: Int
        get() = cmyk.c
        set(c) {
            _setC(c)
            convert(MODEL_CMYK)
        }

    /**
     * Setter and getter for current M(magenta) component for the
     * (CMYK) color model
     */
    var m: Int
        get() = cmyk.m
        set(m) {
            _setM(m)
            convert(MODEL_CMYK)
        }

    /**
     * Setter and getter for current Y(yellow) component for the
     * (CMYK) color model
     */
    var y: Int
        get() = cmyk.y
        set(y) {
            _setY(y)
            convert(MODEL_CMYK)
        }

    /**
     * Setter and getter for current B(black) component for the
     * (CMYK) color model
     */
    var k: Int
        get() = cmyk.k
        set(k) {
            _setK(k)
            convert(MODEL_CMYK)
        }

    /**
     * Setter and getter for current V(value) component for the
     * (HSV) color model
     */
    var v: Int
        get() = hsv.v
        set(v) {
            _setV(v)
            convert(MODEL_HSV)
        }

    /**
     * Setter and getter for current W(white) component for the
     * (HWB) color model
     */
    var w: Int
        get() = hwb.w
        set(w) {
            _setW(w)
            convert(MODEL_HWB)
        }

    /**
     * Setter the S(saturation) component, for either HSV or HSL color models
     * @param s saturation new value
     * @param model show from which color model the component is
     */
    fun setS(s: Int, model: Int) {
        _setS(s, model)
        if (model == MODEL_HSV) {
            convert(MODEL_HSV)
        } else if (model == MODEL_HSL) {
            convert(MODEL_HSL)
        }
    }

    /**
     * Setter the B component, for either RGBA(blue) or HWB(black) color models
     * @param b new value
     * @param model show from which color model the component is
     */
    fun setB(b: Int, model: Int) {
        _setB(b, model)
        if (model == MODEL_RGBA) {
            convert(MODEL_RGBA)
        } else if (model == MODEL_HWB) {
            convert(MODEL_HWB)
        }
    }

    /**
     * Getter the S component, for either HSV or HSL color models
     * @param model show from which color model the component is
     */
    fun getS(model: Int): Int {
        return when (model) {
            MODEL_HSV -> {
                hsv.s
            }
            MODEL_HSL -> {
                hsl.s
            }
            else -> {
                -1
            }
        }
    }

    /**
     * Getter the B component, for either RGBA(blue) or HWB(black) color models
     * @param model show from which color model the component is
     */
    fun getB(model: Int): Int {
        return when (model) {
            MODEL_RGBA -> {
                rgba.b
            }
            MODEL_HWB -> {
                hwb.b
            }
            else -> {
                -1
            }
        }
    }

    private fun _setC(c: Int) {
        var c = c
        if (c > CMYK.C_MAX) {
            c = CMYK.C_MAX
        } else if (c < CMYK.C_MIN) {
            c = CMYK.C_MIN
        }
        cmyk.c = c
    }

    private fun _setM(m: Int) {
        var m = m
        if (m > CMYK.M_MAX) {
            m = CMYK.M_MAX
        } else if (m < CMYK.M_MIN) {
            m = CMYK.M_MIN
        }
        cmyk.m = m
    }

    private fun _setY(y: Int) {
        var y = y
        if (y > CMYK.Y_MAX) {
            y = CMYK.Y_MAX
        } else if (y < CMYK.Y_MIN) {
            y = CMYK.Y_MIN
        }
        cmyk.y = y
    }

    private fun _setK(k: Int) {
        var k = k
        if (k > CMYK.K_MAX) {
            k = CMYK.K_MAX
        } else if (k < CMYK.K_MIN) {
            k = CMYK.K_MIN
        }
        cmyk.k = k
    }

    private fun _setR(r: Int) {

        // check for value in range
        var r = r
        if (r > RGBA.R_MAX) {
            r = RGBA.R_MAX
        } else if (r < RGBA.R_MIN) {
            r = RGBA.R_MIN
        }
        rgba.r = r
    }

    private fun _setG(g: Int) {

        // check for value in range
        var g = g
        if (g > RGBA.G_MAX) {
            g = RGBA.G_MAX
        } else if (g < RGBA.G_MIN) {
            g = RGBA.G_MIN
        }
        rgba.g = g
    }

    private fun _setB(b: Int, model: Int) {

        // check for value in range
        var b = b
        if (model == MODEL_RGB || model == MODEL_RGBA) {
            // blue
            if (b > RGBA.B_MAX) {
                b = RGBA.B_MAX
            } else if (b < RGBA.B_MIN) {
                b = RGBA.B_MIN
            }
            rgba.b = b
        } else if (model == MODEL_HWB) {
            //black
            if (b > HWB.B_MAX) {
                b = HWB.B_MAX
            } else if (b < HWB.B_MIN) {
                b = HWB.B_MIN
            }
            hwb.b = b
        }
    }

    private fun _setA(a: Int) {

        // check for value in range
        var a = a
        if (a > RGBA.A_MAX) {
            a = RGBA.A_MAX
        } else if (a < RGBA.A_MIN) {
            a = RGBA.A_MIN
        }
        rgba.a = a
    }

    private fun _setH(h: Int) {
        var h = h
        if (h > HSV.H_MAX) {
            h = HSV.H_MAX
        } else if (h < HSV.H_MIN) {
            h = HSV.H_MIN
        }
        hsl.h = h
        hsv.h = h
    }

    private fun _setS(s: Int, model: Int) {
        var s = s
        if (model == MODEL_HSV) {
            if (s > HSV.S_MAX) {
                s = HSV.S_MAX
            } else if (s < HSV.S_MIN) {
                s = HSV.S_MIN
            }
            hsv.s = s
        } else if (model == MODEL_HSL) {
            if (s > HSL.S_MAX) {
                s = HSL.S_MAX
            } else if (s < HSL.S_MIN) {
                s = HSL.S_MIN
            }
            hsl.s = s
        }
    }

    private fun _setV(v: Int) {
        var v = v
        if (v > HSV.V_MAX) {
            v = HSV.V_MAX
        } else if (v < HSV.V_MIN) {
            v = HSV.V_MIN
        }
        hsv.v = v
    }

    private fun _setL(l: Int) {
        var l = l
        if (l > HSL.L_MAX) {
            l = HSL.L_MAX
        } else if (l < HSL.L_MIN) {
            l = HSL.L_MIN
        }
        hsl.l = l
    }

    private fun _setW(w: Int) {
        var w = w
        if (w > HWB.W_MAX) {
            w = HWB.W_MAX
        } else if (w < HWB.W_MIN) {
            w = HWB.W_MIN
        }
        hwb.w = w
    }

    private fun _setHEX(hex: String) {
        val newHex = "#" + hex.replace("[^a-f0-9A-F]+".toRegex(), "").toUpperCase()
        if (newHex.length == 7) {
            this.hex.setHEX(hex)
            convert(MODEL_HEX)
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