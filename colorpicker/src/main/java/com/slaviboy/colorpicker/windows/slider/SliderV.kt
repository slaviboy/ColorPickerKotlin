package com.slaviboy.colorpicker.windows.slider

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import com.slaviboy.colorpicker.Range
import com.slaviboy.colorpicker.converter.ColorConverter
import com.slaviboy.colorpicker.window.Slider

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
 * SliderV class representing V(Value) slider color window, with 'value' value in
 * range between [0,100].
 */
class SliderV : Slider {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    override fun onInit() {
        super.onInit()
        range = Range(100f, 0f)
    }

    override fun onRedraw() {

        if (!isInit) {
            return
        }

        // draw gradient [baseColor => BLACK]
        val fillGradient: Shader = LinearGradient(point0.x, point0.y, point1.x, point1.y,
                intArrayOf(
                        colorHolder.baseColor,
                        Color.BLACK
                ),
                floatArrayOf(0f, 1f),
                Shader.TileMode.CLAMP)

        layersPaint.apply {
            alpha = 255
            style = Paint.Style.FILL
            shader = fillGradient
        }

        // clear bitmap
        colorLayer.eraseColor(Color.TRANSPARENT)

        // draw rainbow shader
        layersCanvas = Canvas(colorLayer)
        layersCanvas.drawPath(clipPath, layersPaint)
        invalidate()
    }

    override fun drawSelector(canvas: Canvas) {

        // set fill color
        val fillColor = ColorConverter.HSVtoColor(colorConverter.h, 100, colorConverter.v)
        selectorPaint.color = fillColor
        selectorPaint.style = Paint.Style.FILL
        canvas.drawCircle(selectorX, selectorY, selectorRadius, selectorPaint)

        super.drawSelector(canvas)
    }

    /**
     * Set 'value' value, that will change the selector position in the slider.
     * @param v 'value' value
     */
    fun setV(v: Int) {

        if (!isInit) {
            return
        }
        range.current = v.toFloat()

        // set selector position
        val fact = range.current / 100.0f
        if (type == TYPE_VERTICAL) {
            val size = bound.height()
            selectorY = bound.top + (size - size * fact)
        } else {
            val size = bound.width()
            selectorX = bound.left + (size - size * fact)
        }
        invalidate()
    }

    override fun update() {
        setV(colorConverter.v)
    }
}