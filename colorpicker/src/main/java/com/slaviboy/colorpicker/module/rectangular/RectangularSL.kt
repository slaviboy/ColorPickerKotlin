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
 * RectangularSL is class representing rectangular S(Saturation) and L(Lightness) color window.
 * The SATURATION is represented by the selector position on the X-axis(horizontally), and
 * the LIGHTNESS is set using the Y-axis(vertically).
 * (this color window is usually used in HSL color picker)
 */
class RectangularSL : Rectangular {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    override fun onInit() {
        super.onInit()

        // starting direction is X = 0, Y = 0 and it corresponds to lower range values
        horizontalRange = Range(100f, 0f)
        verticalRange = Range(100f, 0f)
    }

    override fun onRedraw() {
        if (!isInit) {
            return
        }

        // clear bitmaps
        baseLayer.eraseColor(ColorConverter.TRANSPARENT)
        colorLayer.eraseColor(ColorConverter.TRANSPARENT)

        // create linear gradient [White => baseColor => Black](vertically)
        val fillGradient: Shader = LinearGradient(
            0f, bound.top, 0f, bound.bottom,
            intArrayOf(
                ColorConverter.WHITE,
                colorHolder.baseColor,
                ColorConverter.BLACK
            ),
            floatArrayOf(0f, 0.5f, 1f),
            Shader.TileMode.CLAMP
        )

        layersPaint.apply {
            alpha = 255
            style = Paint.Style.FILL
            shader = fillGradient
            xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_OVER)
        }

        // draw first gradient
        layersCanvas = Canvas(baseLayer)
        layersCanvas.drawPath(clipPath, layersPaint)

        // create linear gradient  [baseColor => baseColor(transparent)] (horizontally)
        val fillGradient2: Shader = LinearGradient(
            bound.left, 0f, bound.right, 0f,
            intArrayOf(
                colorHolder.baseColor,
                colorHolder.baseColorTransparent
            ),
            floatArrayOf(0f, 1f),
            Shader.TileMode.CLAMP
        )

        layersPaint.apply {
            shader = fillGradient2
            xfermode = PorterDuffXfermode(PorterDuff.Mode.DST_IN)
        }

        // draw second gradient
        layersCanvas.drawPath(clipPath, layersPaint)

        // create linear gradient [White => Black](vertically)
        val fillGradient3: Shader = LinearGradient(
            0f, bound.top, 0f, bound.bottom,
            intArrayOf(
                ColorConverter.WHITE,
                ColorConverter.BLACK
            ),
            floatArrayOf(0f, 1f),
            Shader.TileMode.CLAMP
        )

        layersPaint.apply {
            shader = fillGradient3
            xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_OVER)
        }

        // draw third gradient
        layersCanvas = Canvas(colorLayer)
        layersCanvas.drawPath(clipPath, layersPaint)
        layersCanvas.drawBitmap(baseLayer, 0f, 0f, layersPaint)
        invalidate()
    }

    /**
     * Set saturation in range between [0,100]
     * @param s saturation value [0,100]
     */
    fun setS(s: Int) {
        if (!isInit) {
            return
        }

        horizontalRange.current = s.toFloat()
        selectorX = bound.left + bound.width() - bound.width() * (s / 100.0f)
        invalidate()
    }

    /**
     * Set lightness in range between [0,100]
     * @param l lightness value [0,100]
     */
    fun setL(l: Int) {
        if (!isInit) {
            return
        }

        verticalRange.current = l.toFloat()
        selectorY = bound.top + (bound.height() - bound.height() * (l / 100.0f))
        invalidate()
    }

    /**
     * Set saturation in range [0,100] and lightness in range [0,100]
     * @param s saturation value [0,100]
     * @param l lightness value [0,100]
     */
    fun setSL(s: Int, l: Int) {
        if (!isInit) {
            return
        }

        horizontalRange.current = s.toFloat()
        selectorX = bound.left + bound.width() - bound.width() * (s / 100.0f)
        verticalRange.current = l.toFloat()
        selectorY = bound.top + (bound.height() - bound.height() * (l / 100.0f))
        invalidate()
    }

    override fun update() {
        setSL(colorConverter.hsl.s, colorConverter.hsl.l)
    }
}