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
package com.slaviboy.colorpicker.module.rectangular

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import com.slaviboy.colorpicker.data.Range
import com.slaviboy.colorpicker.main.ColorConverter
import com.slaviboy.colorpicker.components.Rectangular

/**
 * RectangularSV is class representing rectangular S(Saturation) and V(Value) color window.
 * The SATURATION is represented by the selector position on the X-axis(horizontally), and
 * the VALUE is set using the Y-axis(vertically).
 * (this color window is usually used in HSV color picker)
 */
class RectangularSV : Rectangular {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    override fun onInit() {
        super.onInit()

        //starting direction is X = 0, Y = 0 and it corresponds to lower range value
        horizontalRange = Range(0f, 100f)
        verticalRange = Range(100f, 0f)
        initBaseLayer()
    }

    public override fun onRedraw() {
        if (!isInit) {
            return
        }

        // create the second gradient layer [baseColor => Black](vertically)
        val fillGradient: Shader = LinearGradient(
            0f, bound.top, 0f, bound.bottom, intArrayOf(
                colorHolder.baseColor,
                ColorConverter.BLACK
            ), floatArrayOf(0f, 1f), Shader.TileMode.CLAMP
        )

        layersPaint.apply {
            alpha = 255
            style = Paint.Style.FILL
            shader = fillGradient
        }

        // clear bitmap
        colorLayer.eraseColor(ColorConverter.TRANSPARENT)
        layersCanvas = Canvas(colorLayer)
        layersCanvas.drawPath(clipPath, layersPaint)
        layersCanvas.drawBitmap(baseLayer, 0f, 0f, layersPaint)
        invalidate()
    }

    private fun initBaseLayer() {

        // create the first gradient layer [White => Black](vertically)
        val fillGradient: Shader = LinearGradient(
            0f, bound.top, 0f, bound.bottom,
            intArrayOf(
                ColorConverter.WHITE,
                ColorConverter.BLACK
            ),
            floatArrayOf(0f, 1f),
            Shader.TileMode.CLAMP
        )

        layersPaint = Paint().apply {
            isAntiAlias = true
            shader = fillGradient
        }

        // draw another gradient using -xor [Transparent => Black](horizontally)
        val fillGradient2: Shader = LinearGradient(
            bound.left, 0f, bound.right, 0f,
            intArrayOf(
                ColorConverter.TRANSPARENT,
                ColorConverter.BLACK
            ),
            floatArrayOf(0f, 1f),
            Shader.TileMode.CLAMP
        )

        // draw rainbow shader
        layersCanvas = Canvas(baseLayer)
        layersCanvas.drawPath(clipPath, layersPaint)
        layersPaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.XOR)
        layersPaint.shader = fillGradient2
        layersCanvas.drawPath(clipPath, layersPaint)
        layersPaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_OVER)
    }

    /**
     * Set saturation in range between [0,100]
     * @param s saturation value
     */
    fun setS(s: Int) {
        if (!isInit) {
            return
        }
        horizontalRange.current = s.toFloat()
        selectorX = bound.left + bound.width() * (s / 100.0f)
        invalidate()
    }

    /**
     * Set value in range between [0,100]
     * @param v 'value' value
     */
    fun setV(v: Int) {
        if (!isInit) {
            return
        }
        verticalRange.current = v.toFloat()
        selectorY = bound.top + (bound.height() - bound.height() * (v / 100.0f))
        invalidate()
    }

    /**
     * Set saturation in range [0,100] and value in range [0,100]
     * @param s saturation value
     * @param v 'value' value
     */
    fun setSV(s: Int, v: Int) {
        if (!isInit) {
            return
        }
        horizontalRange.current = s.toFloat()
        selectorX = bound.left + bound.width() * (s / 100.0f)
        verticalRange.current = v.toFloat()
        selectorY = bound.top + (bound.height() - bound.height() * (v / 100.0f))
        invalidate()
    }

    override fun update() {
        setSV(colorConverter.hsv.s, colorConverter.hsv.v)
    }
}