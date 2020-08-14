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
package com.slaviboy.colorpicker.components

import android.content.Context
import android.content.res.TypedArray
import android.graphics.*
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View
import android.view.ViewTreeObserver
import androidx.constraintlayout.widget.ConstraintLayout
import com.slaviboy.colorpicker.data.ColorHolder
import com.slaviboy.colorpicker.data.CornerRadius
import com.slaviboy.colorpicker.R
import com.slaviboy.colorpicker.main.ColorConverter

/**
 * Base class, is abstract class that is extended by the other color windows. It contains properties
 * that are used by the other color windows - Circular, Rectangular and Slider. It has some abstract
 * methods that are common for all color window types, and need to be implemented if new type is created.
 */
abstract class Base : View {

    constructor(context: Context) : super(context) {
        setBaseAttributeSet(context, null)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        setBaseAttributeSet(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        setBaseAttributeSet(context, attrs)
    }

    internal var selectorStrokeWidth: Float                    // selector stroke width
    internal var selectorRadius: Float                         // selector radius
    internal var selectorColor: Int                            // selector color
    internal var selectorExtraStrokeColor: Int                 // stroke color for the extra stroke surrounding the normal selector stroke
    internal var selectorExtraStrokeWidth: Float               // stroke width for the extra stroke surrounding the normal selector stroke
    internal var isTouchDown: Boolean                          // flag showing whether the user finger is currently touching the color window
    internal var borderStrokeWidth: Float                      // border stroke width
    internal var borderColor: Int                              // border color
    internal var isInit: Boolean                               // Flag showing whether the color window is initialized, and can be used.
    internal var halfWidth: Float                              // canvas half width
    internal var halfHeight: Float                             // canvas half height
    internal var selectorX: Float                              // selector x coordinate
    internal var selectorY: Float                              // selector y coordinate
    internal lateinit var selectorPaint: Paint                 // selector paint
    internal lateinit var baseLayer: Bitmap                    // bitmap for the base layer
    internal lateinit var colorLayer: Bitmap                   // bitmap for the color layer
    internal lateinit var layersPaint: Paint                   // paint object for the layers
    internal lateinit var layersCanvas: Canvas                 // canvas object for the layers
    internal lateinit var padding: RectF                       // padding for the color window, so that the selector is kept inside
    lateinit var colorConverter: ColorConverter                // the global color converter object, that converts from one color model to another
    lateinit var colorHolder: ColorHolder                      // the global color holder object, holding base color and selected color
    lateinit var onUpdateListener: OnUpdateListener            // update listener that calls method when selector is moved
    internal lateinit var clipPath: Path                       // color window path used for drawing the color window shape and clip the bitmaps
    internal lateinit var displayMetrics: DisplayMetrics       // used when getting the xml units as pixel, for - dp and sp conversions to px
    internal lateinit var cornerRadius: CornerRadius           // view corner radius
    internal lateinit var unitsString: Array<String>           // string array containing string unit values, from xml properties

    var wrapContentRatio: Float                                // expected width/height ratio if any of the width or height is set as WRAP_CONTENT

    init {

        // set default values
        selectorColor = 0
        borderColor = 0
        isTouchDown = false
        isInit = false
        halfWidth = 0f
        halfHeight = 0f
        selectorX = 0f
        selectorY = 0f
        borderStrokeWidth = 0f
        selectorStrokeWidth = 0f
        selectorRadius = 0f
        selectorExtraStrokeColor = ColorConverter.BLACK
        selectorExtraStrokeWidth = 0f
        wrapContentRatio = 1f
    }

    internal fun setBaseAttributeSet(context: Context, attrs: AttributeSet?) {

        val typedArray: TypedArray = context.obtainStyledAttributes(attrs, R.styleable.Base)

        // get units as string, and after final measurement get the sizes in pixels
        unitsString = arrayOf(
            typedArray.getString(R.styleable.Base_selector_stroke_width) ?: "6px",
            typedArray.getString(R.styleable.Base_selector_radius) ?: "10px",
            typedArray.getString(R.styleable.Base_border_stroke_width) ?: "1px",
            typedArray.getString(R.styleable.Base_corner_radius) ?: "5px",
            typedArray.getString(R.styleable.Base_corner_radius_upper_left) ?: "0px",
            typedArray.getString(R.styleable.Base_corner_radius_upper_right) ?: "0px",
            typedArray.getString(R.styleable.Base_corner_radius_lower_left) ?: "0px",
            typedArray.getString(R.styleable.Base_corner_radius_lower_right) ?: "0px",
            typedArray.getString(R.styleable.Base_selector_extra_stroke_width) ?: "0px"
        )

        // xml attributes
        val defaultWrapContentRatio = if (this is Slider) {
            0.2f
        } else {
            1.0f
        }
        wrapContentRatio = typedArray.getFloat(R.styleable.Base_wrap_content_ratio, defaultWrapContentRatio)
        selectorColor = typedArray.getColor(R.styleable.Base_selector_color, ColorConverter.WHITE)
        selectorExtraStrokeColor = typedArray.getColor(R.styleable.Base_selector_extra_stroke_color, ColorConverter.BLACK)
        borderColor = typedArray.getColor(R.styleable.Base_border_color, ColorConverter.parseColor("#2f000000"))
        typedArray.recycle()

        // get metrics used when converting dp and sp to px
        displayMetrics = context.resources.displayMetrics

        // called before drawing to init some values that need view size
        this.afterMeasured {
            checkForWrapContent()
        }
    }

    /**
     * Check if any of the view has a wrap content set as size,
     * this will make the width match height and vice versa.
     */
    fun checkForWrapContent() {

        // if width set as WRAP_CONTENT then calculate the height using the wrapContentRatio and vice versa
        var newWidth = width
        var newHeight = height
        if (layoutParams.height != layoutParams.width) {
            if (layoutParams.height == ConstraintLayout.LayoutParams.WRAP_CONTENT) {
                newHeight = (width / wrapContentRatio).toInt()
            } else if (layoutParams.width == ConstraintLayout.LayoutParams.WRAP_CONTENT) {
                newWidth = (height * wrapContentRatio).toInt()
            }
        }

        // if change was made
        if (newHeight != height || newWidth != width) {
            layoutParams.width = newWidth
            layoutParams.height = newHeight
            layoutParams = layoutParams
        }

        // since change is made measure again and initialize
        this.afterMeasured {
            onInitBase()
            update()
            onRedraw()
        }
    }

    /**
     * Get unit value in pixels, by passing unit string value, supported unit types are:
     * dp, sp, px, vw(view width) and vh(view height)
     * @param unitStr unit string
     * @return unit value in pixels
     */
    internal fun getUnit(unitStr: String?): Float {
        if (unitStr == null) {
            return 0.0f
        }

        // get unit value
        val value = unitStr
            .substring(0, unitStr.length - 2)
            .replace("[^0-9?!\\.]".toRegex(), "").toFloat()

        // get unit type(last two characters) from the string
        val unit = unitStr.substring(unitStr.length - 2)

        // return the unit value as pixels
        return when (unit) {
            "dp" -> {
                // dp to px
                TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value, displayMetrics)
            }
            "sp" -> {
                // sp to px
                TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, value, displayMetrics)
            }
            "px" -> {
                value
            }
            "vw" -> {
                // as percentage from view width (1.5 = 150%, 2 = 200% ...)
                width * value
            }
            "vh" -> {
                // as percentage from view height (1.5 = 150%, 2 = 200% ...)
                height * value
            }
            else -> {
                0.0f
            }
        }
    }

    /**
     * Method called when final initialization is done, to get view size, set the bitmaps, set selector
     * paint, paint and canvas for the layer are also set boolean variable isInit showing initialization
     * is done. XML unit values are set after view width and height is needed to get unit values as pixels.
     */
    fun onInitBase() {
        // get view width and height
        halfWidth = width / 2f
        halfHeight = height / 2f

        // init attributes from xml
        selectorStrokeWidth = getUnit(unitsString[0])
        selectorRadius = getUnit(unitsString[1])
        borderStrokeWidth = getUnit(unitsString[2])

        // xml corner radius attribute
        val cornerRadiusAll = getUnit(unitsString[3])
        var cornerRadiusUpperLeft = getUnit(unitsString[4])
        var cornerRadiusUpperRight = getUnit(unitsString[5])
        var cornerRadiusLowerLeft = getUnit(unitsString[6])
        var cornerRadiusLowerRight = getUnit(unitsString[7])

        // set corner radii for each corner
        if (cornerRadiusUpperLeft == 0f) {
            cornerRadiusUpperLeft = cornerRadiusAll
        }
        if (cornerRadiusUpperRight == 0f) {
            cornerRadiusUpperRight = cornerRadiusAll
        }
        if (cornerRadiusLowerLeft == 0f) {
            cornerRadiusLowerLeft = cornerRadiusAll
        }
        if (cornerRadiusLowerRight == 0f) {
            cornerRadiusLowerRight = cornerRadiusAll
        }

        cornerRadius = CornerRadius(
            cornerRadiusUpperLeft,
            cornerRadiusUpperRight,
            cornerRadiusLowerLeft,
            cornerRadiusLowerRight
        )

        // get the extra stroke width
        selectorExtraStrokeWidth = getUnit(unitsString[8])

        // get the padding for the selector and set for all sides
        val paddingAll = Math.max(selectorStrokeWidth / 2f + selectorExtraStrokeWidth / 2f + selectorRadius, borderStrokeWidth / 2f)
        padding = RectF(paddingAll, paddingAll, paddingAll, paddingAll)

        // init bitmaps
        baseLayer = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        colorLayer = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

        // set selector paint
        selectorPaint = Paint().apply {
            strokeWidth = selectorStrokeWidth
            color = selectorColor
            style = Paint.Style.STROKE
            isAntiAlias = true
        }

        // init temp paint
        layersPaint = Paint().apply {
            isAntiAlias = true
        }

        // init temp canvas
        layersCanvas = Canvas(baseLayer)
        isInit = true
        onInit()
    }

    fun isOnUpdateListenerInitialised(): Boolean {
        return ::onUpdateListener.isInitialized
    }

    /**
     * Method called when user moves selector to new position corresponding to current
     * finger position on view. Used to change selector position and update ranges.
     * @param x x coordinates
     * @param y y coordinates
     */
    internal abstract fun moveSelector(x: Float, y: Float)

    /**
     * Method called on initialization, after the final measurement is made. Used by color windows
     * to set the base layer bitmap and cached it. That way it is faster for redrawing the canvas.
     */
    internal abstract fun onInit()

    /**
     * Method that is called when view redrawing is need, usually when the base color from the color
     * converter object is changed and colorLayer bitmap need to be redrawn.
     */
    internal abstract fun onRedraw()

    /**
     * Public method used to update selector position using current selected color, from
     * the color converter object. And then redraw the view.
     */
    abstract fun update()

    /**
     * Public method used in some color windows, after base color is changed, to update the
     * bitmaps and then redraw the view.
     */
    abstract fun redraw()

    /**
     * Method that draws border for the color window usually for better user experience
     * since the color window look nicer with dark semi-transparent border. Border color
     * and width(thickness) can be set using the border properties.
     */
    internal open fun drawBorder(canvas: Canvas) {
        if (!::clipPath.isInitialized) {
            return
        }

        // draw border stroke
        layersPaint.apply {
            shader = null
            color = borderColor
            strokeWidth = borderStrokeWidth
            style = Paint.Style.STROKE
        }
        canvas.drawPath(clipPath, layersPaint)
    }

    /**
     * Method that draws the selector on given canvas, method can be override by successor classes
     * and that way set different selector fill styles. Default method is drawing only circle stroke.
     * @param canvas canvas where the selector will be written
     */
    internal open fun drawSelector(canvas: Canvas) {

        selectorPaint.alpha = 255
        selectorPaint.style = Paint.Style.STROKE

        // draw the extras stroke, surrounding the original stroke
        selectorPaint.color = selectorExtraStrokeColor
        selectorPaint.strokeWidth = selectorExtraStrokeWidth
        val halfStrokeWidth = selectorStrokeWidth / 2f
        canvas.drawCircle(selectorX, selectorY, selectorRadius + halfStrokeWidth, selectorPaint)
        canvas.drawCircle(selectorX, selectorY, selectorRadius - halfStrokeWidth, selectorPaint)

        // draw the original stoke
        selectorPaint.color = selectorColor
        selectorPaint.strokeWidth = selectorStrokeWidth
        canvas.drawCircle(selectorX, selectorY, selectorRadius, selectorPaint)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x = event.x
        val y = event.y
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                isTouchDown = true
                moveSelector(x, y)
            }
            MotionEvent.ACTION_MOVE -> {
                moveSelector(x, y)
            }
            MotionEvent.ACTION_UP -> {
                isTouchDown = false
            }
        }
        return true
    }

    override fun onDraw(canvas: Canvas) {
        drawOnCanvas(canvas)
    }

    /**
     * Draw the whole color window, that includes the cached background the selector with its current
     * position and the border for the color window. This method can be used to draw the whole color
     * window on a given canvas.
     * @param canvas canvas where the color window will be drawn
     */
    fun drawOnCanvas(canvas: Canvas) {

        // clear background
        canvas.drawColor(ColorConverter.TRANSPARENT)

        // draw the cached background bitmap
        canvas.drawBitmap(colorLayer, 0f, 0f, null)

        // draw the border
        drawBorder(canvas)

        // draw the selector
        drawSelector(canvas)
    }

    interface OnUpdateListener {

        /**
         * Method is called when the color window is updated by the user(with the finger),
         * that means the selector is moved.
         * @param colorWindow showing from which color window the method is called
         */
        fun onUpdate(colorWindow: Base)
    }

    companion object {

        /**
         * Inline function that is called, when the final measurement is made and
         * the view is about to be draw.
         */
        inline fun View.afterMeasured(crossinline function: View.() -> Unit) {
            viewTreeObserver.addOnGlobalLayoutListener(object :
                ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    if (measuredWidth > 0 && measuredHeight > 0) {
                        viewTreeObserver.removeOnGlobalLayoutListener(this)
                        function()
                    }
                }
            })
        }
    }
}
