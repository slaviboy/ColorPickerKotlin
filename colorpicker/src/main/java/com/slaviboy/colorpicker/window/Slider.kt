package com.slaviboy.colorpicker.window

import android.content.Context
import android.content.res.TypedArray
import android.graphics.PointF
import android.graphics.RectF
import android.util.AttributeSet
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
 * Slider class represent a slider color window model, as the selector follows only one direction.
 * The model is based on 1D(One Dimensional) color window, that means it contains one range but can specify
 * by the slider type property as - vertical or horizontal, that will keep the selector static for the other
 * direction and mimicking slider behavior.
 */
open class Slider : Base {

    constructor(context: Context) : super(context) {
        init(context, null)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(context, attrs)
    }

    lateinit var range: Range               // slider allowed values range
    protected lateinit var point0: PointF   // start gradient point
    protected lateinit var point1: PointF   // end gradient point
    var type: Int                           // slider type - vertical or horizontal

    init {
        type = TYPE_VERTICAL
    }

    private fun init(context: Context, attrs: AttributeSet?) {

        // get custom xml properties
        val typedArray: TypedArray = context.obtainStyledAttributes(attrs, R.styleable.Slider)

        // get slider type
        type = typedArray.getInteger(R.styleable.Slider_type, TYPE_HORIZONTAL)

        // get ranges
        val rangeLower = typedArray.getFloat(R.styleable.Slider_range_lower, 0f)
        val rangeUpper = typedArray.getFloat(R.styleable.Slider_range_upper, 100f)
        range = Range(rangeLower, rangeUpper)

        typedArray.recycle()
    }

    override fun onInit() {

        val paddingVertical: Float
        val paddingHorizontal: Float

        // set points showing the directions depending if selector is centered vertically or horizontally
        val x0: Float
        val y0: Float
        val x1: Float
        val y1: Float
        if (type == TYPE_VERTICAL) {

            // for vertical slider
            paddingHorizontal = borderStrokeWidth
            paddingVertical = padding.top
            x0 = 0f
            y0 = paddingVertical
            x1 = 0f
            y1 = height - paddingVertical

        } else {

            // for horizontal slider
            paddingHorizontal = padding.left
            paddingVertical = borderStrokeWidth
            x0 = paddingHorizontal
            y0 = 0f
            x1 = width - paddingHorizontal
            y1 = 0f
        }

        // set points for setting gradient
        point0 = PointF(x0, y0)
        point1 = PointF(x1, y1)

        // set bound and clip path
        bound = RectF(paddingHorizontal, paddingVertical,
                width - paddingHorizontal, height - paddingVertical)
        clipPath = Rectangular.roundRect(bound.left, bound.top, bound.width(), bound.height(),
                cornerRadius)

        // center selector
        selectorX = halfWidth
        selectorY = halfHeight
    }

    override fun onRedraw() {
        invalidate()
    }

    override fun onMove(x: Float, y: Float) {

        var x = x
        var y = y

        // make sure selector does not pass the canvas boundary
        if (x < padding.left) x = padding.left else if (x >= width - padding.right) x = width - 1f - padding.right
        if (y < padding.top) y = padding.top else if (y >= height - padding.bottom) y = height - 1f - padding.bottom

        // center selector depending on type
        if (type == TYPE_HORIZONTAL) {
            y = halfHeight
        }
        if (type == TYPE_VERTICAL) {
            x = halfWidth
        }

        // set new selector position
        selectorX = x
        selectorY = y

        // set current horizontal and vertical range values
        if (type == TYPE_VERTICAL) {
            range.setCurrent(
                    height - 1f - padding.bottom - padding.top,
                    y - padding.top)
        }
        if (type == TYPE_HORIZONTAL) {
            range.setCurrent(
                    width - 1f - padding.left - padding.right,
                    x - padding.left)
        }

        // call update if listener exist
        if (isOnUpdateListenerInitialised()) {
            onUpdateListener.onUpdate(this)
        }
        invalidate()
    }

    override fun update() {}
    override fun redraw() {
        onRedraw()
    }

    companion object {

        // slider types
        const val TYPE_VERTICAL = 0
        const val TYPE_HORIZONTAL = 1
    }
}