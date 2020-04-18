package com.slaviboy.colorpicker.windows.circular

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import com.slaviboy.colorpicker.Range
import com.slaviboy.colorpicker.converter.ColorConverter
import com.slaviboy.colorpicker.window.Circular

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
            color = Color.WHITE
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
                    Color.WHITE,
                    ColorConverter.HSVtoColor(i.toInt(), 100, 100)
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
        colorLayer.eraseColor(Color.TRANSPARENT)

        // draw bitmap
        layersPaint.alpha = 255
        layersCanvas = Canvas(colorLayer)
        layersCanvas.drawBitmap(baseLayer, 0f, 0f, layersPaint)

        // draw dimming circle
        val opacity = 255 - (255 * (colorConverter.hsv.v / 100.0)).toInt()

        layersPaint.apply {
            shader = null
            style = Paint.Style.FILL
            color = Color.argb(opacity, 0, 0, 0)
        }
        layersCanvas.drawCircle(halfWidth, halfHeight, radius + 1, layersPaint);
        invalidate()
    }

    /**
     * Set H(hue) in range between [0,360]
     * @param h hue value
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
     * @param s saturation value
     */
    fun setS(s: Int) {
        if (!isInit) {
            return
        }

        // set distance and current distance range
        distanceRange.current = s.toFloat()
        distance = radius * (s / 100.0f)
        setSelectorPosition()
    }

    /**
     * Set H(hue) in range [0,360], and S(saturation) in range [0,100]
     * @param h hue
     * @param s saturation
     */
    fun setHS(h: Int, s: Int) {
        if (!isInit) {
            return
        }
        angleRange.current = h.toFloat()
        distanceRange.current = s.toFloat()
        angle = h.toFloat()
        distance = radius * (s / 100.0f)
        setSelectorPosition()
    }

    /**
     * Set selector position using current distance and degree.
     */
    fun setSelectorPosition() {

        val dPoint = distantPoint(halfWidth, halfHeight, halfWidth + radius, halfHeight, distance)
        val rPoint = rotatePoint(halfWidth, halfHeight, dPoint.x, dPoint.y, 360.0 - angle)

        // set selector position
        selectorX = rPoint.x
        selectorY = rPoint.y
        invalidate()
    }

    override fun update() {
        setHS(colorConverter.hsv.h, colorConverter.hsv.s)
    }
}