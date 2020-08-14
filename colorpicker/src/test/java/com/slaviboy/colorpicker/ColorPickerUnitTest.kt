package com.slaviboy.colorpicker

import com.google.common.truth.Truth.assertThat
import com.slaviboy.colorpicker.data.ColorHolder
import com.slaviboy.colorpicker.data.CornerRadius
import com.slaviboy.colorpicker.data.Range
import com.slaviboy.colorpicker.main.ColorConverter
import com.slaviboy.colorpicker.models.*
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*

/**
 * Unit test of the following classes:
 *    # RGBA, HSV, HSL, HWM, CMYK, HEX
 *    # ColorConverter
 *    # CornerRadius
 *    # ColorHolder
 *    # Range
 */
class ColorPickerUnitTest {

    @Test
    fun MainTest() {

        // check the color model classes
        ColorModels_Test()

        // check color converter class
        ColorConverter_Test()

        // check the corner radius class
        CornerRadius_Test()

        // check color holder
        ColorHolder_Test()

        // check range
        Range_Test()
    }

    /**
     * Check the Range class methods and fields for the expected values
     */
    fun Range_Test() {

        var range = Range(0f, 100f)
        Range_Values(range, 0f, 100f, 0f)

        // [0,400] => [0f,100f], 0f = 0f
        range.setCurrent(400f, 0f)
        assertThat(range.current).isEqualTo(0f)

        // [0,400] => [0f,100f], 200f = 50f
        range.setCurrent(400f, 200f)
        assertThat(range.current).isEqualTo(50f)

        // [0,400] => [0f,100f], 400f = 100f
        range.setCurrent(400f, 400f)
        assertThat(range.current).isEqualTo(100f)

        // [0,400] => [-100,100], 400f = 100f
        range = Range(-100f, 100f)
        range.setCurrent(100f, 20f)
        assertThat(range.current).isEqualTo(-60f)

        range = Range(0f, 100f, 20f)
        var currentInNewRange = range.getCurrent(0f, 100f)
        assertThat(currentInNewRange).isEqualTo(20f)

        currentInNewRange = range.getCurrent(-100f, 100f)
        assertThat(currentInNewRange).isEqualTo(-60f)

        currentInNewRange = range.getCurrent(-100f, 0f)
        assertThat(currentInNewRange).isEqualTo(-80f)

        currentInNewRange = range.getCurrent(0f, -100f)
        assertThat(currentInNewRange).isEqualTo(-20f)

        currentInNewRange = range.getCurrent(10f, 20f)
        assertThat(currentInNewRange).isEqualTo(12f)
    }

    /**
     * Check the range values for there expected values
     * @param range current range object
     * @param lower expected lower value for the range
     * @param upper expected upper value for the range
     * @param current expected current values in the range
     */
    fun Range_Values(range: Range, lower: Float, upper: Float, current: Float) {
        assertThat(range.current).isEqualTo(current)
        assertThat(range.lower).isEqualTo(lower)
        assertThat(range.upper).isEqualTo(upper)
        assertThat(range.toString()).isEqualTo("lower: $lower, upper: $upper, current: $current")
    }

    /**
     * Check the ColorHolder class, that holds the values for the color used
     * to visualize the color picker views.
     */
    fun ColorHolder_Test() {
        val colorHolder = ColorHolder()
        val colorConverter = ColorConverter(43, 146, 213, 22)
        colorConverter.onConvertListener = colorHolder

        // change color converter color
        colorConverter.hsv = HSV(colorConverter, 205, 54, 71)

        // check the values for the color
        assertThat(colorHolder.selectedColor).isEqualTo(-13921578)
        assertThat(colorHolder.selectedColorTransparent).isEqualTo(2855638)
        assertThat(colorHolder.baseColor).isEqualTo(-16737793)
        assertThat(colorHolder.baseColorTransparent).isEqualTo(39423)
    }

    /**
     * Check methods and fields for the CornerRadius class
     */
    fun CornerRadius_Test() {

        val cornerRadius = CornerRadius(30f, 10f, 33f, 83f)
        CornerRadius_Values(cornerRadius, 30f, 10f, 33f, 83f)

        // check setter
        cornerRadius.setCornerRadius(53f, 62f, 14f, 51f)
        CornerRadius_Values(cornerRadius, 53f, 62f, 14f, 51f)

        // check add values separately to each of the four radii
        cornerRadius.add(10f, 5f, 3f, 13f)
        CornerRadius_Values(cornerRadius, 63f, 67f, 17f, 64f)

        // check add value to all four radii
        cornerRadius.addAll(7f)
        CornerRadius_Values(cornerRadius, 70f, 74f, 24f, 71f)

        // check subtract value to all four radii
        cornerRadius.subtractAll(7f)
        CornerRadius_Values(cornerRadius, 63f, 67f, 17f, 64f)

        // check add values separately to each of the four radii
        cornerRadius.subtract(10f, 5f, 3f, 13f)
        CornerRadius_Values(cornerRadius, 53f, 62f, 14f, 51f)
    }

    /**
     * Check the expected values for the class CornerRadius
     * @param cornerRadius object of the CornerRadius class
     * @param upperLeft upper left radius
     * @param upperRight upper right radius
     * @param lowerLeft lower left radius
     * @param lowerRight lower right radius
     */
    fun CornerRadius_Values(cornerRadius: CornerRadius, upperLeft: Float, upperRight: Float, lowerLeft: Float, lowerRight: Float) {
        assertThat(cornerRadius.upperLeft).isEqualTo(upperLeft)
        assertThat(cornerRadius.upperRight).isEqualTo(upperRight)
        assertThat(cornerRadius.lowerLeft).isEqualTo(lowerLeft)
        assertThat(cornerRadius.lowerRight).isEqualTo(lowerRight)
        assertThat(cornerRadius.toString()).isEqualTo("upperLeft: $upperLeft, upperRight: $upperRight, lowerLeft: $lowerLeft, lowerRight: $lowerRight")
    }

    /**
     * Check the color model classes
     */
    fun ColorModels_Test() {

        // check color model classes
        RGBA_Test()
        HEX_Test()
        HSV_Test()
        HSL_Test()
        HWB_Test()
        CMYK_Test()
    }

    /**
     * Check the RGBA class, methods and fields for expected values.
     */
    fun RGBA_Test() {

        val colorConverter = ColorConverter()
        val rgba = RGBA(colorConverter, 83, 140, 181, 31)

        // check initial rgba values
        checkRGBA(rgba, 83, 140, 181, 31, ColorConverter.RGBAtoColor(83, 140, 181, 31))

        // change values and check changes
        rgba.setRGBA(55, 66, 21, 9)
        checkRGBA(rgba, 55, 66, 21, 9, ColorConverter.RGBAtoColor(55, 66, 21, 9))
    }

    /**
     * Check the HEX class, methods and fields for expected values.
     */
    fun HEX_Test() {

        val colorConverter = ColorConverter()
        val hex = HEX(colorConverter, "#538CB51F")

        // check initial values
        checkHEX(hex, "#538CB5", "#538CB51F", ColorConverter.parseColor("#1F538CB5"))

        // change values and check changes
        hex.setHexString("#AF00FF")
        checkHEX(hex, "#AF00FF", "#AF00FF1F", 531562751)

        // don`t use previous alpha value
        hex.usePreviousAlphaValue = false
        hex.setHexString("#AF00FF")
        checkHEX(hex, "#AF00FF", "#AF00FFFF", -5308161)
    }

    /**
     * Check the HSV class, methods and fields for expected values.
     */
    fun HSV_Test() {

        val colorConverter = ColorConverter()
        val hsv = HSV(colorConverter, 205, 54, 71)

        // check initial values
        checkHSV(hsv, 205, 54, 71)

        // change all values and check changes
        hsv.setHSV(32, 53, 63)
        checkHSV(hsv, 32, 53, 63)

        // change values one by one and check changes
        hsv.h = 205
        hsv.s = 54
        hsv.v = 71
        checkHSV(hsv, 205, 54, 71)
    }

    /**
     * Check the HSL class, methods and fields for expected values.
     */
    fun HSL_Test() {

        val colorConverter = ColorConverter()
        val hsl = HSL(colorConverter, 205, 40, 52)

        // check initial values
        checkHSL(hsl, 205, 40, 52)

        // change all values and check changes
        hsl.setHSL(32, 53, 63)
        checkHSL(hsl, 32, 53, 63)

        // change values one by one and check changes
        hsl.h = 205
        hsl.s = 40
        hsl.l = 52
        checkHSL(hsl, 205, 40, 52)
    }

    /**
     * Check the HWB class, methods and fields for expected values.
     */
    fun HWB_Test() {

        val colorConverter = ColorConverter()
        val hwb = HWB(colorConverter, 205, 33, 29)

        // check initial hex values
        checkHWB(hwb, 205, 33, 29)

        // change all values and check changes
        hwb.setHWB(32, 53, 63)
        checkHWB(hwb, 32, 53, 63)

        // change values one by one and check changes
        hwb.h = 205
        hwb.w = 33
        hwb.b = 29
        checkHWB(hwb, 205, 33, 29)
    }

    /**
     * Check the CMYK class, methods and fields for expected values.
     */
    fun CMYK_Test() {

        val colorConverter = ColorConverter()
        val cmyk = CMYK(colorConverter, 54, 23, 0, 29)

        // check initial values
        checkCMYK(cmyk, 54, 23, 0, 29)

        // change all values and check changes
        cmyk.setCMYK(52, 38, 91, 44)
        checkCMYK(cmyk, 52, 38, 91, 44)

        // change values one by one and check changes
        cmyk.c = 54
        cmyk.m = 23
        cmyk.y = 0
        cmyk.k = 29
        checkCMYK(cmyk, 54, 23, 0, 29)
    }

    /**
     * Check the RGBA class for expected values
     * @param rgba RGBA object
     * @param r red value [0,255]
     * @param g green value [0,255]
     * @param b blue value [0,255]
     * @param a alpha value [0,255]
     * @param color integer representation of the color
     */
    fun checkRGBA(rgba: RGBA, r: Int, g: Int, b: Int, a: Int, color: Int) {

        assertThat(rgba.r).isEqualTo(r)
        assertThat(rgba.g).isEqualTo(g)
        assertThat(rgba.b).isEqualTo(b)
        assertThat(rgba.a).isEqualTo(a)
        assertThat(rgba.getArray()).isEqualTo(intArrayOf(r, g, b, a))
        assertThat(rgba.getInt()).isEqualTo(color)
        assertThat(rgba.getString(true, true)).isEqualTo("$r, $g, $b, $a")
        assertThat(rgba.getString(false, false)).isEqualTo("$r $g $b")
    }

    /**
     * Check the HEX class for expected values
     * @param hex HEX object
     * @param hexString expected hexadecimal string #RRGGBB
     * @param hexStringAlpha expected hexadecimal string with alpha channel #RRGGBBAA
     */
    fun checkHEX(hex: HEX, hexString: String, hexStringAlpha: String, color: Int) {
        assertThat(hex.toString(false)).isEqualTo(hexString)
        assertThat(hex.toString(true)).isEqualTo(hexStringAlpha)
        assertThat(hex.getColor(true)).isEqualTo(color)
    }

    /**
     * Check the HSV class for expected values
     * @param hsv HSV object
     * @param h hue value [0,360]
     * @param s saturation value [0,100]
     * @param v 'value' value [0,100]
     */
    fun checkHSV(hsv: HSV, h: Int, s: Int, v: Int) {
        assertThat(hsv.h).isEqualTo(h)
        assertThat(hsv.s).isEqualTo(s)
        assertThat(hsv.v).isEqualTo(v)
        assertThat(hsv.getArray()).isEqualTo(intArrayOf(h, s, v))
        assertThat(hsv.getString(true)).isEqualTo("$h°, $s%, $v%")
        assertThat(hsv.getString(false)).isEqualTo("$h $s $v")
    }

    /**
     * Check the HSL class for expected values
     * @param hsl HSL object
     * @param h hue value [0,360]
     * @param s saturation value [0,100]
     * @param l lightness value [0,100]
     */
    fun checkHSL(hsl: HSL, h: Int, s: Int, l: Int) {
        assertThat(hsl.h).isEqualTo(h)
        assertThat(hsl.s).isEqualTo(s)
        assertThat(hsl.l).isEqualTo(l)
        assertThat(hsl.getArray()).isEqualTo(intArrayOf(h, s, l))
        assertThat(hsl.getString(true)).isEqualTo("$h°, $s%, $l%")
        assertThat(hsl.getString(false)).isEqualTo("$h $s $l")
    }

    /**
     * Check the HWB class for expected values
     * @param hwb HWB object
     * @param h hue value [0,360]
     * @param w white value [0,100]
     * @param b black value [0,100]
     */
    fun checkHWB(hwb: HWB, h: Int, w: Int, b: Int) {
        assertThat(hwb.h).isEqualTo(h)
        assertThat(hwb.w).isEqualTo(w)
        assertThat(hwb.b).isEqualTo(b)
        assertThat(hwb.getArray()).isEqualTo(intArrayOf(h, w, b))
        assertThat(hwb.getString(true)).isEqualTo("$h°, $w%, $b%")
        assertThat(hwb.getString(false)).isEqualTo("$h $w $b")
    }

    /**
     * Check the CMYK class for expected values
     * @param cmyk CMYK object
     * @param c cyan value [0,100]
     * @param m magenta value [0,100]
     * @param y yellow value [0,100]
     * @param k black value [0,100]
     */
    fun checkCMYK(cmyk: CMYK, c: Int, m: Int, y: Int, k: Int) {
        assertThat(cmyk.c).isEqualTo(c)
        assertThat(cmyk.m).isEqualTo(m)
        assertThat(cmyk.y).isEqualTo(y)
        assertThat(cmyk.k).isEqualTo(k)
        assertThat(cmyk.getArray()).isEqualTo(intArrayOf(c, m, y, k))
        assertThat(cmyk.getString(true)).isEqualTo("$c%, $m%, $y%, $k%")
        assertThat(cmyk.getString(false)).isEqualTo("$c $m $y $k")
    }

    /**
     * Test the color converter if it converts the correct values
     * from RGBA to the other color models and vice versa.
     */
    fun ColorConverter_Test() {

        // color holder for hex, rgba, hsv,
        val colorModelHolder_HEX_RGBA_HSV = ColorModelHolder(
            "#538CB5", "#538CB51F",
            83, 140, 181, 31,
            205, 54, 71,
            205, 40, 52,
            205, 33, 29,
            54, 23, 0, 29,
            525569205
        )

        // color holder for the hsl
        val colorModelHolder_HSL = ColorModelHolder(
            "#548DB6", "#548DB61F",
            84, 141, 182, 31,
            205, 54, 71,
            205, 40, 52,
            205, 33, 29,
            54, 23, 0, 29,
            525634998
        )

        // color holder for the hwb
        val colorModelHolder_HWB = ColorModelHolder(
            "#548DB5", "#548DB51F",
            84, 141, 181, 31,
            205, 54, 71,
            205, 40, 52,
            205, 33, 29,
            54, 22, 0, 29,
            525634997
        )

        // color holder for the cmyk
        val colorModelHolder_CMYK = ColorModelHolder(
            "#538BB5", "#538BB51F",
            83, 139, 181, 31,
            206, 54, 71,
            206, 40, 52,
            206, 33, 29,
            54, 23, 0, 29,
            525568949
        )

        // test all static methods
        ColorConverterStaticMethods_Test()

        // create converter using hex value
        var colorConverter = ColorConverter(colorModelHolder_HEX_RGBA_HSV.hexAlpha)
        ColorConverterValues_Test(colorConverter, colorModelHolder_HEX_RGBA_HSV, "^", "#", "@", "*")

        // create converter using rgba values
        colorConverter = ColorConverter(r = 83, g = 140, b = 181, a = 31)
        ColorConverterValues_Test(colorConverter, colorModelHolder_HEX_RGBA_HSV, "^", "#", "@", "*")

        // create converter using hsv values
        colorConverter = ColorConverter(h = 205, s = 54, v = 71, a = 31)
        ColorConverterValues_Test(colorConverter, colorModelHolder_HEX_RGBA_HSV, "^", "#", "@", "*")

        // create converter using hsl values
        colorConverter = ColorConverter(h = 205, s = 40, l = 52, a = 31)
        ColorConverterValues_Test(colorConverter, colorModelHolder_HSL, "^", "#", "@", "*")

        // create converter using hwb values
        colorConverter = ColorConverter(h = 205, w = 33, b = 29, a = 31)
        ColorConverterValues_Test(colorConverter, colorModelHolder_HWB, "^", "#", "@", "*")

        // create converter using cmyk values
        colorConverter = ColorConverter(c = 54, m = 23, y = 0, k = 29, a = 31)
        ColorConverterValues_Test(colorConverter, colorModelHolder_CMYK, "^", "#", "@", "*")

        ColorConverterListener_Test(colorConverter)
    }

    /**
     * Test the color model holder value with those that are expected from
     * the color converter.
     * @param colorConverter color converter where the converted values are hold
     * @param holder holder with the color model values that are expected to match those from the color converter
     * @param suffix expected suffix, when all color values are returned as single string
     */
    fun ColorConverterValues_Test(colorConverter: ColorConverter, holder: ColorModelHolder, vararg suffix: String) {

        val s1 = suffix[0]
        val s2 = suffix[1]
        val s3 = suffix[2]

        // check initial values
        checkHEX(colorConverter.hex, holder.hex, holder.hexAlpha, holder.color)
        checkRGBA(colorConverter.rgba, holder.rgba_r, holder.rgba_g, holder.rgba_b, holder.rgba_a, holder.color)
        checkHSV(colorConverter.hsv, holder.hsv_h, holder.hsv_s, holder.hsv_v)
        checkHSL(colorConverter.hsl, holder.hsl_h, holder.hsl_s, holder.hsl_l)
        checkHWB(colorConverter.hwb, holder.hwb_h, holder.hwb_w, holder.hwb_b)
        checkCMYK(colorConverter.cmyk, holder.cmyk_c, holder.cmyk_m, holder.cmyk_y, holder.cmyk_k)

        // check suffix values
        colorConverter.setSuffix(ColorConverter.MODEL_RGB, *suffix)
        assertThat(colorConverter.rgba.getString(false, true)).isEqualTo("${holder.rgba_r}$s1${holder.rgba_g}$s2${holder.rgba_b}$s3")

        colorConverter.setSuffix(ColorConverter.MODEL_RGBA, *suffix)
        assertThat(colorConverter.rgba.getString(true, true)).isEqualTo("${holder.rgba_r}$s1${holder.rgba_g}$s2${holder.rgba_b}$s3${holder.rgba_a}${suffix[3]}")

        colorConverter.setSuffix(ColorConverter.MODEL_HSV, *suffix)
        assertThat(colorConverter.hsv.getString(true)).isEqualTo("${holder.hsv_h}$s1${holder.hsv_s}$s2${holder.hsv_v}$s3")

        colorConverter.setSuffix(ColorConverter.MODEL_HSL, *suffix)
        assertThat(colorConverter.hsl.getString(true)).isEqualTo("${holder.hsl_h}$s1${holder.hsl_s}$s2${holder.hsl_l}$s3")

        colorConverter.setSuffix(ColorConverter.MODEL_HWB, *suffix)
        assertThat(colorConverter.hwb.getString(true)).isEqualTo("${holder.hwb_h}$s1${holder.hwb_w}$s2${holder.hwb_b}$s3")

        colorConverter.setSuffix(ColorConverter.MODEL_CMYK, *suffix)
        assertThat(colorConverter.cmyk.getString(true)).isEqualTo("${holder.cmyk_c}$s1${holder.cmyk_m}$s2${holder.cmyk_y}$s3${holder.cmyk_k}${suffix[3]}")
    }

    /**
     * Test the color converter listener, if it is called correctly after setting values
     * using different color models.
     */
    fun ColorConverterListener_Test(colorConverter: ColorConverter) {

        // create mock listener and attach it to the converter
        val listener = mock(ColorConverter.OnConvertListener::class.java)
        colorConverter.onConvertListener = listener

        // update converter values using different types of color models - rgba in this case
        colorConverter.rgba = RGBA(colorConverter, 34, 51, 62, 1)

        // expect 1 call of the listener after the rgba value was changed
        verify(listener, times(1)).onConvert(colorConverter)
    }

    /**
     * Test the static methods, for ColorConverter class to check for proper return values.
     */
    fun ColorConverterStaticMethods_Test() {

        val colorRGBA = ColorConverter.RGBAtoColor(83, 140, 181, 31)
        assertThat(colorRGBA).isEqualTo(525569205)

        // get all four channels
        val r = ColorConverter.red(colorRGBA)
        val g = ColorConverter.green(colorRGBA)
        val b = ColorConverter.blue(colorRGBA)
        val a = ColorConverter.alpha(colorRGBA)
        assertThat(r).isEqualTo(83)
        assertThat(g).isEqualTo(140)
        assertThat(b).isEqualTo(181)
        assertThat(a).isEqualTo(31)

        // uppercase hex
        var hex = ColorConverter.RGBAtoHEXA(83, 140, 181)
        assertThat(hex).isEqualTo("#538CB5")

        // lowercase hex
        hex = ColorConverter.RGBAtoHEXA(r = 83, g = 140, b = 181, isUpperCase = false)
        assertThat(hex).isEqualTo("#538cb5")

        // hex with alpha, alpha is after R,G,B
        val hexAlpha = ColorConverter.RGBAtoHEXA(83, 140, 181, 31)
        assertThat(hexAlpha).isEqualTo("#538CB51F")

        // hexadecimal to integer representation
        var colorHEX = ColorConverter.HEXtoColor("#538CB5")
        assertThat(colorHEX).isEqualTo(-11301707)

        // HSV to integer representation
        var colorHSV = ColorConverter.HSVAtoColor(205, 54, 71)
        assertThat(colorHSV).isEqualTo(-11301707)

        // AHEX to integer representation, alpha channel is before R,G,B
        colorHEX = ColorConverter.HEXtoColor("#1F538CB5")
        assertThat(colorHEX).isEqualTo(525569205)

        // HSV with alpha to integer representation
        colorHSV = ColorConverter.HSVAtoColor(205, 54, 71, 31)
        assertThat(colorHSV).isEqualTo(525569205)

        // HSL with alpha to integer representation
        val colorHSL = ColorConverter.HSLAtoColor(205, 40, 52, 31)
        assertThat(colorHSL).isEqualTo(525634998)

        // HWB with alpha to integer representation
        val colorHWB = ColorConverter.HWBAtoColor(205, 33, 29, 31)
        assertThat(colorHWB).isEqualTo(525634997)

        // CMYK with alpha to integer representation
        val colorCMYK = ColorConverter.CMYKAtoColor(54, 23, 0, 29, 31)
        assertThat(colorCMYK).isEqualTo(525568949)

        val h = ColorConverter.RGBtoH(83, 140, 181)
        assertThat(h).isEqualTo(205)
    }
}

/**
 * Class that holds the color model value, that are expected during the unit testing.
 * @param hex hexadecimal string in format #RRGGBB
 * @param hexAlpha hexadecimal string with alpha channel in format ##RRGGBBAA
 * @param rgba_r red value of a RGBA
 * @param rgba_g green value of a RGBA
 * @param rgba_b blue value of a RGBA
 * @param rgba_a alpha value of a RGBA
 * @param hsv_h hue value of a HSV
 * @param hsv_s saturation value of a HSV
 * @param hsv_v 'value' value of a HSV
 * @param hsl_h hue value of a HSL
 * @param hsl_s saturation value of a HSL
 * @param hsl_l lightness value of a HSL
 * @param hwb_h hue value of a HWB
 * @param hwb_w white value of a HWB
 * @param hwb_b black value of a HWB
 * @param cmyk_c cyan value of a CMYK
 * @param cmyk_m magenta value of a CMYK
 * @param cmyk_y yellow value of a CMYK
 * @param cmyk_k cyan value of a CMYK
 * @param color integer representation of the color that includes the alpha channel
 * @param rgb red, green, blue expected string
 * @param rgba red, green, blue, alpha expected string
 * @param hsv hue, saturation, value expected string
 * @param hsl hue, saturation, lightness expected string
 * @param hwb hue, white, black expected string
 * @param cmyk cyan, magenta, yellow, black expected string
 */
class ColorModelHolder(
    var hex: String, var hexAlpha: String,
    var rgba_r: Int, var rgba_g: Int, var rgba_b: Int, var rgba_a: Int,
    var hsv_h: Int, var hsv_s: Int, var hsv_v: Int,
    var hsl_h: Int, var hsl_s: Int, var hsl_l: Int,
    var hwb_h: Int, var hwb_w: Int, var hwb_b: Int,
    var cmyk_c: Int, var cmyk_m: Int, var cmyk_y: Int, var cmyk_k: Int,
    var color: Int,
    var rgb: String = "",
    var rgba: String = "",
    var hsv: String = "",
    var hsl: String = "",
    var hwb: String = "",
    var cmyk: String = ""
)