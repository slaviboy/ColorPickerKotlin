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
package com.slaviboy.colorpicker.module.slider

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import com.slaviboy.colorpicker.data.Range
import com.slaviboy.colorpicker.main.ColorConverter
import com.slaviboy.colorpicker.components.Slider

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
            fillColors[i] = ColorConverter.parseColor(fillColorsString[i])
        }

        val fillGradient: Shader = LinearGradient(
            gradientPoints[0].x, gradientPoints[0].y, gradientPoints[1].x, gradientPoints[1].y,
            fillColors, fillColorStops, Shader.TileMode.CLAMP
        )

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

        super.drawSelector(canvas)
    }

    /**
     * Set hue value, that will change the selector position in the slider.
     * @param h hue value
     */
    fun setH(h: Int) {
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
        setH(colorConverter.hsv.h)
    }
}