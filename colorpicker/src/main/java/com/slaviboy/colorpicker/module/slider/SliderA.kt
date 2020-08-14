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
import android.content.res.TypedArray
import android.graphics.*
import android.util.AttributeSet
import com.slaviboy.colorpicker.R
import com.slaviboy.colorpicker.data.Range
import com.slaviboy.colorpicker.main.ColorConverter
import com.slaviboy.colorpicker.components.Slider

/**
 * SliderA class representing A(Alpha) slider color window, with opacity value in
 * range between [0,255].
 */
class SliderA : Slider {

    constructor(context: Context) : super(context) {
        init(context, null)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(context, attrs)
    }

    internal var zebraBlockSize = 0       // zebra block size
    internal var zebraBlockColor = 0      // zebra block color
    internal var zebraBackgroundColor = 0 // zebra background color

    internal fun init(context: Context, attrs: AttributeSet?) {

        // set range
        range = Range(0f, 255f)

        // get xml attributes
        val typedArray: TypedArray = context.obtainStyledAttributes(attrs, R.styleable.SliderA)
        zebraBlockSize = typedArray.getDimensionPixelSize(R.styleable.SliderA_zebra_block_size, 14)
        zebraBlockColor = typedArray.getColor(R.styleable.SliderA_zebra_block_color, ColorConverter.parseColor("#CCCCCC"))
        zebraBackgroundColor = typedArray.getColor(R.styleable.SliderA_zebra_background_color, ColorConverter.WHITE)

        typedArray.recycle()
    }

    override fun onInit() {
        super.onInit()

        layersPaint.apply {
            alpha = 255
            shader = null
            color = zebraBackgroundColor
        }

        // set background color
        val bmpZebra = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        layersCanvas = Canvas(bmpZebra)

        // draw zebra background
        layersCanvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), layersPaint)

        // set block color
        layersPaint.color = zebraBlockColor

        // draw zebra blocks
        val rows = height / zebraBlockSize    // number of rows to fit
        val columns = width / zebraBlockSize  // number of columns to fit
        for (i in 0..rows) {
            for (j in 0..columns / 2) {
                val left = 2 * j * zebraBlockSize + (if (i % 2 == 0) 0 else zebraBlockSize).toFloat()
                val top = i * zebraBlockSize.toFloat()
                val right = left + zebraBlockSize
                val bottom = top + zebraBlockSize
                layersCanvas.drawRect(left, top, right, bottom, layersPaint)
            }
        }

        // base shape with black color
        val bmpBase = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        layersCanvas = Canvas(bmpBase)
        layersPaint.color = ColorConverter.BLACK
        layersCanvas.drawPath(clipPath, layersPaint)

        // draw zebra bitmap and cur base bitmap using SDT_IN mode
        layersCanvas = Canvas(baseLayer)
        layersCanvas.drawBitmap(bmpZebra, 0f, 0f, layersPaint)
        layersPaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.DST_IN)
        layersCanvas.drawBitmap(bmpBase, 0f, 0f, layersPaint)
        layersPaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_OVER)
    }

    override fun drawSelector(canvas: Canvas) {

        // set fill color
        val fillColor = ColorConverter.HSVAtoColor(colorConverter.hsv.h, 100, 100, colorConverter.rgba.a)
        selectorPaint.color = fillColor
        selectorPaint.style = Paint.Style.FILL
        canvas.drawCircle(selectorX, selectorY, selectorRadius, selectorPaint)

        super.drawSelector(canvas)
    }

    override fun onRedraw() {
        if (!isInit) {
            return
        }

        // draw linear gradient baseColor(Transparent)->baseColor
        val fillGradient: Shader = LinearGradient(
            gradientPoints[0].x, gradientPoints[0].y, gradientPoints[1].x, gradientPoints[1].y,
            intArrayOf(
                colorHolder.baseColorTransparent,
                colorHolder.baseColor
            ),
            floatArrayOf(0f, 1f),
            Shader.TileMode.CLAMP
        )

        layersPaint.apply {
            alpha = 255
            style = Paint.Style.FILL
            shader = fillGradient
        }

        // clear bitmap
        colorLayer.eraseColor(ColorConverter.TRANSPARENT)

        // draw rainbow shader
        layersCanvas = Canvas(colorLayer)
        layersCanvas.drawBitmap(baseLayer, 0f, 0f, layersPaint)
        layersCanvas.drawPath(clipPath, layersPaint)
        invalidate()
    }

    /**
     * Set alpha value, that will change the selector position in the slider.
     * @param a alpha value [0,255]
     */
    fun setA(a: Int) {
        if (!isInit) {
            return
        }
        range.current = a.toFloat()

        // set selector position
        val fact = range.current / 255.0f
        if (type == TYPE_VERTICAL) {
            val size = bound.height()
            selectorY = bound.top + size * fact
        } else {
            val size = bound.width()
            selectorX = bound.left + size * fact
        }
        invalidate()
    }

    override fun update() {
        setA(colorConverter.rgba.a)
    }
}