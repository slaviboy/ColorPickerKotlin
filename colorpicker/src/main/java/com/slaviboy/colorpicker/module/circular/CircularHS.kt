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
package com.slaviboy.colorpicker.module.circular

import android.content.ContentValues
import android.content.Context
import android.graphics.*
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.AttributeSet
import com.slaviboy.colorpicker.data.Range
import com.slaviboy.colorpicker.main.ColorConverter
import com.slaviboy.colorpicker.components.Circular
import com.slaviboy.colorpicker.models.HSL
import com.slaviboy.colorpicker.models.HSV
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

/**
 * CircularHS is class representing circular H(hue) and S(Saturation) color window.
 * The HUE is shown by current degree between the selector and the horizontal
 * line through the center. And the SATURATION is represented by the distance
 * between the selector and the circle center.
 * (this color window is usually used in HSV or HSL color pickers)
 */
class CircularHS : Circular {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    override fun onInit() {

        // set ranges fot the hue and saturation
        distanceRange = Range(0f, 100f)
        angleRange = Range(0f, 360f)

        // the radius is the minimum half side from width and height, that way the circle can fit in the view
        radius = Math.min(halfWidth, halfHeight) - (selectorRadius + selectorStrokeWidth / 2f + selectorExtraStrokeWidth / 2f)

        val centerX = halfWidth
        val centerY = halfHeight
        val sX = radius
        val sY = radius

        // init paint
        layersPaint.apply {
            alpha = 255
            shader = null
            color = ColorConverter.WHITE
            style = Paint.Style.STROKE
        }

        // fill the circle with rainbow using lines with gradient color
        var i = 0.0
        while (i < 360.0) {

            val rad = (i * (2.0 * Math.PI) / 360.0)

            val x1 = centerX
            val y1 = centerY
            val x2 = centerX + sX * Math.cos(rad).toFloat()
            val y2 = centerY + sY * Math.sin(rad).toFloat()

            // linear gradient [White => Hue], from the center to the edge
            val fillGradient: Shader = LinearGradient(
                x1, y1,
                x2, y2,
                intArrayOf(
                    ColorConverter.WHITE,
                    ColorConverter.HSVAtoColor(i.toInt(), 100, 100)
                ),
                floatArrayOf(0f, 1f),
                Shader.TileMode.CLAMP
            )

            layersPaint.shader = fillGradient
            layersCanvas.drawLine(x1, y1, x2, y2, layersPaint)

            i += 0.1
        }

        // reset paint
        layersPaint.apply {
            shader = null
            style = Paint.Style.FILL
            strokeWidth = 0f
        }
    }

    override fun onRedraw() {

        if (!isInit) {
            return
        }

        // clear bitmap
        colorLayer.eraseColor(ColorConverter.TRANSPARENT)

        // draw bitmap
        layersPaint.alpha = 255
        layersCanvas = Canvas(colorLayer)
        layersCanvas.drawBitmap(baseLayer, 0f, 0f, layersPaint)

        // draw dimming circle
        val opacity = 255 - (255 * (colorConverter.hsv.v / HSV.V_MAX.toFloat())).toInt()

        layersPaint.apply {
            shader = null
            style = Paint.Style.FILL
            color = ColorConverter.RGBAtoColor(0, 0, 0, opacity)
        }
        layersCanvas.drawCircle(halfWidth, halfHeight, radius + 1, layersPaint);
        invalidate()
    }

    /**
     * Set H(hue) in range between [0,360]
     * @param h hue value [0,360]
     */
    fun setH(h: Int) {
        if (!isInit) {
            return
        }

        // set current angle
        angleRange.current = h.toFloat()
        angle = h.toFloat()
        setSelectorPosition()
    }

    /**
     * Set S(saturation) in range between [0,100]
     * @param s saturation value [0,100]
     */
    fun setS(s: Int) {
        if (!isInit) {
            return
        }

        // set distance and current distance range
        distanceRange.current = s.toFloat()
        distance = radius * (s / HSL.S_MAX.toFloat())
        setSelectorPosition()
    }

    /**
     * Set H(hue) in range [0,360], and S(saturation) in range [0,100]
     * @param h hue [0,360]
     * @param s saturation [0,100]
     */
    fun setHS(h: Int, s: Int) {
        if (!isInit) {
            return
        }
        angleRange.current = h.toFloat()
        distanceRange.current = s.toFloat()
        angle = h.toFloat()
        distance = radius * (s / HSL.S_MAX.toFloat())
        setSelectorPosition()
    }

    /**
     * Set selector position using current distance and degree.
     */
    fun setSelectorPosition() {

        val dPoint = distantPoint(halfWidth, halfHeight, halfWidth + radius, halfHeight, distance)
        val rPoint = rotatePoint(halfWidth, halfHeight, dPoint.x, dPoint.y, HSV.H_MAX - angle)

        // set selector position
        selectorX = rPoint.x
        selectorY = rPoint.y

        invalidate()
    }

    override fun update() {
        setHS(colorConverter.hsv.h, colorConverter.hsv.s)
    }
}