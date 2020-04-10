package com.slaviboy.colorpicker

import com.slaviboy.colorpicker.converter.ColorConverter
import com.slaviboy.colorpicker.models.HSL
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class ExampleUnitTest {

    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun colorConverterTest() {

        val converter = ColorConverter(121,234, 5, 13)

        // RGBA(red, green, blue, alpha)
        assertEquals(121, converter.r)
        assertEquals(234, converter.g)
        assertEquals(5, converter.getB(ColorConverter.MODEL_RGBA))
        assertEquals(13, converter.a)

        // HSL(hue, saturation, lightness)
        assertEquals(90, converter.h)
        assertEquals(96, converter.getS(ColorConverter.MODEL_HSL))
        assertEquals(47, converter.l)

        // HSV(hue, saturation, value)
        assertEquals(90, converter.h)
        assertEquals(98, converter.getS(ColorConverter.MODEL_HSV))
        assertEquals(92, converter.v)

        // HWB(hue, white, black)
        assertEquals(90, converter.h)
        assertEquals(2, converter.w)
        assertEquals(8, converter.getB(ColorConverter.MODEL_HWB))

        // CMYK(cyan, magenta, yellow, black)
        assertEquals(48, converter.c)
        assertEquals(0, converter.m)
        assertEquals(98, converter.y)
        assertEquals(8, converter.k)

        // hex
        assertEquals("#79ea05".toUpperCase(), converter.HEX)

        // check string values
        assertEquals("121, 234, 5", converter.getRGB())
        assertEquals("121, 234, 5, 13", converter.getRGBA())
        assertEquals("90°, 96%, 47%", converter.getHSL())
        assertEquals("90°, 98%, 92%", converter.getHSV())
        assertEquals("90°, 2, 8", converter.getHWB())
        assertEquals("48%, 0%, 98%, 8%", converter.getCMYK())
        assertEquals("RGBA(121, 234, 5, 13), HSL(90°, 96%, 47%), HSV(90°, 98%, 92%), HWB(90°, 2, 8), CMYK(48%, 0%, 98%, 8%), HEX(#79EA05)", converter.toString())

        // suffix test
        converter.setSuffix(ColorConverter.MODEL_HSV, "* ", "& ", "#")
        assertEquals("90* 98& 92#", converter.getHSV())

        // set single value
        converter.h = 180
        assertEquals("RGBA(5, 235, 235, 13), HSL(180°, 96%, 47%), HSV(180* 98& 92#), HWB(180°, 2, 8), CMYK(98%, 0%, 0%, 8%), HEX(#05EBEB)", converter.toString())

        // set color model values
        converter.setCMYK(31, 22, 1, 55)
        assertEquals("RGBA(79, 90, 114, 13), HSL(221°, 18%, 38%), HSV(221* 31& 45#), HWB(221°, 31, 55), CMYK(31%, 22%, 1%, 55%), HEX(#4F5A72)", converter.toString())

        // set color model object
        val hsl = HSL(242, 19, 41)
        converter.setHSL(hsl)
        assertEquals("RGBA(86, 85, 124, 13), HSL(242°, 19%, 41%), HSV(242* 32& 49#), HWB(242°, 33, 51), CMYK(31%, 31%, 0%, 51%), HEX(#56557C)", converter.toString())

    }



}