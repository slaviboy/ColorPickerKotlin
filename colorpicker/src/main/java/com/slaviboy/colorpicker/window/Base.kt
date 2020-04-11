package com.slaviboy.colorpicker.window

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
import com.slaviboy.colorpicker.ColorHolder
import com.slaviboy.colorpicker.CornerRadius
import com.slaviboy.colorpicker.R
import com.slaviboy.colorpicker.converter.ColorConverter

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
 * Base class, is abstract class that is extended by the other color windows. It contains properties
 * that are used by the other color windows - Circular, Rectangular and Slider. It has some abstract
 * methods that are common for all color window types, and need to be implemented if new type is created.
 */
abstract class Base : View {

    constructor(context: Context) : super(context) {
        init(context, null)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(context, attrs)
    }

    protected var selectorStrokeWidth: Float                    // selector stroke width
    protected var selectorRadius: Float                         // selector radius
    protected var selectorColor: Int                            // selector color
    protected var selectorExtraStrokeColor: Int                 // stroke color for the extra stroke surrounding the normal selector stroke
    protected var selectorExtraStrokeWidth: Float               // stroke width for the extra stroke surrounding the normal selector stroke
    protected var isTouchDown: Boolean                          // flag showing whether the user finger is currently touching the color window
    protected var borderStrokeWidth: Float                      // border stroke width
    protected var borderColor: Int                              // border color
    protected var isInit: Boolean                               // Flag showing whether the color window is initialized, and can be used.
    protected var halfWidth: Float                              // canvas half width
    protected var halfHeight: Float                             // canvas half height
    protected var selectorX: Float                              // selector x coordinate
    protected var selectorY: Float                              // selector y coordinate
    protected lateinit var selectorPaint: Paint                 // selector paint
    protected lateinit var baseLayer: Bitmap                    // bitmap for the base layer
    protected lateinit var colorLayer: Bitmap                   // bitmap for the color layer
    protected lateinit var layersPaint: Paint                   // paint object for the layers
    protected lateinit var layersCanvas: Canvas                 // canvas object for the layers
    protected lateinit var padding: RectF                       // padding for the color window, so that the selector is keep inside
    lateinit var colorConverter: ColorConverter                 // the global color converter object, that converts from one color model to another
    lateinit var colorHolder: ColorHolder                       // the global color holder object, holding base color and selected color
    lateinit var onUpdateListener: OnUpdateListener             // update listener that calls method when selector is moved
    protected lateinit var clipPath: Path                       // color window path used for drawing the color window shape and clip the bitmaps
    protected lateinit var bound: RectF                         // boundary for the color window shape
    protected lateinit var displayMetrics: DisplayMetrics       // used when getting the xml units as pixel, for - dp and sp conversions to px
    protected lateinit var cornerRadius: CornerRadius           // view corner radius
    private lateinit var unitsString: Array<String>             // string array containing string unit values, from xml properties

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
        selectorExtraStrokeColor = Color.BLACK
        selectorExtraStrokeWidth = 0f
    }

    private fun init(context: Context, attrs: AttributeSet?) {

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

        // selector xml attributes
        selectorColor = typedArray.getColor(R.styleable.Base_selector_color, Color.WHITE)
        selectorExtraStrokeColor = typedArray.getColor(R.styleable.Base_selector_extra_stroke_color, Color.BLACK)
        borderColor = typedArray.getColor(R.styleable.Base_border_color, Color.parseColor("#2f000000"))
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

        // match width and height to have same value if any of is WRAP_CONTENT
        var newWidth = width
        var newHeight = height
        if (layoutParams.height != layoutParams.width) {
            if (layoutParams.height == ConstraintLayout.LayoutParams.WRAP_CONTENT) {
                newHeight = width
            } else if (layoutParams.width == ConstraintLayout.LayoutParams.WRAP_CONTENT) {
                newWidth = height
            }
        }

        // if change is made
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
    protected fun getUnit(unitStr: String?): Float {
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
    private fun onInitBase() {
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
    protected abstract fun onMove(x: Float, y: Float)

    /**
     * Method called on initialization, after the final measurement is made. Used by color windows
     * to set the base layer bitmap and cached it. That way it is faster for redrawing the canvas.
     */
    protected abstract fun onInit()

    /**
     * Method that is called when view redrawing is need, usually when the base color from the color
     * converter object is changed and colorLayer bitmap need to be redrawn.
     */
    protected abstract fun onRedraw()

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
    protected open fun drawBorder(canvas: Canvas) {
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
    protected open fun drawSelector(canvas: Canvas) {

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
                onMove(x, y)
            }
            MotionEvent.ACTION_MOVE -> {
                onMove(x, y)
            }
            MotionEvent.ACTION_UP -> {
                isTouchDown = false
            }
        }
        return true
    }

    protected override fun onDraw(canvas: Canvas) {

        // clear background
        canvas.drawColor(Color.TRANSPARENT)

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
