package com.slaviboy.colorpicker

import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import androidx.test.platform.app.InstrumentationRegistry
import com.google.common.truth.Truth.assertThat
import com.slaviboy.colorpicker.components.Base
import com.slaviboy.colorpicker.main.ColorConverter
import com.slaviboy.colorpicker.main.Updater
import com.slaviboy.colorpicker.module.circular.CircularHS
import com.slaviboy.colorpicker.module.rectangular.RectangularSL
import com.slaviboy.colorpicker.module.rectangular.RectangularSV
import com.slaviboy.colorpicker.module.slider.SliderA
import com.slaviboy.colorpicker.module.slider.SliderH
import com.slaviboy.colorpicker.module.slider.SliderV
import org.junit.Test
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader

/**
 * Instrumented test, that checks the cached bitmaps generated for each color window used
 * when drawing the color windows. Using the previously generated pixel data for each color
 * window and stored as text files in the 'raw' folder. The test check if expected pixel
 * data from the bitmaps matches those generated in real time, when creating the color windows.
 * Instrumented test is used because with Robolectric pixel data is not generated and empty
 * arrays are returned, that is why the emulator is needed.
 *
 * Unit test of the following classes:
 *  # SliderA
 *  # SliderH
 *  # SliderV
 *  # CircularHS
 *  # RectangularSV
 *  # RectangularSL
 */
class ColorWindowsInstrumentedTest {

    lateinit var context: Context

    // color windows
    lateinit var sliderA: SliderA
    lateinit var sliderH: SliderH
    lateinit var sliderV: SliderV
    lateinit var circularHS: CircularHS
    lateinit var rectangularSV: RectangularSV
    lateinit var rectangularSL: RectangularSL

    @Test
    fun MainTest() {

        context = InstrumentationRegistry.getInstrumentation().targetContext

        // initialize the color windows
        sliderA = SliderA(context)
        sliderV = SliderV(context)
        sliderH = SliderH(context)
        circularHS = CircularHS(context)
        rectangularSV = RectangularSV(context)
        rectangularSL = RectangularSL(context)

        ColorWindowsBitmapTest(context)
    }

    /**
     * Test the bitmaps of the color windows with the expected bitmap pixel data
     * for each color window.
     */
    fun ColorWindowsBitmapTest(context: Context) {

        val colorConverter = ColorConverter(31, 83, 140, 70)
        val updater = Updater(
            colorConverter = colorConverter,
            colorWindows = mutableListOf(
                sliderA,
                sliderV,
                sliderH,
                circularHS,
                rectangularSV,
                rectangularSL
            )
        )

        setColorWindowsSize()
        checkColorWindowsBitmaps()
        checkColorWindowsSelectorAndBorder()
    }

    /**
     * Check the bitmaps pixel data of the color windows with the expected pixel data
     * presets from the raw folder. This includes the two layer baseLayer and colorLayer
     * that are used when drawing the color window, that does not include the selector and
     * the border for the color windows.
     */
    fun checkColorWindowsBitmaps() {

        // check sliderA bitmaps
        assertThat(checkBitmap(sliderA.baseLayer, R.raw.slider_a_base_layer)).isTrue()
        assertThat(checkBitmap(sliderA.colorLayer, R.raw.slider_a_color_layer)).isTrue()

        // check slideH bitmaps
        assertThat(checkBitmap(sliderH.baseLayer, R.raw.slider_h_base_layer)).isTrue()
        assertThat(checkBitmap(sliderH.colorLayer, R.raw.slider_h_color_layer)).isTrue()

        // check slideV bitmaps (does not use the baseLayer)
        assertThat(checkBitmap(sliderV.colorLayer, R.raw.slider_v_color_layer)).isTrue()

        // check circularHS bitmaps
        assertThat(checkBitmap(circularHS.baseLayer, R.raw.circular_hs_base_layer)).isTrue()
        assertThat(checkBitmap(circularHS.colorLayer, R.raw.circular_hs_color_layer)).isTrue()

        // check rectangularSV bitmaps
        assertThat(checkBitmap(rectangularSV.baseLayer, R.raw.rectangular_sv_base_layer)).isTrue()
        assertThat(checkBitmap(rectangularSV.colorLayer, R.raw.rectangular_sv_color_layer)).isTrue()

        // check rectangularSL bitmaps
        assertThat(checkBitmap(rectangularSL.baseLayer, R.raw.rectangular_sl_base_layer)).isTrue()
        assertThat(checkBitmap(rectangularSL.colorLayer, R.raw.rectangular_sl_color_layer)).isTrue()
    }

    /**
     * Check the actual bitmap for the color windows, that includes the color layer
     * with selector and border of the color window.
     */
    fun checkColorWindowsSelectorAndBorder() {

        // bitmap and canvas 100x100 pixels
        val bitmap_100_100 = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888)
        val canvas_100_100 = Canvas(bitmap_100_100)

        // draw rectangularSV color windows with selector and border
        rectangularSV.drawOnCanvas(canvas_100_100)
        assertThat(checkBitmap(bitmap_100_100, R.raw.rectangular_sv)).isTrue()
        bitmap_100_100.eraseColor(Color.TRANSPARENT)

        // draw rectangularSL color windows with selector and border
        rectangularSL.drawOnCanvas(canvas_100_100)
        assertThat(checkBitmap(bitmap_100_100, R.raw.rectangular_sl)).isTrue()
        bitmap_100_100.eraseColor(Color.TRANSPARENT)

        // draw circularHS color windows with selector and border
        circularHS.drawOnCanvas(canvas_100_100)
        assertThat(checkBitmap(bitmap_100_100, R.raw.circular_hs)).isTrue()
        bitmap_100_100.eraseColor(Color.TRANSPARENT)

        // bitmap and canvas 100x100 pixels
        val bitmap_20_100 = Bitmap.createBitmap(20, 100, Bitmap.Config.ARGB_8888)
        val canvas_20_100 = Canvas(bitmap_20_100)

        // draw sliderA color windows with selector and border
        sliderA.drawOnCanvas(canvas_20_100)
        assertThat(checkBitmap(bitmap_20_100, R.raw.slider_a)).isTrue()
        bitmap_20_100.eraseColor(Color.TRANSPARENT)

        // draw sliderV color windows with selector and border
        sliderV.drawOnCanvas(canvas_20_100)
        assertThat(checkBitmap(bitmap_20_100, R.raw.slider_v)).isTrue()
        bitmap_20_100.eraseColor(Color.TRANSPARENT)

        // draw sliderH color windows with selector and border
        sliderH.drawOnCanvas(canvas_20_100)
        assertThat(checkBitmap(bitmap_20_100, R.raw.slider_h)).isTrue()
        bitmap_20_100.eraseColor(Color.TRANSPARENT)
    }

    /**
     * Set the size for all available types of color windows, 100x100 for circular and
     * 20x100 for the sliders.
     */
    fun setColorWindowsSize() {

        // set circular module size to 100x100
        setColorWindowSize(circularHS, 100, 100)

        // set rectangle modules size to 100x100
        setColorWindowSize(rectangularSV, 100, 100)
        setColorWindowSize(rectangularSL, 100, 100)

        // set slider modules size to 20x100
        setColorWindowSize(sliderA, 20, 100)
        setColorWindowSize(sliderH, 20, 100)
        setColorWindowSize(sliderV, 20, 100)
    }

    /**
     * Load string from the raw folder using a resource id of the given file.
     * @param resources resource from the context
     * @param resId resource id of the file
     */
    fun loadStringFromRawResource(resources: Resources, resId: Int): String {
        val rawResource = resources.openRawResource(resId)
        val content = streamToString(rawResource)
        try {
            rawResource.close()
        } catch (e: IOException) {
            throw e
        }
        return content
    }

    /**
     * Read the file from the raw folder using input stream
     */
    private fun streamToString(inputStream: InputStream): String {
        var l: String?
        val bufferedReader = BufferedReader(InputStreamReader(inputStream))
        val stringBuilder = StringBuilder()
        try {
            while (bufferedReader.readLine().also { l = it } != null) {
                stringBuilder.append(l)
            }
        } catch (e: IOException) {
        }
        return stringBuilder.toString()
    }

    /**
     * Check if bitmap pixel data matches with the expected one give as a string
     * @param bitmap bitmap with the pixel data
     * @param resId resource id of the file holding the pixel data
     */
    fun checkBitmap(bitmap: Bitmap, resId: Int): Boolean {

        // expected bitmap pixel data give as string
        val expectedBitmapData = loadStringFromRawResource(context.resources, resId)

        val width = bitmap.width
        val height = bitmap.height
        val pixelData = IntArray(width * height)
        bitmap.getPixels(pixelData, 0, width, 0, 0, width, height)

        return pixelData.joinToString(", ") == expectedBitmapData
    }

    /**
     * Set color window size, by calling the method layout() that is usually triggered when
     * the layout is inflated and size of the view is calculated.
     * @param base base color
     */
    fun setColorWindowSize(base: Base, width: Int, height: Int) {
        base.layout(0, 0, width, height)
        base.onInitBase()
        base.update()
        base.onRedraw()
    }
}