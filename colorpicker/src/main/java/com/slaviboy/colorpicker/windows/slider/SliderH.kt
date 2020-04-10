package com.slaviboy.colorpicker.windows.slider

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import com.slaviboy.colorpicker.Range
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
 * SliderH class representing H(Hue) slider color window, with hue value in
 * range between [0,360].
 */
class SliderH : Slider {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    override fun onInit() {
        super.onInit()

        // set ranges
        range = Range(360f, 0f)

        // set fill rainbow gradient color
        val fillColorsString = arrayOf("#ff0000", "#ff00ff", "#0000ff", "#00ffff", "#00ff00", "#ffff00", "#ff0000")
        val fillColorStops = floatArrayOf(0.00f, 0.166f, 0.33f, 0.5f, 0.66f, 0.83f, 1f)
        val fillColors = IntArray(fillColorsString.size)

        for (i in fillColors.indices) {
            fillColors[i] = Color.parseColor(fillColorsString[i])
        }

        val fillGradient: Shader = LinearGradient(point0.x, point0.y, point1.x, point1.y,
                fillColors, fillColorStops, Shader.TileMode.CLAMP)

        // init temp layerPaint
        layersPaint.shader = fillGradient

        // draw rainbow fill shader
        layersCanvas.drawPath(clipPath, layersPaint)
        colorLayer = baseLayer
    }

    override fun drawSelector(canvas: Canvas) {

        // set fill color
        val fillColor = colorHolder.baseColor
        selectorPaint.color = fillColor
        selectorPaint.style = Paint.Style.FILL
        canvas.drawCircle(selectorX, selectorY, selectorRadius, selectorPaint)

        // restore stroke color before calling super
        selectorPaint.color = selectorColor
        selectorPaint.style = Paint.Style.STROKE
        super.drawSelector(canvas)
    }

    /**
     * Set hue value, that will change the selector position in the slider.
     * @param h hue value
     */
    private fun setH(h: Int) {
        if (!isInit) {
            return
        }
        range.current = h.toFloat()

        // set selector position
        val fact = range.current / 360.0f
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
        setH(colorConverter.h)
    }
}