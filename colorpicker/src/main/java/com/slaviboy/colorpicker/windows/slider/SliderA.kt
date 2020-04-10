package com.slaviboy.colorpicker.windows.slider

import android.content.Context
import android.content.res.TypedArray
import android.graphics.*
import android.util.AttributeSet
import com.slaviboy.colorpicker.R
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
 * SliderA class representing A(Alpha) slider color window, with opacity value in
 * range between [0,100].
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

    private var zebraBlockSize = 0       // zebra block size
    private var zebraBlockColor = 0      // zebra block color
    private var zebraBackgroundColor = 0 // zebra background color

    private fun init(context: Context, attrs: AttributeSet?) {

        // set range
        range = Range(0f, 100f)

        // get xml attributes
        val typedArray: TypedArray = context.obtainStyledAttributes(attrs, R.styleable.SliderA)
        zebraBlockSize = typedArray.getDimensionPixelSize(R.styleable.SliderA_zebra_block_size, 14)
        zebraBlockColor = typedArray.getColor(R.styleable.SliderA_zebra_block_color, Color.parseColor("#CCCCCC"))
        zebraBackgroundColor = typedArray.getColor(R.styleable.SliderA_zebra_background_color, Color.WHITE)

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
        layersPaint.color = Color.BLACK
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
        val fillColor = ColorConverter.HSVAtoColor(colorConverter.h, 100, 100, colorConverter.a)
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
            point0.x, point0.y, point1.x, point1.y,
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
        colorLayer.eraseColor(Color.TRANSPARENT)

        // draw rainbow shader
        layersCanvas = Canvas(colorLayer)
        layersCanvas.drawBitmap(baseLayer, 0f, 0f, layersPaint)
        layersCanvas.drawPath(clipPath, layersPaint)
        invalidate()
    }

    /**
     * Set alpha value, that will change the selector position in the slider.
     * @param a alpha value
     */
    fun setA(a: Int) {
        if (!isInit) {
            return
        }
        range.current = a.toFloat()

        // set selector position
        val fact = range.current / 100.0f
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
        setA(colorConverter.a)
    }
}