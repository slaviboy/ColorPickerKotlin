package com.slaviboy.colorpicker

import android.app.Activity
import android.graphics.PointF
import android.graphics.RectF
import android.os.Build
import android.widget.TextView
import com.google.common.truth.Truth.assertThat
import com.slaviboy.colorpicker.components.Base
import com.slaviboy.colorpicker.components.Circular
import com.slaviboy.colorpicker.components.Rectangular
import com.slaviboy.colorpicker.components.Slider
import com.slaviboy.colorpicker.data.ColorHolder
import com.slaviboy.colorpicker.data.CornerRadius
import com.slaviboy.colorpicker.data.Range
import com.slaviboy.colorpicker.main.ColorConverter
import com.slaviboy.colorpicker.main.Updater
import com.slaviboy.colorpicker.module.circular.CircularHS
import com.slaviboy.colorpicker.module.rectangular.RectangularSL
import com.slaviboy.colorpicker.module.rectangular.RectangularSV
import com.slaviboy.colorpicker.module.slider.SliderA
import com.slaviboy.colorpicker.module.slider.SliderH
import com.slaviboy.colorpicker.module.slider.SliderV
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.android.controller.ActivityController
import org.robolectric.annotation.Config
import kotlin.collections.HashMap

/**
 * Unit test of the following classes using the Robolectric framework:
 *  # Updater
 *  # SliderA
 *  # SliderH
 *  # SliderV
 *  # CircularHS
 *  # RectangularSV
 *  # RectangularSL
 */
@Config(sdk = [Build.VERSION_CODES.P])
@RunWith(RobolectricTestRunner::class)
class ColorWindowsUnitTest {

    lateinit var activityController: ActivityController<Activity>
    lateinit var activity: Activity

    // color windows
    lateinit var sliderA: SliderA
    lateinit var sliderH: SliderH
    lateinit var sliderV: SliderV
    lateinit var circularHS: CircularHS
    lateinit var rectangularSV: RectangularSV
    lateinit var rectangularSL: RectangularSL

    // text views
    lateinit var textViewRGBA: TextView
    lateinit var textViewRGB: TextView
    lateinit var textViewRGBA_R: TextView
    lateinit var textViewRGBA_G: TextView
    lateinit var textViewRGBA_B: TextView
    lateinit var textViewRGBA_A: TextView
    lateinit var textViewHSV: TextView
    lateinit var textViewHSV_H: TextView
    lateinit var textViewHSV_S: TextView
    lateinit var textViewHSV_V: TextView
    lateinit var textViewHSL: TextView
    lateinit var textViewHSL_H: TextView
    lateinit var textViewHSL_S: TextView
    lateinit var textViewHSL_L: TextView
    lateinit var textViewHWB: TextView
    lateinit var textViewHWB_H: TextView
    lateinit var textViewHWB_W: TextView
    lateinit var textViewHWB_B: TextView
    lateinit var textViewCMYK: TextView
    lateinit var textViewCMYK_C: TextView
    lateinit var textViewCMYK_M: TextView
    lateinit var textViewCMYK_Y: TextView
    lateinit var textViewCMYK_K: TextView
    lateinit var textViewHEX: TextView
    lateinit var textViewHEXA: TextView

    // color converter and updater
    lateinit var colorConverter: ColorConverter
    lateinit var updater: Updater

    // text views and color windows
    lateinit var colorWindows: MutableList<Base>
    lateinit var textViews: HashMap<TextView, Int>

    // color holder with the expected values
    val colorModelHolder = ColorModelHolder(
        "#538CB5", "#538CB51F",
        83, 140, 181, 31,
        205, 54, 71,
        205, 40, 52,
        205, 33, 29,
        54, 23, 0, 29,
        525569205,
        "83, 140, 181",
        "83, 140, 181, 31",
        "205°, 54%, 71%",
        "205°, 40%, 52%",
        "205°, 33%, 29%",
        "54%, 23%, 0%, 29%"
    )

    @Before
    fun setUp() {

        // create activity
        activityController = Robolectric.buildActivity(Activity::class.java)
        activity = activityController.get()

        // create color windows types using the context from the activity
        sliderA = SliderA(activity)
        sliderV = SliderV(activity)
        sliderH = SliderH(activity)
        circularHS = CircularHS(activity)
        rectangularSV = RectangularSV(activity)
        rectangularSL = RectangularSL(activity)

        // create text views using the context from the activity
        textViewRGBA = TextView(activity)
        textViewRGB = TextView(activity)
        textViewRGBA_R = TextView(activity)
        textViewRGBA_G = TextView(activity)
        textViewRGBA_B = TextView(activity)
        textViewRGBA_A = TextView(activity)
        textViewHSV = TextView(activity)
        textViewHSV_H = TextView(activity)
        textViewHSV_S = TextView(activity)
        textViewHSV_V = TextView(activity)
        textViewHSL = TextView(activity)
        textViewHSL_H = TextView(activity)
        textViewHSL_S = TextView(activity)
        textViewHSL_L = TextView(activity)
        textViewHWB = TextView(activity)
        textViewHWB_H = TextView(activity)
        textViewHWB_W = TextView(activity)
        textViewHWB_B = TextView(activity)
        textViewCMYK = TextView(activity)
        textViewCMYK_C = TextView(activity)
        textViewCMYK_M = TextView(activity)
        textViewCMYK_Y = TextView(activity)
        textViewCMYK_K = TextView(activity)
        textViewHEX = TextView(activity)
        textViewHEXA = TextView(activity)

        // set the color windows in a list
        colorWindows = mutableListOf(
            sliderA,
            sliderV,
            sliderH,
            circularHS,
            rectangularSV,
            rectangularSL
        )

        // set the text views in a hash map with the text views as key, and value as the type
        textViews = hashMapOf(
            textViewRGBA to Updater.TYPE_RGBA,
            textViewRGB to Updater.TYPE_RGB,
            textViewRGBA_R to Updater.TYPE_RGBA_R,
            textViewRGBA_G to Updater.TYPE_RGBA_G,
            textViewRGBA_B to Updater.TYPE_RGBA_B,
            textViewRGBA_A to Updater.TYPE_RGBA_A,
            textViewHSV to Updater.TYPE_HSV,
            textViewHSV_H to Updater.TYPE_HSV_H,
            textViewHSV_S to Updater.TYPE_HSV_S,
            textViewHSV_V to Updater.TYPE_HSV_V,
            textViewHSL to Updater.TYPE_HSL,
            textViewHSL_H to Updater.TYPE_HSL_H,
            textViewHSL_S to Updater.TYPE_HSL_S,
            textViewHSL_L to Updater.TYPE_HSL_L,
            textViewHWB to Updater.TYPE_HWB,
            textViewHWB_H to Updater.TYPE_HWB_H,
            textViewHWB_W to Updater.TYPE_HWB_W,
            textViewHWB_B to Updater.TYPE_HWB_B,
            textViewCMYK to Updater.TYPE_CMYK,
            textViewCMYK_C to Updater.TYPE_CMYK_C,
            textViewCMYK_M to Updater.TYPE_CMYK_M,
            textViewCMYK_Y to Updater.TYPE_CMYK_Y,
            textViewCMYK_K to Updater.TYPE_CMYK_K,
            textViewHEX to Updater.TYPE_HEX,
            textViewHEXA to Updater.TYPE_HEXA
        )
    }

    @Test
    fun MainTest() {

        UpdaterTest()
        SliderColorWindowsTest()
        RectangularColorWindowsTest()
        CircularColorWindowsTest()
    }

    /**
     * Test the slider color windows, that includes the sliders: SliderA, SliderH and SliderV
     */
    fun SliderColorWindowsTest() {

        // initialize sliderH
        colorConverter = ColorConverter(0, 255, 255)
        updater = Updater(colorConverter = colorConverter, colorWindows = mutableListOf(sliderH))
        setColorWindowSize(sliderH, 20, 100)

        // sliderH
        val valuesSliderH = SliderValueHolder(
            788529152,
            1f,
            -1,
            6f,
            -16777216,
            0f,
            10f,
            10f,
            50f,
            CornerRadius(5.0f, 5.0f, 5.0f, 5.0f),
            arrayOf("6px", "10px", "1px", "5px", "0px", "0px", "0px", "0px", "0px"),
            RectF(13.0f, 1.0f, 7.0f, 99.0f),
            Range(360f, 0f, 180f)
        )
        valuesSliderH.testValues(sliderH)
        valuesSliderH.testMoveSelector(sliderH, PointF(223f, 312f), Range(360f, 0f, 0f))


        // initialize sliderV
        colorConverter = ColorConverter(0, 255, 255)
        updater = Updater(colorConverter = colorConverter, colorWindows = mutableListOf(sliderV))
        setColorWindowSize(sliderV, 20, 100)

        // sliderV
        val valuesSliderV = SliderValueHolder(
            788529152,
            1f,
            -1,
            6f,
            -16777216,
            0f,
            10f,
            13f,
            50f,
            CornerRadius(5.0f, 5.0f, 5.0f, 5.0f),
            arrayOf("6px", "10px", "1px", "5px", "0px", "0px", "0px", "0px", "0px"),
            RectF(13.0f, 1.0f, 7.0f, 99.0f),
            Range(100f, 0f, 100f)
        )
        valuesSliderV.testValues(sliderV)
        valuesSliderV.testMoveSelector(sliderV, PointF(223f, 312f), Range(100f, 0f, 0f))


        // initialize sliderA
        colorConverter = ColorConverter(0, 255, 255)
        updater = Updater(colorConverter = colorConverter, colorWindows = mutableListOf(sliderA))
        setColorWindowSize(sliderA, 20, 100)

        // sliderA
        val valuesSliderA = SliderValueHolder(
            788529152,
            1f,
            -1,
            6f,
            -16777216,
            0f,
            10f,
            7f,
            50f,
            CornerRadius(5.0f, 5.0f, 5.0f, 5.0f),
            arrayOf("6px", "10px", "1px", "5px", "0px", "0px", "0px", "0px", "0px"),
            RectF(13.0f, 1.0f, 7.0f, 99.0f),
            Range(0f, 255f, 255f)
        )
        valuesSliderA.testValues(sliderA)
        valuesSliderA.testMoveSelector(sliderA, PointF(223f, 312f), Range(0f, 255f, 255f))
    }

    /**
     * Test the rectangular color windows, that includes the rectangular windows: RectangularSV, RectangularSL
     */
    fun RectangularColorWindowsTest() {

        // initialize rectangularSV
        colorConverter = ColorConverter(0, 255, 255)
        updater = Updater(colorConverter = colorConverter, colorWindows = mutableListOf(rectangularSV))
        setColorWindowSize(rectangularSV, 100, 100)

        // rectangularSV
        val valuesRectangularSV = RectangularValueHolder(
            788529152,
            1f,
            -1,
            6f,
            -16777216,
            0f,
            10f,
            87f,
            13f,
            CornerRadius(5.0f, 5.0f, 5.0f, 5.0f),
            arrayOf("6px", "10px", "1px", "5px", "0px", "0px", "0px", "0px", "0px"),
            RectF(13.0f, 13.0f, 87.0f, 87.0f),
            Range(100f, 0f, 100f),
            Range(0f, 100f, 100f)
        )
        valuesRectangularSV.testValues(rectangularSV)
        valuesRectangularSV.testMoveSelector(
            rectangularSV, PointF(223f, 312f), Range(100f, 0f, 0f),
            Range(0f, 100f, 100f)
        )


        // initialize rectangularSL
        colorConverter = ColorConverter(0, 255, 255)
        updater = Updater(colorConverter = colorConverter, colorWindows = mutableListOf(rectangularSL))
        setColorWindowSize(rectangularSL, 100, 100)

        // rectangularSV
        val valuesRectangularSL = RectangularValueHolder(
            788529152,
            1f,
            -1,
            6f,
            -16777216,
            0f,
            10f,
            13f,
            50f,
            CornerRadius(5.0f, 5.0f, 5.0f, 5.0f),
            arrayOf("6px", "10px", "1px", "5px", "0px", "0px", "0px", "0px", "0px"),
            RectF(13.0f, 13.0f, 87.0f, 87.0f),
            Range(100f, 0f, 50f),
            Range(100f, 0f, 100f)
        )
        valuesRectangularSL.testValues(rectangularSL)
        valuesRectangularSL.testMoveSelector(
            rectangularSL, PointF(223f, 312f), Range(100f, 0f, 0f),
            Range(100f, 0f, 0f)
        )
    }

    /**
     * Test the circular color windows, that includes the circular windows: CircularHS
     */
    fun CircularColorWindowsTest() {

        // initialize circularHS
        colorConverter = ColorConverter(0, 255, 255)
        updater = Updater(colorConverter = colorConverter, colorWindows = mutableListOf(circularHS))
        setColorWindowSize(circularHS, 100, 100)

        // circularHS
        val valuesCircularHS = CircularValueHolder(
            788529152,
            1f,
            -1,
            6f,
            -16777216,
            0f,
            10f,
            13f,
            50f,
            CornerRadius(5.0f, 5.0f, 5.0f, 5.0f),
            arrayOf("6px", "10px", "1px", "5px", "0px", "0px", "0px", "0px", "0px"),
            180f,
            37.0f,
            37.0f,
            Range(0f, 100f, 100f),
            Range( 0f, 360f, 180f)

        )
        valuesCircularHS.testValues(circularHS)
        valuesCircularHS.testMoveSelector(
            circularHS, PointF(223f, 312f), Range(0f, 100f, 100f),
            Range(0f, 360f, 57f)
        )

        // static methods
        val distanceBetweenTwoPoints = Circular.distanceBetweenTwoPoints(10f, 12f, 55f, 23f)
        assertThat(distanceBetweenTwoPoints).isEqualTo(46.32494f)

        val angleBetweenTwoPoint = Circular.angleBetweenTwoPoint(10f, 12f, 55f, 23f)
        assertThat(angleBetweenTwoPoint).isEqualTo(13.736268f)

        val rotatedPoint = Circular.rotatePoint(10f, 12f, 55f, 23f, 90f)
        assertThat(rotatedPoint).isEqualTo(PointF(21.0f, -33.0f))

        val distantPoint = Circular.distantPoint(10f, 12f, 55f, 23f, 100f)
        assertThat(distantPoint).isEqualTo(PointF(107.1399f, 35.745308f))
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
     * Test the updater class and all of its methods.
     */
    fun UpdaterTest() {

        // order of methods call should be kept the same
        UpdaterAttachUsingConstructor(colorModelHolder)
        UpdaterAttachUsingMethods(colorModelHolder)
        UpdaterFocusUnfocusMethods()
        UpdaterListenerTest()
        UpdaterCheckExpectedFormat()
    }

    /**
     * Test functions that check for the expected format for text views with multiple integer values those are
     * HSV, HSL, HWB, CMYK, RGB, RGBA, test the format for single integer value those are HSV_H, HSV_S, HSV_S,..
     * which are the single values forming the multiple integer format. And check the format for HEX and HEXA
     * text views. The test checks if methods properly determine if text view values is in the correct format.
     */
    fun UpdaterCheckExpectedFormat() {

        // correct values expected for HSV
        textViewHSV.text = "0°, 0%, 0%"
        var areMultipleIntValuesCorrect = updater.checkMultipleInt(textViewHSV)
        assertThat(areMultipleIntValuesCorrect).isEqualTo(true)

        // exceed the limit of HSV by setting the V to 101
        textViewHSV.text = "0°, 0%, 101%"
        areMultipleIntValuesCorrect = updater.checkMultipleInt(textViewHSV)
        assertThat(areMultipleIntValuesCorrect).isEqualTo(false)

        // adding extra value when the limit is 3 total values H,S,V
        textViewHSV.text = "0°, 0%, 0%, 0"
        areMultipleIntValuesCorrect = updater.checkMultipleInt(textViewHSV)
        assertThat(areMultipleIntValuesCorrect).isEqualTo(false)

        // removing one value when the expected number is 3 values H,S,V
        textViewHSV.text = "0°, 0%"
        areMultipleIntValuesCorrect = updater.checkMultipleInt(textViewHSV)
        assertThat(areMultipleIntValuesCorrect).isEqualTo(false)

        // correct values expected for H
        textViewHSV_H.text = "4"
        var isSingleIntValueCorrect = updater.checkSingeInt(textViewHSV_H)
        assertThat(isSingleIntValueCorrect).isEqualTo(true)

        // exceed the limit of H by setting the H to 361
        textViewHSV_H.text = "361"
        isSingleIntValueCorrect = updater.checkSingeInt(textViewHSV_H)
        assertThat(isSingleIntValueCorrect).isEqualTo(false)

        // checkHEX
        textViewHEX.text = "#ff00ff"
        var isSHexValueCorrect = updater.checkHEX(textViewHEX)
        assertThat(isSHexValueCorrect).isEqualTo(true)

        textViewHEX.text = "#ff00fp"
        isSHexValueCorrect = updater.checkHEX(textViewHEX)
        assertThat(isSHexValueCorrect).isEqualTo(false)

        textViewHEX.text = "ff00ff0"
        isSHexValueCorrect = updater.checkHEX(textViewHEX)
        assertThat(isSHexValueCorrect).isEqualTo(false)
    }

    /**
     * Test the attaching of color windows and text views using the constructor of the Updater
     * class, and check for proper values for both type of views.
     */
    fun UpdaterAttachUsingConstructor(colorModelHolder: ColorModelHolder) {

        // attach using constructor
        colorConverter = ColorConverter("#538CB51F")
        updater = Updater(colorConverter, ColorHolder(), colorWindows, textViews)

        // set size for all modules
        setColorWindowsSize()

        // make sure expected number of elements are attached
        assertThat(updater.colorWindows.size).isEqualTo(6)
        assertThat(updater.textViews.size).isEqualTo(25)

        // check expected values values
        checkExpectedValues(colorModelHolder)
    }

    /**
     * Test the attach methods available in the updater class for both text views and color windows(modules).
     */
    fun UpdaterAttachUsingMethods(colorModelHolder: ColorModelHolder) {

        // test detaching methods for both color windows and text views
        updater.detachColorWindow(rectangularSV)
        updater.detachTextViews(textViewRGB)

        // make sure expected number of elements are attached
        assertThat(updater.colorWindows.size).isEqualTo(5)
        assertThat(updater.textViews.size).isEqualTo(24)

        // test detaching all, and clear previously set values
        updater.detachAll()
        ResetViewsValues()

        // make sure expected number of elements are attached
        assertThat(updater.colorWindows.size).isEqualTo(0)
        assertThat(updater.textViews.size).isEqualTo(0)

        // attach color windows
        updater.attachColorWindow(circularHS)
        updater.attachColorWindows(rectangularSV, rectangularSL)
        updater.attachColorWindows(listOf(sliderA, sliderH, sliderV))

        // attach text views
        updater.attachTextViewsRGB(textViewRGB)
        updater.attachTextViewsRGBA(textViewRGBA)
        updater.attachTextViewsHSV(textViewHSV)
        updater.attachTextViewsHSL(textViewHSL)
        updater.attachTextViewsHWB(textViewHWB)
        updater.attachTextViewsCMYK(textViewCMYK)
        updater.attachTextViewsHEX(textViewHEX)
        updater.attachTextViewsHEXA(textViewHEXA)
        updater.attachTextViewRGBA(textViewRGBA_R, textViewRGBA_G, textViewRGBA_B, textViewRGBA_A)
        updater.attachTextViewHSV(textViewHSV_H, textViewHSV_S, textViewHSV_V)
        updater.attachTextViewHSL(textViewHSL_H, textViewHSL_S, textViewHSL_L)
        updater.attachTextViewHWB(textViewHWB_H, textViewHWB_W, textViewHWB_B)
        updater.attachTextViewCMYK(textViewCMYK_C, textViewCMYK_M, textViewCMYK_Y, textViewCMYK_K)

        // make sure expected number of elements are attached
        assertThat(updater.colorWindows.size).isEqualTo(6)
        assertThat(updater.textViews.size).isEqualTo(25)

        checkExpectedValues(colorModelHolder)
    }

    /**
     * Reset all text views values and color windows values, that means text views values are set
     * to empty string, and color windows range values are set to 0.
     */
    fun ResetViewsValues() {

        // clear text view value, so cached values are not used
        for ((textView, type) in textViews) {
            textView.text = ""
        }

        // clear color windows current values
        for (colorWindow in colorWindows) {

            when (colorWindow) {
                is Rectangular -> {
                    colorWindow.horizontalRange.current = 0f
                    colorWindow.verticalRange.current = 0f
                }
                is Circular -> {
                    colorWindow.angleRange.current = 0f
                    colorWindow.distanceRange.current = 0f
                }
                is Slider -> {
                    colorWindow.range.current = 0f
                }
            }
        }
    }

    /**
     * Check the text view values, after calling the focus and unfocus methods
     * from the updater class.
     */
    fun UpdaterFocusUnfocusMethods() {

        /**
         * Check tha values of the text view, when focused or unfocused,
         * when focus the suffix are removed for the mutiple value text
         * such as HSV, HWB, CMYK..
         */
        fun checkValue(textView: TextView, focusValue: String, unfocusValue: String) {

            // focus text view and check values
            updater.focus(textView)
            assertThat(textView.text.toString()).isEqualTo(focusValue)

            // unfocus text view and check values
            updater.unfocus(textView)
            assertThat(textView.text.toString()).isEqualTo(unfocusValue)
        }

        // check RGBA
        checkValue(textViewRGB, "83 140 181", "83, 140, 181")
        checkValue(textViewRGBA, "83 140 181 31", "83, 140, 181, 31")
        checkValue(textViewRGBA_R, "83", "83")
        checkValue(textViewRGBA_G, "140", "140")
        checkValue(textViewRGBA_B, "181", "181")
        checkValue(textViewRGBA_A, "12", "12")  // [0,255]

        // check HSV
        checkValue(textViewHSV, "205 54 71", "205°, 54%, 71%")
        checkValue(textViewHSV_H, "205", "205")
        checkValue(textViewHSV_S, "54", "54")
        checkValue(textViewHSV_V, "71", "71")

        // check HSL
        checkValue(textViewHSL, "205 40 52", "205°, 40%, 52%")
        checkValue(textViewHSL_H, "205", "205")
        checkValue(textViewHSL_S, "40", "40")
        checkValue(textViewHSL_L, "52", "52")

        // check HWB
        checkValue(textViewHWB, "205 33 29", "205°, 33%, 29%")
        checkValue(textViewHWB_H, "205", "205")
        checkValue(textViewHWB_W, "33", "33")
        checkValue(textViewHWB_B, "29", "29")

        // check CMYK
        checkValue(textViewCMYK, "54 23 0 29", "54%, 23%, 0%, 29%")
        checkValue(textViewCMYK_C, "54", "54")
        checkValue(textViewCMYK_M, "23", "23")
        checkValue(textViewCMYK_Y, "0", "0")
        checkValue(textViewCMYK_K, "29", "29")

        // check HEXA
        checkValue(textViewHEX, "#538CB5", "#538CB5")
        checkValue(textViewHEXA, "#538CB51F", "#538CB51F")
    }

    /**
     * Check the listener for the updater for proper call when the color window or
     * text view values are changed.
     */
    fun UpdaterListenerTest() {

        // create mock listener and attach it to the converter
        val listener = Mockito.mock(Updater.OnUpdateListener::class.java)
        updater.onUpdateListener = listener

        // expect 1 call of the listener for the color window, after its selector is moved
        rectangularSV.moveSelector(50f, 0f)
        Mockito.verify(listener, Mockito.times(1)).onColorWindowUpdate(rectangularSV)
        Mockito.verify(listener, Mockito.times(0)).onTextViewUpdate(textViewHEX)

        // change text
        textViewHEX.text = "#FF14F3"
        updater.checkTextView(textViewHEX)

        // expect 1 call of the listener for the text view, after text was changed
        Mockito.verify(listener, Mockito.times(1)).onColorWindowUpdate(rectangularSV)
        Mockito.verify(listener, Mockito.times(1)).onTextViewUpdate(textViewHEX)
    }

    /**
     * Check if current value for the color modules and text views matches
     * the expected values for all available color models.
     * @param holder holder for the expected values for the color models
     */
    fun checkExpectedValues(holder: ColorModelHolder) {

        // rectangularSV
        assertThat(rectangularSV.horizontalRange.current).isEqualTo(holder.hsv_s)
        assertThat(rectangularSV.verticalRange.current).isEqualTo(holder.hsv_v)

        // rectangularSL
        assertThat(rectangularSL.horizontalRange.current).isEqualTo(holder.hsl_s)
        assertThat(rectangularSL.verticalRange.current).isEqualTo(holder.hsl_l)

        // circularHS
        assertThat(circularHS.angleRange.current).isEqualTo(holder.hsv_h)
        assertThat(circularHS.distanceRange.current).isEqualTo(holder.hsv_s)

        // sliderA
        assertThat(sliderA.range.current).isEqualTo(holder.rgba_a)

        // sliderH
        assertThat(sliderH.range.current).isEqualTo(holder.hsv_h)

        // sliderV
        assertThat(sliderV.range.current).isEqualTo(holder.hsv_v)

        // r,g,b,a
        assertThat(textViewRGB.text.toString()).isEqualTo(holder.rgb)
        assertThat(textViewRGBA.text.toString()).isEqualTo(holder.rgba)
        assertThat(textViewRGBA_R.text.toString()).isEqualTo("${holder.rgba_r}")
        assertThat(textViewRGBA_G.text.toString()).isEqualTo("${holder.rgba_g}")
        assertThat(textViewRGBA_B.text.toString()).isEqualTo("${holder.rgba_b}")
        assertThat(textViewRGBA_A.text.toString()).isEqualTo("${(holder.rgba_a * (100f / 255)).toInt()}")

        // h,s,v
        assertThat(textViewHSV.text.toString()).isEqualTo(holder.hsv)
        assertThat(textViewHSV_H.text.toString()).isEqualTo("${holder.hsv_h}")
        assertThat(textViewHSV_S.text.toString()).isEqualTo("${holder.hsv_s}")
        assertThat(textViewHSV_V.text.toString()).isEqualTo("${holder.hsv_v}")

        // h,s,l
        assertThat(textViewHSL.text.toString()).isEqualTo(holder.hsl)
        assertThat(textViewHSL_H.text.toString()).isEqualTo("${holder.hsl_h}")
        assertThat(textViewHSL_S.text.toString()).isEqualTo("${holder.hsl_s}")
        assertThat(textViewHSL_L.text.toString()).isEqualTo("${holder.hsl_l}")

        // h,w,b
        assertThat(textViewHWB.text.toString()).isEqualTo(holder.hwb)
        assertThat(textViewHWB_H.text.toString()).isEqualTo("${holder.hwb_h}")
        assertThat(textViewHWB_W.text.toString()).isEqualTo("${holder.hwb_w}")
        assertThat(textViewHWB_B.text.toString()).isEqualTo("${holder.hwb_b}")

        // c,m,y,k
        assertThat(textViewCMYK.text.toString()).isEqualTo(holder.cmyk)
        assertThat(textViewCMYK_C.text.toString()).isEqualTo("${holder.cmyk_c}")
        assertThat(textViewCMYK_M.text.toString()).isEqualTo("${holder.cmyk_m}")
        assertThat(textViewCMYK_Y.text.toString()).isEqualTo("${holder.cmyk_y}")
        assertThat(textViewCMYK_K.text.toString()).isEqualTo("${holder.cmyk_k}")

        // hex
        assertThat(textViewHEX.text.toString()).isEqualTo(holder.hex)
        assertThat(textViewHEXA.text.toString()).isEqualTo(holder.hexAlpha)
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

    /**
     * Check range values
     * @param range range object which values should be checked
     * @param lower expected lower value
     * @param upper expected upper value
     * @param current expected current value
     */
    fun checkRange(range: Range, lower: Float, upper: Float, current: Float) {
        assertThat(range.lower).isEqualTo(lower)
        assertThat(range.upper).isEqualTo(upper)
        assertThat(range.current).isEqualTo(current)
    }
}

/**
 * Class that holds the expected values for given Slider color windows.
 */
class SliderValueHolder(
    borderColor: Int, borderStrokeWidth: Float, selectorColor: Int, selectorStrokeWidth: Float,
    selectorExtraStrokeColor: Int, selectorExtraStrokeWidth: Float, selectorRadius: Float, selectorX: Float, selectorY: Float,
    cornerRadius: CornerRadius, unitsString: Array<String>, var bound: RectF, var range: Range
) : ColorWindowValueHolder(
    borderColor, borderStrokeWidth, selectorColor, selectorStrokeWidth,
    selectorExtraStrokeColor, selectorExtraStrokeWidth, selectorRadius, selectorX, selectorY,
    cornerRadius, unitsString
) {

    override fun testValues(base: Base) {
        super.testValues(base)
        if (base is Slider) {
            assertThat(base.bound).isEqualTo(bound)
            assertThat(base.range.toString()).isEqualTo(range.toString())
        }
    }

    /**
     * Test moving the selector for the color window
     */
    fun testMoveSelector(slider: Slider, position: PointF, range: Range) {
        slider.moveSelector(position.x, position.y)
        assertThat(slider.range.toString()).isEqualTo(range.toString())
    }
}

/**
 * Class that holds the expected values for give Rectangular color windows.
 */
class RectangularValueHolder(
    borderColor: Int, borderStrokeWidth: Float, selectorColor: Int, selectorStrokeWidth: Float,
    selectorExtraStrokeColor: Int, selectorExtraStrokeWidth: Float, selectorRadius: Float, selectorX: Float, selectorY: Float,
    cornerRadius: CornerRadius, unitsString: Array<String>, var bound: RectF, var verticalRange: Range, var horizontalRange: Range
) : ColorWindowValueHolder(
    borderColor, borderStrokeWidth, selectorColor, selectorStrokeWidth,
    selectorExtraStrokeColor, selectorExtraStrokeWidth, selectorRadius, selectorX, selectorY,
    cornerRadius, unitsString
) {

    override fun testValues(base: Base) {
        super.testValues(base)

        if (base is Rectangular) {
            assertThat(base.bound).isEqualTo(bound)
            assertThat(base.verticalRange.toString()).isEqualTo(verticalRange.toString())
            assertThat(base.horizontalRange.toString()).isEqualTo(horizontalRange.toString())
        }
    }

    /**
     * Test moving the selector for the color window
     */
    fun testMoveSelector(rectangular: Rectangular, position: PointF, verticalRange: Range, horizontalRange: Range) {
        rectangular.moveSelector(position.x, position.y)
        assertThat(rectangular.verticalRange.toString()).isEqualTo(verticalRange.toString())
        assertThat(rectangular.horizontalRange.toString()).isEqualTo(horizontalRange.toString())
    }
}

/**
 * Class that holds the expected values for given Circular color windows.
 */
class CircularValueHolder(
    borderColor: Int, borderStrokeWidth: Float, selectorColor: Int, selectorStrokeWidth: Float,
    selectorExtraStrokeColor: Int, selectorExtraStrokeWidth: Float, selectorRadius: Float, selectorX: Float, selectorY: Float,
    cornerRadius: CornerRadius, unitsString: Array<String>, val angle: Float, val distance: Float, var radius: Float, var distanceRange: Range, var angleRange: Range
) : ColorWindowValueHolder(
    borderColor, borderStrokeWidth, selectorColor, selectorStrokeWidth,
    selectorExtraStrokeColor, selectorExtraStrokeWidth, selectorRadius, selectorX, selectorY,
    cornerRadius, unitsString
) {

    override fun testValues(base: Base) {
        super.testValues(base)
        if (base is Circular) {
            assertThat(base.distanceRange.toString()).isEqualTo(distanceRange.toString())
            assertThat(base.angleRange.toString()).isEqualTo(angleRange.toString())
            assertThat(base.angle).isEqualTo(angle)
            assertThat(base.distance).isEqualTo(distance)
            assertThat(base.radius).isEqualTo(radius)
        }
    }

    /**
     * Test moving the selector for the color window
     */
    fun testMoveSelector(circular: Circular, position: PointF, distanceRange: Range, angleRange: Range) {
        circular.moveSelector(position.x, position.y)
        assertThat(circular.distanceRange.toString()).isEqualTo(distanceRange.toString())
        assertThat(circular.angleRange.toString()).isEqualTo(angleRange.toString())
    }
}

/**
 * Class that holds the expected values for given color windows.
 */
open class ColorWindowValueHolder(
    var borderColor: Int, var borderStrokeWidth: Float, var selectorColor: Int, var selectorStrokeWidth: Float,
    var selectorExtraStrokeColor: Int, var selectorExtraStrokeWidth: Float, var selectorRadius: Float, var selectorX: Float, var selectorY: Float,
    var cornerRadius: CornerRadius, var unitsString: Array<String>
) {

    /**
     * Test if vales matches between given color window, and the values from the holder
     */
    open fun testValues(base: Base) {
        assertThat(base.borderColor).isEqualTo(borderColor)
        assertThat(base.borderStrokeWidth).isEqualTo(borderStrokeWidth)
        assertThat(base.selectorColor).isEqualTo(selectorColor)
        assertThat(base.selectorStrokeWidth).isEqualTo(selectorStrokeWidth)
        assertThat(base.selectorExtraStrokeColor).isEqualTo(selectorExtraStrokeColor)
        assertThat(base.selectorExtraStrokeWidth).isEqualTo(selectorExtraStrokeWidth)
        assertThat(base.selectorRadius).isEqualTo(selectorRadius)
        assertThat(base.selectorX).isEqualTo(selectorX)
        assertThat(base.selectorY).isEqualTo(selectorY)
        assertThat(base.cornerRadius.toString()).isEqualTo(cornerRadius.toString())
        assertThat(base.unitsString).isEqualTo(unitsString)
    }
}