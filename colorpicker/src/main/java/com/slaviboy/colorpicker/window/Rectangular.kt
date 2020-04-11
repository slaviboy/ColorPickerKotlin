package com.slaviboy.colorpicker.window

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.util.AttributeSet
import com.slaviboy.colorpicker.CornerRadius
import com.slaviboy.colorpicker.R
import com.slaviboy.colorpicker.Range

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
 * Rectangular class represent a rectangular color window model, as the selector follows a rectangular
 * path. The model is based on 2D(Two Dimensional) color window, that means it contains two ranges for
 * each dimension, horizontal for X-coordinates and vertical for Y-coordinates.
 */
open class Rectangular : Base {
    constructor(context: Context) : super(context) {
        init(context, null)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(context, attrs)
    }

    lateinit var verticalRange: Range     // vertical direction values range
    lateinit var horizontalRange: Range   // horizontal direction values range

    /**
     * Method called to get the xml attribute values.
     * @param context context
     * @param attrs attribute set
     */
    private fun init(context: Context, attrs: AttributeSet?) {

        val typedArray: TypedArray = context.obtainStyledAttributes(attrs, R.styleable.Rectangular)

        // xml ranges
        val verticalRangeLower = typedArray.getFloat(R.styleable.Rectangular_vertical_range_lower, 0f)
        val verticalRangeUpper = typedArray.getFloat(R.styleable.Rectangular_vertical_range_upper, 100f)
        val horizontalRangeLower = typedArray.getFloat(R.styleable.Rectangular_horizontal_range_lower, 0f)
        val horizontalRangeUpper = typedArray.getFloat(R.styleable.Rectangular_horizontal_range_upper, 100f)

        verticalRange = Range(verticalRangeLower, verticalRangeUpper)
        horizontalRange = Range(horizontalRangeLower, horizontalRangeUpper)

        typedArray.recycle()
    }

    override fun onInit() {

        // set bound and clip path
        bound = RectF(padding.left, padding.top, width - padding.right, height - padding.bottom)
        clipPath = roundRect(bound.left, bound.top, bound.width(), bound.height(), cornerRadius)
    }

    override fun onRedraw() {

        // redraw view
        invalidate()
    }

    override fun onMove(x: Float, y: Float) {

        var x = x
        var y = y

        // make sure selector does not pass the canvas boundary
        if (x < padding.left) x = padding.left else if (x >= width - padding.right) x = width - 1f - padding.right
        if (y < padding.top) y = padding.top else if (y >= height - padding.bottom) y = height - 1f - padding.bottom

        // set new selector position
        selectorX = x
        selectorY = y

        // set current horizontal and vertical range values
        verticalRange.setCurrent(
            height - 1f - padding.bottom - padding.top,
            y - padding.top
        )
        horizontalRange.setCurrent(
            width - 1f - padding.left - padding.right,
            x - padding.left
        )

        // call update if listener exist
        if (isOnUpdateListenerInitialised()) {
            onUpdateListener.onUpdate(this)
        }

        // force view redraw
        invalidate()
    }

    override fun drawSelector(canvas: Canvas) {

        // set fill color
        val fillColor = colorHolder.selectedColor
        selectorPaint.color = fillColor
        selectorPaint.style = Paint.Style.FILL
        canvas.drawCircle(selectorX, selectorY, selectorRadius, selectorPaint)

        super.drawSelector(canvas)
    }

    override fun update() {}
    override fun redraw() {
        onRedraw()
    }

    companion object {

        /**
         * Method that returns round rectangle path, with different corner radii for each
         * corner, by given position and rectangle size.
         * @param x left coordinate
         * @param y top coordinate
         * @param width rectangle width
         * @param height rectangle height
         * @param radius corner radius object
         * @return round rectangular path, specify by the arguments
         */
        fun roundRect(x: Float, y: Float, width: Float, height: Float, radius: CornerRadius): Path {

            // make sure corner radius is in range
            val min = Math.min(width, height)
            var upperLeft = radius.upperLeft
            var upperRight = radius.upperRight
            var lowerLeft = radius.lowerLeft
            var lowerRight = radius.lowerRight
            if (min < 2f * upperLeft) upperLeft = min / 2f
            if (min < 2f * upperRight) upperRight = min / 2f
            if (min < 2f * lowerLeft) lowerLeft = min / 2f
            if (min < 2f * lowerRight) lowerRight = min / 2f

            // round rectangular path
            val path = Path()
            path.moveTo(x + upperLeft, y)
            path.lineTo(x + width - upperRight, y)
            path.quadTo(x + width, y, x + width, y + upperRight)
            path.lineTo(x + width, y + height - lowerRight)
            path.quadTo(x + width, y + height, x + width - lowerRight, y + height)
            path.lineTo(x + lowerLeft, y + height)
            path.quadTo(x, y + height, x, y + height - lowerLeft)
            path.lineTo(x, y + upperLeft)
            path.quadTo(x, y, x + upperLeft, y)
            path.close()

            return path
        }
    }
}