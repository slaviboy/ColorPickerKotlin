package com.slaviboy.colorpicker

import android.text.Editable
import android.text.InputFilter
import android.text.InputFilter.LengthFilter
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.TextView
import com.slaviboy.colorpicker.converter.ColorConverter
import com.slaviboy.colorpicker.models.*
import com.slaviboy.colorpicker.window.Base
import com.slaviboy.colorpicker.window.Base.OnUpdateListener
import com.slaviboy.colorpicker.window.Circular
import com.slaviboy.colorpicker.window.Rectangular
import com.slaviboy.colorpicker.window.Slider
import com.slaviboy.colorpicker.windows.circular.CircularHS
import com.slaviboy.colorpicker.windows.rectangular.RectangularSL
import com.slaviboy.colorpicker.windows.rectangular.RectangularSV
import com.slaviboy.colorpicker.windows.slider.SliderA
import com.slaviboy.colorpicker.windows.slider.SliderH
import com.slaviboy.colorpicker.windows.slider.SliderV
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
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
 * Update class is the class that takes care for updating text views and color windows.
 * The only thing you need to do is to attach different components and the class will
 * take care for the rest. That way it is easy to create custom color pickers.
 * @param colorConverter object that holds the base color, and selected color for the pickers
 * @param colorHolder global color converter object, that holds colorHolder for the window
 */
class Updater(
    val colorConverter: ColorConverter = ColorConverter(),
    val colorHolder: ColorHolder = ColorHolder()
) : OnUpdateListener {

    constructor(colorHolder: ColorHolder) : this(ColorConverter(), colorHolder)

    private val colorWindows: MutableList<Base>                        // list with all attached color windows
    private val textViews: HashMap<TextView, Int>                      // hash map with text view as key and type as value
    private var isInnerTextChange: Boolean                             // flag showing if setText() is called from inside this class, if false then it is called by the user from the UI
    private var newCaretPosition: Int                                  // new caret position after user types in edit text
    private lateinit var cachedTextValue: String                       // cached value that is set when text view is focused by the user
    private lateinit var onTextChangeListener: OnTextChangeListener    // when text change listener is set, in the onTextChange() method you specify how other text views values changes
    private lateinit var onUpdateListener: OnUpdateListener            // update listener, from which you can listen for text view or color window change by the user

    init {

        colorWindows = ArrayList()
        textViews = HashMap()
        isInnerTextChange = false
        newCaretPosition = 0

        colorHolder.onConvert(colorConverter)            // initial call, to match the default color
        colorConverter.setOnConvertListener(colorHolder) // attach the listener to the color holder
    }

    /**
     * Attach text view to the updater class with certain type, that shows what text value will expected
     * as input and output, from which the other attached color windows and text values will be updated
     * synchronously.
     * @param textView text view that will be attached
     * @param type text view type
     */
    fun attachTextView(textView: TextView, type: Int) {

        // make sure value is not already in the hash map
        if (!textViews.containsKey(textView)) {
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
            } else if (type == TYPE_RGB || type == TYPE_RGBA || type == TYPE_HSV || type == TYPE_HSL || type == TYPE_CMYK) {
                // multiple values expected
            } else {
                // for single integer values limit to 3 digits
                textView.filters = arrayOf<InputFilter>(LengthFilter(3))
            }

            textView.isCursorVisible = false
            textViews[textView] = type

            // update text after it is put into the list array
            updateTextView(textView)
        }
    }

    fun updateTextViewTag(textView: TextView, type: Int) {

        if (textViews.containsKey(textView)) {

            if (type == TYPE_HEX) {
                // for hex limit to 7 digits
                textView.filters = arrayOf<InputFilter>(LengthFilter(7))
            } else if (type == TYPE_RGB || type == TYPE_RGBA || type == TYPE_HSV || type == TYPE_HSL || type == TYPE_CMYK) {
                // multiple values expected
            } else {
                // for single integer values limit to 3 digits
                textView.filters = arrayOf<InputFilter>(LengthFilter(3))
            }

            textView.isCursorVisible = false
            textViews[textView] = type
            updateTextView(textView)
        }
    }

    /**
     * Attach text view with preset type: hex
     * @param textViewHEX - text view with hexadecimal type
     */
    fun attachTextViewHEX(textViewHEX: TextView) {
        attachTextView(textViewHEX, TYPE_HEX)
    }

    /**
     * Attach text view with preset type: rgb
     * @param textViewRGB text view with combine type: rgb(red,green,blue)
     */
    fun attachTextViewRGB(textViewRGB: TextView) {
        attachTextView(textViewRGB, TYPE_RGB)
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
     * Attach text view with preset type: rgba
     * @param textViewRGBA text view with combine type: rgba(red,green,blue,alpha)
     */
    fun attachTextViewRGBA(textViewRGBA: TextView) {
        attachTextView(textViewRGBA, TYPE_RGBA)
    }

    /**
     * Attach text view with preset type: hsv.
     * @param textViewHSV text view with combine type: hsv(hue,saturation,value)
     */
    fun attachTextViewHSV(textViewHSV: TextView) {
        attachTextView(textViewHSV, TYPE_HSV)
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
     * Attach text view with preset type: hsv.
     * @param textViewHSL text view with combine type: hsl(hue,saturation,lightness)
     */
    fun attachTextViewHSL(textViewHSL: TextView) {
        attachTextView(textViewHSL, TYPE_HSL)
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
     * Attach text view with preset type: hwb.
     * @param textViewHWB text view with combine type: hwb(hue,white,black)
     */
    fun attachTextViewHWB(textViewHWB: TextView) {
        attachTextView(textViewHWB, TYPE_HWB)
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
     * Attach text view with preset type: cmyk.
     * @param textViewCMYK text view with combine type: cmyk(cyan,magenta,yellow,black)
     */
    fun attachTextViewCMYK(textViewCMYK: TextView) {
        attachTextView(textViewCMYK, TYPE_CMYK)
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
     * @param colorWindow - color window that will be attached
     */
    private fun attachColorWindow(colorWindow: Base) {

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
     * Attach text views passed as list array argument. Each text view must have set tag property
     * set to its corresponding text view type. That way the updater knows what values to set to
     * each separate text view.
     * @param textViews - text views that will be attached
     */
    fun attachTextViews(textViews: List<TextView>) {

        // attach multiple text views using array list
        for (i in textViews.indices) {
            val textView = textViews[i]
            attachTextView(textView)
        }
    }

    /**
     * Attach text view and get type by checking the tag value for expected string values. The tag is set
     * using xml, or set using the setTag() method.
     * @param textViews -text view that will be attached
     */
    fun attachTextView(textViews: TextView) {

        // get string tag, to get type and attach text view
        val strTag = textViews.tag.toString()

        // attach only if proper type is set, and is supported by the updater class
        val type = getType(strTag)
        if (type in TYPE_RGB..TYPE_HEX) {
            attachTextView(textViews, type)
        }
    }

    /**
     * Method called when text view is focused, to get cached values and remove unwanted
     * symbols for multiple values text view.
     * Example HSV: '130°, 45%, 32%' will be converted to '130 45 32'
     * @param textView text view that is being focused
     * @param type text view type
     */
    private fun focus(textView: TextView, type: Int) {
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
     * Method called when text view is unfocused to hide caret and restore special symbols using
     * the color converter object, and returning the wanted values with special characters.
     * Example HSV: '130 45 32' will be converted to '130°, 45%, 32%'
     * @param textView text view that is being unfocused
     * @param type text view type
     */
    private fun unfocus(textView: TextView, type: Int) {

        // set text value by including special symbols
        val withSymbols = true
        val withSymbolsStr: String?
        withSymbolsStr = when (type) {
            TYPE_RGB -> {
                colorConverter.getRGB(withSymbols)
            }
            TYPE_RGBA -> {
                colorConverter.getRGBA(withSymbols)
            }
            TYPE_HSV -> {
                colorConverter.getHSV(withSymbols)
            }
            TYPE_HSL -> {
                colorConverter.getHSL(withSymbols)
            }
            TYPE_HWB -> {
                colorConverter.getHWB(withSymbols)
            }
            TYPE_CMYK -> {
                colorConverter.getCMYK(withSymbols)
            }
            else -> {
                cachedTextValue
            }
        }
        textView.text = withSymbolsStr
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
     * Method that update all color windows except the sender color window. The update includes
     * changing the color converter and then redrawing and updating the selector position, for
     * all color windows that are included.
     * @param colorWindow sender color window whose selector is being moved by the user
     */
    private fun updateColorWindows(colorWindow: Base) {

        if (colorWindow is SliderH) {
            val w = colorWindow as Slider

            // hue for HSV & HSL
            val h = w.range.current.roundToInt()
            if (colorConverter.h != h) {
                colorConverter.h = h

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
            val a = w.range.current.roundToInt()
            if (colorConverter.a != a) {
                colorConverter.a = a

                // redraw and update other color windows
                for (i in colorWindows.indices) {
                    val tempWindow = colorWindows[i]
                    (tempWindow as? SliderA)?.update()
                }
            }
        } else if (colorWindow is SliderV) {
            val w = colorWindow as Slider

            // value for HSV
            val v = w.range.current.roundToInt()
            if (colorConverter.v != v) {
                colorConverter.v = v

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
            if (colorConverter.v != v || colorConverter.getS(ColorConverter.MODEL_HSV) != s) {

                colorConverter.setHSV(colorConverter.h, s, v)

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
            if (colorConverter.l != l || colorConverter.getS(ColorConverter.MODEL_HSL) != s) {

                colorConverter.setHSL(colorConverter.h, s, l)

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

            // saturation and hude for HSV
            val s = w.distanceRange.current.roundToInt()
            val h = w.angleRange.current.roundToInt()
            if (colorConverter.h != h || colorConverter.getS(ColorConverter.MODEL_HSV) != s) {

                colorConverter.setHSV(h, s, colorConverter.v)

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
    private fun updateTextViews(sender: TextView? = null) {

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
     * @param textView
     */
    private fun updateTextView(textView: TextView) {

        val type = textViews[textView]!!
        var intValue = -1

        // get integer value for single text view type// convert integer to string for single value types

        // if there is a focused edit text then set caret at the beginning
        // get as string for multiple value type
        when (type) {
            TYPE_RGBA_R -> {
                intValue = colorConverter.r
            }
            TYPE_RGBA_G -> {
                intValue = colorConverter.g
            }
            TYPE_RGBA_B -> {
                intValue = colorConverter.getB(ColorConverter.MODEL_RGBA)
            }
            TYPE_RGBA_A -> {
                intValue = colorConverter.a
            }
            TYPE_HSV_H -> {
                intValue = colorConverter.h
            }
            TYPE_HSV_S -> {
                intValue = colorConverter.getS(ColorConverter.MODEL_HSV)
            }
            TYPE_HSV_V -> {
                intValue = colorConverter.v
            }
            TYPE_HSL_H -> {
                intValue = colorConverter.h
            }
            TYPE_HSL_S -> {
                intValue = colorConverter.getS(ColorConverter.MODEL_HSL)
            }
            TYPE_HSL_L -> {
                intValue = colorConverter.l
            }
            TYPE_CMYK_C -> {
                intValue = colorConverter.c
            }
            TYPE_CMYK_M -> {
                intValue = colorConverter.m
            }
            TYPE_CMYK_Y -> {
                intValue = colorConverter.y
            }
            TYPE_CMYK_K -> {
                intValue = colorConverter.k
            }
        }

        // get the new string value
        val value: String
        value = if (intValue == -1) {

            // get as string for multiple value type
            when (type) {
                TYPE_HEX -> {
                    colorConverter.HEX
                }
                TYPE_RGB -> {
                    colorConverter.getRGB(true)
                }
                TYPE_RGBA -> {
                    colorConverter.getRGBA(true)
                }
                TYPE_HSV -> {
                    colorConverter.getHSV(true)
                }
                TYPE_HSL -> {
                    colorConverter.getHSL(true)
                }
                TYPE_HWB -> {
                    colorConverter.getHWB(true)
                }
                TYPE_CMYK -> {
                    colorConverter.getCMYK(true)
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
    private fun beforeTextChanged(start: Int, after: Int) {

        // get new caret position the text view will have after the text change
        newCaretPosition = start + after
    }

    /**
     * After text change is made by the used, check if the new text value matches all expected text
     * pattern, that mean the new value passes all the restrictions.
     * @param editable editable holding the new text value
     */
    private fun afterTextChanged(editable: Editable) {

        // if user is changing the text value using the UI
        if (!isInnerTextChange) {

            // set flag showing that inner(from this class) text vales change will be made
            isInnerTextChange = true
            if (!editable.toString().equals("", ignoreCase = true)) {
                for (temp in textViews.keys) {

                    // found which text view value is changed by the user
                    if (temp.text.hashCode() == editable.hashCode() &&
                        temp.isFocused
                    ) {

                        // check if the text change is allowed
                        checkTextView(temp)
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
    private fun checkTextView(textView: TextView) {
        val type = textViews[textView]!!
        val isValueUpdated: Boolean
        isValueUpdated = if (type == TYPE_HEX) {
            // hex expected
            checkHEX(textView)
        } else if (type == TYPE_RGB || type == TYPE_RGBA || type == TYPE_HSV || type == TYPE_HSL || type == TYPE_CMYK) {
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
     * Method checks if text view value matches the expected HEX text pattern, the includes 7 symbols
     * length expectation and only hex symbols.
     * @param textView text view whose value is checked
     * @return if the expected text patter is correct for the current text view
     */
    private fun checkHEX(textView: TextView): Boolean {

        // remove all symbols except the hexadecimal ones
        val newText = "#" + textView.text.toString().replace("[^a-f0-9A-F]+".toRegex(), "").toUpperCase()
        var isCorrect = false

        // make sure hex string length is matched
        if (newText.length == 7) {
            colorConverter.HEX = newText
            isCorrect = true
        }

        // change text value
        if (textView is EditText) {
            val del = textView.text.length - newText.length
            val caretPosition = newCaretPosition
            cachedTextValue = newText

            // set caret position after inner text change
            textView.setText(newText)
            textView.setSelection(caretPosition - del)
        } else {
            textView.text = newText
        }
        return isCorrect
    }

    /**
     * Check if text view value matched multiple integer value, that includes text views holding multiple
     * values like: RGB, RGBA, HSV, HSL and CMYK.
     * @param textView text view whose value is checked
     * @param type text view type
     * @return if the expected text patter is correct for the current text view
     */
    private fun checkMultipleInt(textView: TextView, type: Int): Boolean {
        val newTextArray = textView.text.toString()
            .replace("[^0-9 ]+".toRegex(), "") // leave only numbers and white space
            .replace(" +".toRegex(), " ") // remove white spaces that are glued together
            .split(" ".toRegex()).toTypedArray() // split by white space to get numbers as string array

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
                colorConverter.setRGB(intValues[0], intValues[1], intValues[2])
                newText = colorConverter.getRGB(false)
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
                colorConverter.setRGBA(intValues[0], intValues[1], intValues[2], intValues[3])
                newText = colorConverter.getRGBA(false)
                isCorrect = true
            } else {
                newText = cachedTextValue
            }
        } else if (type == TYPE_HSV) {

            // only 3 numbers and each one is in range
            if (totalValues == 3 && HSV.inRangeH(intValues[0]) &&
                HSV.inRangeS(intValues[1]) && HSV.inRangeV(intValues[2])
            ) {
                colorConverter.setHSV(intValues[0], intValues[1], intValues[2])
                newText = colorConverter.getHSV(false)
                isCorrect = true
            } else {
                newText = cachedTextValue
            }
        } else if (type == TYPE_HSL) {

            // only 3 numbers and each one is in range
            if (totalValues == 3 && HSL.inRangeH(intValues[0]) &&
                HSL.inRangeS(intValues[1]) && HSL.inRangeL(intValues[2])
            ) {
                colorConverter.setHSL(intValues[0], intValues[1], intValues[2])
                newText = colorConverter.getHSL(false)
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
                colorConverter.setCMYK(intValues[0], intValues[1], intValues[2], intValues[3])
                newText = colorConverter.getCMYK(false)
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
    private fun checkSingeInt(textView: TextView, type: Int): Boolean {

        // replace all characters except digit 0-9
        val newText = textView.text.toString().replace("\\D+".toRegex(), "")

        // get value as int
        var value = if (newText.isEmpty()) 0 else newText.toInt()
        var inRange = false

        // check if value is in range depending on the text view type
        if (type == TYPE_RGBA_R) {
            inRange = RGBA.inRangeR(value)
            if (inRange) {
                colorConverter.r = value
            }
        } else if (type == TYPE_RGBA_G) {
            inRange = RGBA.inRangeG(value)
            if (inRange) {
                colorConverter.g = value
            }
        } else if (type == TYPE_RGBA_B) {
            inRange = RGBA.inRangeB(value)
            if (inRange) {
                colorConverter.setB(value, ColorConverter.MODEL_RGBA)
            }
        } else if (type == TYPE_RGBA_A) {
            inRange = RGBA.inRangeA(value)
            if (inRange) {
                colorConverter.a = value
            }
        } else if (type == TYPE_HSV_H) {
            inRange = HSV.inRangeH(value)
            if (inRange) {
                colorConverter.h = value
            }
        } else if (type == TYPE_HSV_S) {
            inRange = HSV.inRangeS(value)
            if (inRange) {
                colorConverter.setS(value, ColorConverter.MODEL_HSV)
            }
        } else if (type == TYPE_HSV_V) {
            inRange = HSV.inRangeV(value)
            if (inRange) {
                colorConverter.v = value
            }
        } else if (type == TYPE_HSL_H) {
            inRange = HSL.inRangeH(value)
            if (inRange) {
                colorConverter.h = value
            }
        } else if (type == TYPE_HSL_S) {
            inRange = HSL.inRangeS(value)
            if (inRange) {
                colorConverter.setS(value, ColorConverter.MODEL_HSL)
            }
        } else if (type == TYPE_HSL_L) {
            inRange = HSL.inRangeL(value)
            if (inRange) {
                colorConverter.l = value
            }
        } else if (type == TYPE_HWB_H) {
            inRange = HWB.inRangeH(value)
            if (inRange) {
                colorConverter.h = value
            }
        } else if (type == TYPE_HWB_W) {
            inRange = HWB.inRangeW(value)
            if (inRange) {
                colorConverter.w = value
            }
        } else if (type == TYPE_HWB_B) {
            inRange = HWB.inRangeB(value)
            if (inRange) {
                colorConverter.setB(value, ColorConverter.MODEL_HWB)
            }
        } else if (type == TYPE_CMYK_C) {
            inRange = CMYK.inRangeC(value)
            if (inRange) {
                colorConverter.c = value
            }
        } else if (type == TYPE_CMYK_M) {
            inRange = CMYK.inRangeM(value)
            if (inRange) {
                colorConverter.m = value
            }
        } else if (type == TYPE_CMYK_Y) {
            inRange = CMYK.inRangeY(value)
            if (inRange) {
                colorConverter.y = value
            }
        } else if (type == TYPE_CMYK_K) {
            inRange = CMYK.inRangeK(value)
            if (inRange) {
                colorConverter.k = value
            }
        }

        if (!inRange) {
            value = cachedTextValue.toInt()
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

    fun setOnTextChangeListener(onTextChangeListener: OnTextChangeListener) {
        this.onTextChangeListener = onTextChangeListener
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
         * @param textView - text view whose integer type representation will be returned
         * @return integer representation of text view type
         */
        fun getType(textView: TextView): Int {
            val tag = textView.tag
            val strTag = tag?.toString() ?: ""
            return getType(strTag)
        }
    }

}