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

import android.text.Editable
import android.text.InputFilter
import android.text.InputFilter.LengthFilter
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.TextView
import com.slaviboy.colorpicker.data.ColorHolder
import com.slaviboy.colorpicker.models.*
import com.slaviboy.colorpicker.components.Base
import com.slaviboy.colorpicker.components.Base.OnUpdateListener
import com.slaviboy.colorpicker.components.Circular
import com.slaviboy.colorpicker.components.Rectangular
import com.slaviboy.colorpicker.components.Slider
import com.slaviboy.colorpicker.module.circular.CircularHS
import com.slaviboy.colorpicker.module.rectangular.RectangularSL
import com.slaviboy.colorpicker.module.rectangular.RectangularSV
import com.slaviboy.colorpicker.module.slider.SliderA
import com.slaviboy.colorpicker.module.slider.SliderH
import com.slaviboy.colorpicker.module.slider.SliderV
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.math.roundToInt

/**
 * Update class is the class that takes care for updating text views and color windows.
 * The only thing you need to do is to attach different components and the class will
 * take care for the rest. That way it is easy to create custom color pickers.
 * @param colorConverter object that holds the base color, and selected color for the pickers
 * @param colorHolder global color converter object, that holds colorHolder for the window
 * @param colorWindows list with all attached color windows
 * @param textViews hash map with text view set as key and type as value
 */
class Updater(
    val colorConverter: ColorConverter = ColorConverter(),
    val colorHolder: ColorHolder = ColorHolder(),
    colorWindows: MutableList<Base> = ArrayList(),
    textViews: HashMap<TextView, Int> = HashMap()
) : OnUpdateListener {

    constructor(colorHolder: ColorHolder) : this(ColorConverter(), colorHolder)

    internal var colorWindows: MutableList<Base>
    internal var textViews: HashMap<TextView, Int>
    internal var isInnerTextChange: Boolean                             // flag showing if setText() is called from inside this class, if false then it is called by the user from the UI
    internal var newCaretPosition: Int                                  // new caret position after user types in edit text
    internal lateinit var cachedTextValue: String                       // cached value that is set when text view is focused by the user
    internal lateinit var onTextChangeListener: OnTextChangeListener    // when text change listener is set, in the onTextChange() method you specify how other text views values changes
    internal lateinit var onUpdateListener: OnUpdateListener            // update listener, from which you can listen for text view or color window change by the user

    init {
        isInnerTextChange = false
        newCaretPosition = 0

        colorHolder.onConvert(colorConverter)            // initial call, to match the default color
        colorConverter.onConvertListener = colorHolder   // attach the listener to the color holder

        this.colorWindows = ArrayList()
        this.textViews = HashMap()

        // attach color windows if such exist
        for (i in colorWindows.indices) {
            attachColorWindows(colorWindows[i])
        }

        // attach text views with given type
        for (textView in textViews.keys) {
            val type = textViews.getValue(textView)
            attachTextView(textView, type)
        }
    }

    /**
     * Attach text view to the updater class with certain type, that shows what text value will expected
     * as input and output, from which the other attached color windows and text values will be updated
     * synchronously.
     * @param textView text view that will be attached
     * @param type text view type
     */
    fun attachTextView(textView: TextView, type: Int, isUpdate: Boolean = false) {

        // make sure value is not already in the hash map
        if (!textViews.containsKey(textView) || isUpdate) {
            textView.isSingleLine = true

            // add listener to detect text changes
            textView.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                    this@Updater.beforeTextChanged(start, after)
                }

                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
                override fun afterTextChanged(s: Editable) {
                    this@Updater.afterTextChanged(s)
                }
            })

            // set listener for focus changes
            textView.onFocusChangeListener = View.OnFocusChangeListener { view, hasFocus ->
                if (hasFocus) {
                    focus(view as TextView, type)
                } else {
                    unfocus(view as TextView, type)
                }
            }

            // set DONE button for keyboard and add listener
            textView.imeOptions = EditorInfo.IME_ACTION_DONE
            textView.setOnEditorActionListener { view, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    unfocus(view, type)
                }
                false
            }

            if (type == TYPE_HEX) {
                // for hex limit to 7 digits
                textView.filters = arrayOf<InputFilter>(LengthFilter(7))
            } else if (type == TYPE_HEXA) {
                // for hex limit to 9 digits
                textView.filters = arrayOf<InputFilter>(LengthFilter(9))
            } else if (type == TYPE_RGB || type == TYPE_RGBA || type == TYPE_HSV || type == TYPE_HSL || type == TYPE_HWB || type == TYPE_CMYK) {
                // multiple values expected
                textView.filters = arrayOf<InputFilter>()
            } else {
                // for single integer values limit to 3 digits
                textView.filters = arrayOf<InputFilter>(LengthFilter(3))
            }

            textView.isCursorVisible = false
            textViews[textView] = type

            // update text after it is put into the list array
            if (!isUpdate) {
                updateTextView(textView)
            } else {
                updateTextViews()
            }
        }
    }

    /**
     * Update the tag for a specific text view
     * @param textView text view which tag should be updated
     * @param type the new type fo the text view
     */
    fun updateTextViewTag(textView: TextView, type: Int) {
        clearEditTextFocus()
        attachTextView(textView, type, true)
    }

    /**
     * Attach multiple text views, each with its own type.
     * @param textViews array list with text views that will be attached
     * @param types array list with the corresponding type to each text view
     */
    fun attachTextViews(textViews: ArrayList<TextView>, types: ArrayList<Int>) {
        for (i in textViews.indices) {
            attachTextView(textViews[i], types[i])
        }
    }

    /**
     * Attach multiple text views with preset type: hex
     * @param textViewHEX text view with hexadecimal type
     */
    fun attachTextViewsHEX(vararg textViewHEX: TextView) {
        for (i in textViewHEX.indices) {
            attachTextView(textViewHEX[i], TYPE_HEX)
        }
    }

    /**
     * Attach multiple text views with preset type: hexA
     * @param textViewHEXA text view with hexadecimal type that include alpha channel
     */
    fun attachTextViewsHEXA(vararg textViewHEXA: TextView) {
        for (i in textViewHEXA.indices) {
            attachTextView(textViewHEXA[i], TYPE_HEXA)
        }
    }

    /**
     * Attach multiple text views with preset type: rgb
     * @param textViewRGB text view with combine type: rgb(red,green,blue)
     */
    fun attachTextViewsRGB(vararg textViewRGB: TextView) {
        for (i in textViewRGB.indices) {
            attachTextView(textViewRGB[i], TYPE_RGB)
        }
    }

    /**
     * Attach three text views each one with separate type: r, g and b
     * @param textViewR text view with r(red) type
     * @param textViewG text view with g(green) type
     * @param textViewB text view with b(blue) type
     */
    fun attachTextViewRGB(textViewR: TextView, textViewG: TextView, textViewB: TextView) {
        attachTextView(textViewR, TYPE_RGBA_R)
        attachTextView(textViewG, TYPE_RGBA_G)
        attachTextView(textViewB, TYPE_RGBA_B)
    }

    /**
     * Attach four text views each one with separate type: r, g, b and a
     * @param textViewR text view with r(red) type
     * @param textViewG text view with g(green) type
     * @param textViewB text view with b(blue) type
     * @param textViewA text view with a(alpha) type
     */
    fun attachTextViewRGBA(textViewR: TextView, textViewG: TextView, textViewB: TextView, textViewA: TextView) {
        attachTextViewRGB(textViewR, textViewG, textViewB)
        attachTextView(textViewA, TYPE_RGBA_A)
    }

    /**
     * Attach multiple text views with preset type: rgba
     * @param textViewRGBA text view with combine type: rgba(red,green,blue,alpha)
     */
    fun attachTextViewsRGBA(vararg textViewRGBA: TextView) {
        for (i in textViewRGBA.indices) {
            attachTextView(textViewRGBA[i], TYPE_RGBA)
        }
    }

    /**
     * Attach multiple text views with preset type: hsv.
     * @param textViewHSV text view with combine type: hsv(hue,saturation,value)
     */
    fun attachTextViewsHSV(vararg textViewHSV: TextView) {
        for (i in textViewHSV.indices) {
            attachTextView(textViewHSV[i], TYPE_HSV)
        }
    }

    /**
     * Attach three text views each one with separate type: h, s and v.
     * @param textViewH text view with h(hue) type
     * @param textViewS text view with s(saturation) type
     * @param textViewV text view with v(value) type
     */
    fun attachTextViewHSV(textViewH: TextView, textViewS: TextView, textViewV: TextView) {
        attachTextView(textViewH, TYPE_HSV_H)
        attachTextView(textViewS, TYPE_HSV_S)
        attachTextView(textViewV, TYPE_HSV_V)
    }

    /**
     * Attach multiple text views with preset type: hsv.
     * @param textViewHSL text view with combine type: hsl(hue,saturation,lightness)
     */
    fun attachTextViewsHSL(vararg textViewHSL: TextView) {
        for (i in textViewHSL.indices) {
            attachTextView(textViewHSL[i], TYPE_HSL)
        }
    }

    /**
     * Attach three text views each one with separate type: h, s and l.
     * @param textViewH text view with h(hue) type
     * @param textViewS text view with s(saturation) type
     * @param textViewL text view with l(lightness) type
     */
    fun attachTextViewHSL(textViewH: TextView, textViewS: TextView, textViewL: TextView) {
        attachTextView(textViewH, TYPE_HSL_H)
        attachTextView(textViewS, TYPE_HSL_S)
        attachTextView(textViewL, TYPE_HSL_L)
    }

    /**
     * Attach multiple text views with preset type: hwb.
     * @param textViewHWB text view with combine type: hwb(hue,white,black)
     */
    fun attachTextViewsHWB(vararg textViewHWB: TextView) {
        for (i in textViewHWB.indices) {
            attachTextView(textViewHWB[i], TYPE_HWB)
        }
    }

    /**
     * Attach three text views each one with separate type: h, w and b.
     * @param textViewH text view with h(hue) type
     * @param textViewW text view with w(white) type
     * @param textViewB text view with b(black) type
     */
    fun attachTextViewHWB(textViewH: TextView, textViewW: TextView, textViewB: TextView) {
        attachTextView(textViewH, TYPE_HWB_H)
        attachTextView(textViewW, TYPE_HWB_W)
        attachTextView(textViewB, TYPE_HWB_B)
    }

    /**
     * Attach multiple text views with preset type: cmyk.
     * @param textViewCMYK text view with combine type: cmyk(cyan,magenta,yellow,black)
     */
    fun attachTextViewsCMYK(vararg textViewCMYK: TextView) {
        for (i in textViewCMYK.indices) {
            attachTextView(textViewCMYK[i], TYPE_CMYK)
        }
    }

    /**
     * Attach four text views each one with separate type: c, m, y and k.
     * @param textViewC text view with c(cyan) type
     * @param textViewM text view with m(magenta) type
     * @param textViewY text view with y(yellow) type
     * @param textViewK text view with k(black) type
     */
    fun attachTextViewCMYK(textViewC: TextView, textViewM: TextView, textViewY: TextView, textViewK: TextView) {
        attachTextView(textViewC, TYPE_CMYK_C)
        attachTextView(textViewM, TYPE_CMYK_M)
        attachTextView(textViewY, TYPE_CMYK_Y)
        attachTextView(textViewK, TYPE_CMYK_K)
    }

    /**
     * Attach color windows passed as list argument.
     * @param colorWindows color windows that will be attached
     */
    fun attachColorWindows(colorWindows: List<Base>) {

        // attach multiple color windows using array list
        for (i in colorWindows.indices) {
            val colorWindow = colorWindows[i]
            attachColorWindow(colorWindow)
        }
    }

    /**
     * Attach color windows as multiple arguments.
     * @param colorWindows multiple argument variable
     */
    fun attachColorWindows(vararg colorWindows: Base) {

        // attach multiple color windows using multiple arguments
        for (i in colorWindows.indices) {
            attachColorWindow(colorWindows[i])
        }
    }

    /**
     * Attach color window to the array list with windows, set update listener and default color to window.
     * @param colorWindow color window that will be attached
     */
    internal fun attachColorWindow(colorWindow: Base) {

        // make sure color window is not yet attached
        for (i in this.colorWindows.indices) {
            if (this.colorWindows[i] == colorWindow) {
                return
            }
        }

        // set listener and color converter
        colorWindow.onUpdateListener = this
        colorWindow.colorConverter = colorConverter
        colorWindow.colorHolder = colorHolder

        // update and redraw to math the default color from the color converter
        colorWindow.update()
        colorWindow.redraw()

        // add color window to list
        colorWindows.add(colorWindow)
    }

    /**
     * Attach text views passed as list array argument. Each text view MUST have set tag property
     * set to its corresponding text view type. That way the updater knows what values to set to
     * each separate text view.
     * @param textViews text views that will be attached
     */
    fun attachTextViews(textViews: List<TextView>) {

        // attach multiple text views using array list
        for (i in textViews.indices) {
            val textView = textViews[i]
            attachTextView(textView)
        }
    }

    /**
     * Attach text view and get type by checking the tag value for expected string values. The tag
     * is set using xml, or set using the setTag() method.
     * @param textViews text view that will be attached
     */
    fun attachTextView(textViews: TextView) {

        // get string tag, to get type and attach text view
        val strTag = textViews.tag.toString()

        // attach only if proper type is set, and is supported by the updater class
        val type = getType(strTag)
        if (type in TYPE_RGB..TYPE_HEXA) {
            attachTextView(textViews, type)
        }
    }

    /**
     * Detach given text views, that were previously attached by the user
     * @param textViews text views passed as multiple arguments
     */
    fun detachTextViews(vararg textViews: TextView) {

        // remove all existing text view that are attached
        for (textView in textViews) {
            if (this.textViews.containsKey(textView)) {
                this.textViews.remove(textView)
            }
        }
    }

    /**
     * Detach given color windows, that were previously attached by the user
     * @param colorWindows color windows passed as multiple arguments
     */
    fun detachColorWindow(vararg colorWindows: Base) {

        // remove all existing color windows that are attached
        for (colorWindow in colorWindows) {
            this.colorWindows.remove(colorWindow)
        }
    }

    /**
     * Detach all color windows and text views, that were previously attached
     * by the user.
     */
    fun detachAll() {
        colorWindows = ArrayList()
        textViews = HashMap()
    }

    /**
     * Method called when text view is focused, to get cached values and remove unwanted
     * symbols for multiple values text view.
     * Example HSV: '130°, 45%, 32%' will be converted to '130 45 32'
     * @param textView text view that is being focused
     * @param type text view type
     */
    internal fun focus(textView: TextView, type: Int = textViews[textView]!!) {
        newCaretPosition = textView.selectionStart

        // set cached text value on focus and remove unwanted symbols
        if (type == TYPE_RGB || type == TYPE_RGBA || type == TYPE_HSV || type == TYPE_HSL || type == TYPE_HWB || type == TYPE_CMYK) {
            isInnerTextChange = true

            // leave only numbers and white space
            cachedTextValue = textView.text.toString()
                .replace("[^0-9 ]+".toRegex(), "")
            textView.text = cachedTextValue

            // if edit text move caret o beginning
            if (textView is EditText) {
                textView.setSelection(textView.text.length)
            }
            isInnerTextChange = false
        } else {
            cachedTextValue = textView.text.toString()
        }
        textView.isCursorVisible = true
    }

    /**
     * Method called when text view is unfocused to hide caret and restore suffix using
     * the color converter object, and returning the wanted values with special characters.
     * Example HSV: '130 45 32' will be converted to '130°, 45%, 32%'
     * @param textView text view that is being unfocused
     * @param type text view type
     */
    internal fun unfocus(textView: TextView, type: Int = textViews[textView]!!) {

        // set text value by including suffix
        val withSuffixString: String = when (type) {
            TYPE_RGB -> {
                colorConverter.rgba.getString(false)
            }
            TYPE_RGBA -> {
                colorConverter.rgba.toString()
            }
            TYPE_HSV -> {
                colorConverter.hsv.toString()
            }
            TYPE_HSL -> {
                colorConverter.hsl.toString()
            }
            TYPE_HWB -> {
                colorConverter.hwb.toString()
            }
            TYPE_CMYK -> {
                colorConverter.cmyk.toString()
            }
            else -> {
                cachedTextValue
            }
        }
        textView.text = withSuffixString
        textView.isCursorVisible = false
    }

    /**
     * Method called when color window is updated(changed) by the user, using
     * the UI interface(when user moves the selector).
     * @param colorWindow color window that is being updated by the user
     */
    override fun onUpdate(colorWindow: Base) {

        // set flag showing that text values will be changed from inside this class
        if (!isInnerTextChange) {
            isInnerTextChange = true
        }

        clearEditTextFocus()

        // update color windows and text views
        updateColorWindows(colorWindow)
        updateTextViews()

        // send call, showing that color window was updated
        if (::onUpdateListener.isInitialized) {
            onUpdateListener.onColorWindowUpdate(colorWindow)
        }

        // set flag showing that inner text change was finished
        isInnerTextChange = false
    }

    /**
     * Clear focus from all edit text.
     */
    fun clearEditTextFocus() {
        textViews.forEach { (key, _) ->
            if (key is EditText) {
                key.clearFocus()
            }
        }
    }

    /**
     * Method that update all color windows except the sender color window. The update includes
     * changing the color converter and then redrawing and updating the selector position, for
     * all color windows that are included.
     * @param colorWindow sender color window whose selector is being moved by the user
     */
    internal fun updateColorWindows(colorWindow: Base) {

        if (colorWindow is SliderH) {
            val w = colorWindow as Slider

            // hue for HSV & HSL
            val h = w.range.current.roundToInt()
            if (colorConverter.hsv.h != h) {
                colorConverter.hsv.h = h

                // redraw and update other color windows
                for (i in colorWindows.indices) {
                    val tempWindow = colorWindows[i]
                    if (tempWindow is CircularHS ||
                        tempWindow is SliderH
                    ) {
                        tempWindow.update()
                    } else {
                        tempWindow.redraw()
                    }
                }
            }
        } else if (colorWindow is SliderA) {
            val w = colorWindow as Slider

            // alpha for RGBA
            val a = (w.range.current).roundToInt()
            if (colorConverter.rgba.a != a) {
                colorConverter.rgba.a = a

                // redraw and update other alpha color windows
                for (i in colorWindows.indices) {
                    val tempWindow = colorWindows[i]
                    if (tempWindow is SliderA) {
                        tempWindow.update()
                    }
                }
            }
        } else if (colorWindow is SliderV) {
            val w = colorWindow as Slider

            // value for HSV
            val v = w.range.current.roundToInt()
            if (colorConverter.hsv.v != v) {
                colorConverter.hsv.v = v

                // redraw and update other color windows
                for (i in colorWindows.indices) {
                    val tempWindow = colorWindows[i]
                    if (tempWindow is CircularHS) {
                        // redraw only, it will use the color converter to get the V for dimming
                        tempWindow.redraw()
                    } else if (tempWindow is RectangularSV ||
                        tempWindow is RectangularSL ||
                        tempWindow is SliderV
                    ) {
                        tempWindow.update()
                    }
                }
            }
        } else if (colorWindow is RectangularSV) {
            val w = colorWindow as Rectangular

            // saturation and value for HSV
            val v = w.verticalRange.current.roundToInt()
            val s = w.horizontalRange.current.roundToInt()
            if (colorConverter.hsv.v != v || colorConverter.hsv.s != s) {

                colorConverter.hsv.setHSV(s = s, v = v)

                // redraw and update other color windows
                for (i in colorWindows.indices) {
                    val tempWindow = colorWindows[i]
                    if (tempWindow is SliderV ||
                        tempWindow is RectangularSL ||
                        tempWindow is RectangularSV
                    ) {
                        tempWindow.update()
                    } else if (tempWindow is CircularHS) {
                        // need redrawing for dimming
                        tempWindow.update()
                        tempWindow.redraw()
                    }
                }
            }
        } else if (colorWindow is RectangularSL) {
            val w = colorWindow as Rectangular

            // saturation and lightness for HSL
            val l = w.verticalRange.current.roundToInt()
            val s = w.horizontalRange.current.roundToInt()
            if (colorConverter.hsl.l != l || colorConverter.hsl.s != s) {

                colorConverter.hsl.setHSL(s = s, l = l)

                // redraw and update other color windows
                for (i in colorWindows.indices) {
                    val tempWindow = colorWindows[i]
                    if (tempWindow is SliderV ||
                        tempWindow is RectangularSL ||
                        tempWindow is RectangularSV
                    ) {
                        tempWindow.update()
                    } else if (tempWindow is CircularHS) {
                        // need redrawing for dimming
                        tempWindow.update()
                        tempWindow.redraw()
                    }
                }
            }
        } else if (colorWindow is CircularHS) {
            val w = colorWindow as Circular

            // saturation and hue for HSV
            val s = w.distanceRange.current.roundToInt()
            val h = w.angleRange.current.roundToInt()
            if (colorConverter.hsl.h != h || colorConverter.hsv.s != s) {

                colorConverter.hsv.setHSV(h, s)

                // redraw and update other color windows
                for (i in colorWindows.indices) {
                    val tempWindow = colorWindows[i]
                    if (tempWindow is SliderH ||
                        tempWindow is CircularHS
                    ) {
                        // update selector position
                        tempWindow.update()
                    } else if (tempWindow is SliderA ||
                        tempWindow is SliderV
                    ) {
                        // redraw using base color
                        tempWindow.redraw()
                    } else if (tempWindow is RectangularSL ||
                        tempWindow is RectangularSV
                    ) {
                        tempWindow.update()
                        tempWindow.redraw()
                    }
                }
            }
        }
    }

    /**
     * Method that update all text view values except the sender view, which is the text view
     * the user is changing at the moment, so it does not get in infinite loop by changing itself.
     * @param sender sender text view whose value is already changed
     */
    internal fun updateTextViews(sender: TextView? = null) {

        // if listener is set, call it instead of changing text values
        if (::onTextChangeListener.isInitialized) {
            onTextChangeListener.onTextChange(colorConverter, sender)
            return
        }

        // update all text views, except the sender one
        for (temp in textViews.keys) {

            // if text view match sender, then skip update
            if (sender != null && sender === temp) {
                continue
            }
            updateTextView(temp)
        }
    }

    /**
     * Update text view value using the color converter to get the value for the
     * corresponding text view type.
     * @param textView text view that will be updated
     */
    internal fun updateTextView(textView: TextView) {

        val type = textViews[textView]!!
        var intValue = -1

        // if there is a focused edit text then set caret at the beginning
        // get as string for multiple value type
        when (type) {
            TYPE_RGBA_R -> {
                intValue = colorConverter.rgba.r
            }
            TYPE_RGBA_G -> {
                intValue = colorConverter.rgba.g
            }
            TYPE_RGBA_B -> {
                intValue = colorConverter.rgba.b
            }
            TYPE_RGBA_A -> {
                intValue = ((colorConverter.rgba.a / 255f) * 100).toInt() // alpha in range between [0,100]
            }
            TYPE_HSV_H -> {
                intValue = colorConverter.hsv.h
            }
            TYPE_HSV_S -> {
                intValue = colorConverter.hsv.s
            }
            TYPE_HSV_V -> {
                intValue = colorConverter.hsv.v
            }
            TYPE_HWB_H -> {
                intValue = colorConverter.hwb.h
            }
            TYPE_HWB_W -> {
                intValue = colorConverter.hwb.w
            }
            TYPE_HWB_B -> {
                intValue = colorConverter.hwb.b
            }
            TYPE_HSL_H -> {
                intValue = colorConverter.hsl.h
            }
            TYPE_HSL_S -> {
                intValue = colorConverter.hsl.s
            }
            TYPE_HSL_L -> {
                intValue = colorConverter.hsl.l
            }
            TYPE_CMYK_C -> {
                intValue = colorConverter.cmyk.c
            }
            TYPE_CMYK_M -> {
                intValue = colorConverter.cmyk.m
            }
            TYPE_CMYK_Y -> {
                intValue = colorConverter.cmyk.y
            }
            TYPE_CMYK_K -> {
                intValue = colorConverter.cmyk.k
            }
        }

        val withSuffix = !textView.isFocused

        // get the new string value
        val value: String
        value = if (intValue == -1) {

            // get as string for multiple value type
            when (type) {
                TYPE_HEX -> {
                    colorConverter.hex.toString(false)
                }
                TYPE_HEXA -> {
                    colorConverter.hex.toString(true)
                }
                TYPE_RGB -> {
                    colorConverter.rgba.getString(false, withSuffix)
                }
                TYPE_RGBA -> {
                    colorConverter.rgba.getString(true, withSuffix)
                }
                TYPE_HSV -> {
                    colorConverter.hsv.getString(withSuffix)
                }
                TYPE_HSL -> {
                    colorConverter.hsl.getString(withSuffix)
                }
                TYPE_HWB -> {
                    colorConverter.hwb.getString(withSuffix)
                }
                TYPE_CMYK -> {
                    colorConverter.cmyk.getString(withSuffix)
                }
                else -> {
                    ""
                }
            }
        } else {
            // convert integer to string for single value types
            intValue.toString()
        }
        textView.text = value

        // if there is a focused edit text then set caret at the beginning
        if (textView is EditText && textView.isFocused()) {
            textView.setSelection(value.length)
        }
    }

    /**
     * Method called before text view, value is changed, used to get the new expected
     * caret position after the change.
     * @param start
     * @param after
     */
    internal fun beforeTextChanged(start: Int, after: Int) {

        // get new caret position the text view will have after the text change
        newCaretPosition = start + after
    }

    /**
     * After text change is made by the used, check if the new text value matches all expected text
     * pattern, that mean the new value passes all the restrictions.
     * @param editable editable holding the new text value
     */
    internal fun afterTextChanged(editable: Editable) {

        val stringValue = editable.toString()

        // if user is changing the text value using the UI
        if (!isInnerTextChange) {

            // set flag showing that inner(from this class) text vales change will be made
            isInnerTextChange = true

            if (stringValue != "") {
                for (textView in textViews.keys) {

                    // found which text view value is changed by the user
                    if (textView.text.toString() == stringValue && textView.isFocused) {

                        // check if the text change is allowed
                        checkTextView(textView)
                        break
                    }
                }
            }

            // set the flag showing that inner values are done, and user can again change value using the UI
            isInnerTextChange = false
        }
    }

    /**
     * Method called when user changes some of the attached text input values from the UI, and this
     * method checks if text value passes all restrictions like allowing only numbers, or check
     * for hexadecimal symbols only.
     * @param textView - text view whose value will be checked
     */
    internal fun checkTextView(textView: TextView) {

        val type = textViews[textView]!!
        val isValueUpdated: Boolean = if (type == TYPE_HEX || type == TYPE_HEXA) {
            // hex expected
            checkHEX(textView, type)
        } else if (type == TYPE_RGB || type == TYPE_RGBA || type == TYPE_HSV || type == TYPE_HSL || type == TYPE_HWB || type == TYPE_CMYK) {
            // multiple values expected
            checkMultipleInt(textView, type)
        } else {
            // single integer value expected
            checkSingeInt(textView, type)
        }

        // if text value change has passed all restrictions
        if (isValueUpdated) {

            // update and redraw all color windows
            for (i in colorWindows.indices) {
                val tempWindow = colorWindows[i]
                tempWindow.update()
                tempWindow.redraw()
            }

            // update other text views
            updateTextViews(textView)

            // send call, showing that text view value was changed
            if (::onUpdateListener.isInitialized) {
                onUpdateListener.onTextViewUpdate(textView)
            }
        }
    }

    /**
     * Update all color windows and text views
     */
    fun updateViews() {

        // update and redraw all color windows
        for (i in colorWindows.indices) {
            val tempWindow = colorWindows[i]
            tempWindow.update()
            tempWindow.redraw()
        }

        // update other text views
        updateTextViews()
    }

    /**
     * Method checks if text view value matches the expected HEX text pattern, the includes 7 symbols
     * length expectation and only hex symbols.
     * @param textView text view whose value is checked
     * @param type text view type that is hex or hexA
     * @return if the expected text patter is correct for the current text view
     */
    internal fun checkHEX(textView: TextView, type: Int = textViews[textView]!!): Boolean {

        // remove all symbols except the hexadecimal ones
        val newText = "#" + textView.text.toString().replace("[^a-f0-9A-F]+".toRegex(), "").toUpperCase()
        var isCorrect = false

        val expectedLength = if (type == TYPE_HEX) 7 else 9

        // make sure hex string length is matching the expected one for hex or hexA
        if (newText.length == expectedLength) {
            colorConverter.hex.setHexString(newText)
            isCorrect = true
        }

        // change text value
        if (textView is EditText) {
            val deletePosition = textView.text.length - newText.length
            val caretPosition = newCaretPosition
            cachedTextValue = newText

            // set caret position after inner text change
            textView.setText(newText)
            textView.setSelection(caretPosition - deletePosition)
        } else {
            textView.text = newText
        }
        return isCorrect
    }

    /**
     * Check if text view value matched multiple integer value, that includes text views holding multiple
     * values like: RGB, RGBA, HSV, HSL and CMYK.
     * @param textView text view whose value is checked
     * @param type text view type that has multiple values
     * @return if the expected text patter is correct for the current text view
     */
    internal fun checkMultipleInt(textView: TextView, type: Int = textViews[textView]!!): Boolean {
        val newTextArray = textView.text.toString()
            .replace("[^0-9 ]+".toRegex(), "") // leave only numbers and white space
            .replace(" +".toRegex(), " ")      // remove white spaces that are glued together
            .split(" ".toRegex()).toTypedArray()           // split by white space to get numbers as string array

        // extract the numbers from the string array
        var totalValues = 0
        val intValues = IntArray(newTextArray.size)
        for (i in intValues.indices) {
            if (newTextArray[i].isNotEmpty()) {
                intValues[totalValues] = newTextArray[i].toInt()
                totalValues++
            }
        }
        var newText = ""
        var isCorrect = false

        // check if the text value is correct and passes all expectations
        if (type == TYPE_RGB) {

            // only 3 numbers and each one is in range
            if (totalValues == 3 && RGBA.inRangeR(intValues[0]) &&
                RGBA.inRangeG(intValues[1]) && RGBA.inRangeB(intValues[2])
            ) {
                colorConverter.rgba.setRGBA(intValues[0], intValues[1], intValues[2])
                newText = colorConverter.rgba.getString(false, false)
                isCorrect = true
            } else {
                newText = cachedTextValue
            }
        } else if (type == TYPE_RGBA) {

            // only 4 numbers and each one is in range
            if (totalValues == 4 &&
                RGBA.inRangeR(intValues[0]) && RGBA.inRangeG(intValues[1]) &&
                RGBA.inRangeB(intValues[2]) && RGBA.inRangeA(intValues[3])
            ) {
                colorConverter.rgba.setRGBA(intValues[0], intValues[1], intValues[2], intValues[3])
                newText = colorConverter.rgba.getString(true, false)
                isCorrect = true
            } else {
                newText = cachedTextValue
            }
        } else if (type == TYPE_HSV) {

            // only 3 numbers and each one is in range
            if (totalValues == 3 && HSV.inRangeH(intValues[0]) &&
                HSV.inRangeS(intValues[1]) && HSV.inRangeV(intValues[2])
            ) {
                colorConverter.hsv.setHSV(intValues[0], intValues[1], intValues[2])
                newText = colorConverter.hsv.getString(false)
                isCorrect = true
            } else {
                newText = cachedTextValue
            }
        } else if (type == TYPE_HSL) {

            // only 3 numbers and each one is in range
            if (totalValues == 3 && HSL.inRangeH(intValues[0]) &&
                HSL.inRangeS(intValues[1]) && HSL.inRangeL(intValues[2])
            ) {
                colorConverter.hsl.setHSL(intValues[0], intValues[1], intValues[2])
                newText = colorConverter.hsl.getString(false)
                isCorrect = true
            } else {
                newText = cachedTextValue
            }
        } else if (type == TYPE_HWB) {

            // only 3 numbers and each one is in range
            if (totalValues == 3 && HWB.inRangeH(intValues[0]) &&
                HWB.inRangeW(intValues[1]) && HWB.inRangeB(intValues[2])
            ) {
                colorConverter.hwb.setHWB(intValues[0], intValues[1], intValues[2])
                newText = colorConverter.hwb.getString(false)
                isCorrect = true
            } else {
                newText = cachedTextValue
            }
        } else if (type == TYPE_CMYK) {

            // only 4 numbers and each one is in range
            if (totalValues == 4 &&
                CMYK.inRangeC(intValues[0]) && CMYK.inRangeM(intValues[1]) &&
                CMYK.inRangeY(intValues[2]) && CMYK.inRangeK(intValues[3])
            ) {
                colorConverter.cmyk.setCMYK(intValues[0], intValues[1], intValues[2], intValues[3])
                newText = colorConverter.cmyk.getString(false)
                isCorrect = true
            } else {
                newText = cachedTextValue
            }
        }

        // update text view value or restore previous using cache if value is not correct
        if (textView is EditText) {
            val del = textView.text.length - newText.length
            val caretPosition = newCaretPosition
            cachedTextValue = newText
            textView.setText(newText)
            textView.setSelection(caretPosition - del)
        } else {
            textView.text = newText
        }
        return isCorrect
    }

    /**
     * Check if text view value matches single integer value, and the value is in the expected
     * range that include text types like TYPE_RGBA_R, TYPE_RGBA_G...
     * @param textView text view whose value is checked
     * @param type text view input type -TYPE_RGBA, TYPE_RGBA_A...
     * @return if the expected text patter is correct for the current text view
     */
    internal fun checkSingeInt(textView: TextView, type: Int = textViews[textView]!!): Boolean {

        // replace all characters except digit 0-9
        val newText = textView.text.toString().replace("\\D+".toRegex(), "")

        // get value as int
        var value = if (newText.isEmpty()) 0 else newText.toInt()
        var inRange = false

        // check if value is in range depending on the text view type
        if (type == TYPE_RGBA_R) {
            inRange = RGBA.inRangeR(value)
            if (inRange) {
                colorConverter.rgba.r = value
            }
        } else if (type == TYPE_RGBA_G) {
            inRange = RGBA.inRangeG(value)
            if (inRange) {
                colorConverter.rgba.g = value
            }
        } else if (type == TYPE_RGBA_B) {
            inRange = RGBA.inRangeB(value)
            if (inRange) {
                colorConverter.rgba.b = value
            }
        } else if (type == TYPE_RGBA_A) {
            inRange = RGBA.inRangeA(value)
            if (inRange) {
                colorConverter.rgba.a = value
            }
        } else if (type == TYPE_HSV_H) {
            inRange = HSV.inRangeH(value)
            if (inRange) {
                colorConverter.hsv.h = value
            }
        } else if (type == TYPE_HSV_S) {
            inRange = HSV.inRangeS(value)
            if (inRange) {
                colorConverter.hsv.s = value
            }
        } else if (type == TYPE_HSV_V) {
            inRange = HSV.inRangeV(value)
            if (inRange) {
                colorConverter.hsv.v = value
            }
        } else if (type == TYPE_HSL_H) {
            inRange = HSL.inRangeH(value)
            if (inRange) {
                colorConverter.hsl.h = value
            }
        } else if (type == TYPE_HSL_S) {
            inRange = HSL.inRangeS(value)
            if (inRange) {
                colorConverter.hsl.s = value
            }
        } else if (type == TYPE_HSL_L) {
            inRange = HSL.inRangeL(value)
            if (inRange) {
                colorConverter.hsl.l = value
            }
        } else if (type == TYPE_HWB_H) {
            inRange = HWB.inRangeH(value)
            if (inRange) {
                colorConverter.hwb.h = value
            }
        } else if (type == TYPE_HWB_W) {
            inRange = HWB.inRangeW(value)
            if (inRange) {
                colorConverter.hwb.w = value
            }
        } else if (type == TYPE_HWB_B) {
            inRange = HWB.inRangeB(value)
            if (inRange) {
                colorConverter.hwb.b = value
            }
        } else if (type == TYPE_CMYK_C) {
            inRange = CMYK.inRangeC(value)
            if (inRange) {
                colorConverter.cmyk.c = value
            }
        } else if (type == TYPE_CMYK_M) {
            inRange = CMYK.inRangeM(value)
            if (inRange) {
                colorConverter.cmyk.m = value
            }
        } else if (type == TYPE_CMYK_Y) {
            inRange = CMYK.inRangeY(value)
            if (inRange) {
                colorConverter.cmyk.y = value
            }
        } else if (type == TYPE_CMYK_K) {
            inRange = CMYK.inRangeK(value)
            if (inRange) {
                colorConverter.cmyk.k = value
            }
        }

        if (!inRange) {
            value = cachedTextValue.replace("\\D+".toRegex(), "").toInt()
        }

        // change text value if it is in range
        if (textView is EditText) {
            val n = "" + value
            val del = textView.text.length - n.length
            val caretPosition = newCaretPosition
            cachedTextValue = n
            textView.setText(n)
            textView.setSelection(caretPosition - del)
        } else {
            textView.text = "$value"
        }
        return inRange
    }

    interface OnTextChangeListener {
        fun onTextChange(colorConverter: ColorConverter?, sender: TextView?)
    }

    interface OnUpdateListener {

        /**
         * Method called when text view value is changed by the user.
         * @param textView sender text view whose value is changed
         */
        fun onTextViewUpdate(textView: TextView)

        /**
         * Method called when color window value is changed by the user.
         * @param colorWindow sender color window whose value is changed
         */
        fun onColorWindowUpdate(colorWindow: Base)
    }

    fun setOnUpdateListener(onUpdateListener: OnUpdateListener) {
        this.onUpdateListener = onUpdateListener
    }

    companion object {

        // text view available types, that are attached to certain - text view using the tag attribute
        const val TYPE_NONE = 0
        const val TYPE_RGB = 1
        const val TYPE_RGBA = 2
        const val TYPE_RGBA_R = 3
        const val TYPE_RGBA_G = 4
        const val TYPE_RGBA_B = 5
        const val TYPE_RGBA_A = 6
        const val TYPE_HSV = 7
        const val TYPE_HSV_H = 8
        const val TYPE_HSV_S = 9
        const val TYPE_HSV_V = 10
        const val TYPE_HSL = 11
        const val TYPE_HSL_H = 12
        const val TYPE_HSL_S = 13
        const val TYPE_HSL_L = 14
        const val TYPE_HWB = 15
        const val TYPE_HWB_H = 16
        const val TYPE_HWB_W = 17
        const val TYPE_HWB_B = 18
        const val TYPE_CMYK = 19
        const val TYPE_CMYK_C = 20
        const val TYPE_CMYK_M = 21
        const val TYPE_CMYK_Y = 22
        const val TYPE_CMYK_K = 23
        const val TYPE_HEX = 24
        const val TYPE_HEXA = 25

        /**
         * Get text view type(as integer representation), by passing it as string argument.
         * This is method is used only once on initialization, since checking strings are
         * cost operations.
         * @param strTag string tag showing the text view expected type
         * @return integer representation of text view type
         */
        fun getType(strTag: String): Int {
            if (strTag.isNotEmpty()) {
                when (strTag.toLowerCase()) {
                    "rgb" -> return TYPE_RGB
                    "rgba" -> return TYPE_RGBA
                    "rgba_r" -> return TYPE_RGBA_R
                    "rgba_g" -> return TYPE_RGBA_G
                    "rgba_b" -> return TYPE_RGBA_B
                    "rgba_a" -> return TYPE_RGBA_A
                    "hsv" -> return TYPE_HSV
                    "hsv_h" -> return TYPE_HSV_H
                    "hsv_s" -> return TYPE_HSV_S
                    "hsv_v" -> return TYPE_HSV_V
                    "hsl" -> return TYPE_HSL
                    "hsl_h" -> return TYPE_HSL_H
                    "hsl_s" -> return TYPE_HSL_S
                    "hsl_l" -> return TYPE_HSL_L
                    "hwb" -> return TYPE_HWB
                    "hwb_h" -> return TYPE_HWB_H
                    "hwb_w" -> return TYPE_HWB_W
                    "hwb_b" -> return TYPE_HWB_B
                    "hex" -> return TYPE_HEX
                    "hexa" -> return TYPE_HEXA
                    "cmyk" -> return TYPE_CMYK
                    "cmyk_c" -> return TYPE_CMYK_C
                    "cmyk_m" -> return TYPE_CMYK_M
                    "cmyk_y" -> return TYPE_CMYK_Y
                    "cmyk_k" -> return TYPE_CMYK_K
                }
            }
            return TYPE_NONE
        }

        /**
         * Static method that returns text view type as integer representation.
         * @param textView text view whose integer type representation will be returned
         * @return integer representation of text view type
         */
        fun getType(textView: TextView): Int {
            val tag = textView.tag
            val strTag = tag?.toString() ?: ""
            return getType(strTag)
        }
    }

}