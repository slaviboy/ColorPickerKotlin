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
        val fillGradient: Shader = LinearGradient(gradientPoints[0].x, gradientPoints[0].y, gradientPoints[1].x, gradientPoints[1].y,
                intArrayOf(
                        colorHolder.baseColor,
                    ColorConverter.BLACK
                ),
                floatArrayOf(0f, 1f),
                Shader.TileMode.CLAMP)

        layersPaint.apply {
            alpha = 255
            style = Paint.Style.FILL
            shader = fillGradient
        }

        // clear bitmap
        colorLayer.eraseColor(ColorConverter.TRANSPARENT)

        // draw rainbow shader
        layersCanvas = Canvas(colorLayer)
        layersCanvas.drawPath(clipPath, layersPaint)
        invalidate()
    }

    override fun drawSelector(canvas: Canvas) {

        // set fill color
        val fillColor = ColorConverter.HSVAtoColor(colorConverter.hsv.h, 100, colorConverter.hsv.v)
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
        setV(colorConverter.hsv.v)
    }
}