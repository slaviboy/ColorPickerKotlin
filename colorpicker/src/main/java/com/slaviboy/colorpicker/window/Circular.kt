package com.slaviboy.colorpicker.window

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PointF
import android.util.AttributeSet
import com.slaviboy.colorpicker.R
import com.slaviboy.colorpicker.Range
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

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
 * Circular class represents a circular color window model, the selector follows a circular path by changing the
 * view containing this model set the initial circle radius. The radius matches half of the minimum side, so if
 * width is bigger than height that means the radius is half of the height.
 */
open class Circular : Base {

    constructor(context: Context) : super(context) {
        init(context, null)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context, null)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(context, null)
    }

    protected var angle: Float           // degree between a line from center to current selector position and the horizontal line passing from the center
    protected var distance: Float        // distance between the circle center and current selector position
    protected var radius: Float          // show current circle radius it matches half the minimum from the two sides: Min(width/2, height/2) minus the stroke width
    lateinit var distanceRange: Range    // range corresponding for the distance
    lateinit var angleRange: Range       // range corresponding for the angle

    init {
        angle = 0f
        distance = 0f
        radius = 0f
    }

    /**
     * Method called to get the xml attribute values.
     * @param context context
     * @param attrs attribute set
     */
    private fun init(context: Context, attrs: AttributeSet?) {

        val typedArray: TypedArray = context.obtainStyledAttributes(attrs, R.styleable.Circular)

        // xml ranges
        val distanceRangeLower = typedArray.getFloat(R.styleable.Circular_distance_range_lower, 0f)
        val distanceRangeUpper = typedArray.getFloat(R.styleable.Circular_distance_range_upper, 100f)
        val angleRangeLower = typedArray.getFloat(R.styleable.Circular_angle_range_lower, 0f)
        val angleRangeUpper = typedArray.getFloat(R.styleable.Circular_angle_range_upper, 100f)

        distanceRange = Range(distanceRangeLower, distanceRangeUpper)
        angleRange = Range(angleRangeLower, angleRangeUpper)

        typedArray.recycle()
    }

    override fun onInit() {}
    override fun onRedraw() {
        invalidate()
    }


    fun distanceBetweenTwoPoints(x0: Float, y0: Float, x1: Float, y1: Float): Float {
        val dX = (x0 - x1)
        val dY = (y0 - y1)
        return sqrt(dX * dX + dY * dY.toDouble()).toFloat()
    }

    override fun onMove(x: Float, y: Float) {

        val centerX = halfWidth
        val centerY = halfHeight

        // get degree [0, 360]
        val degree = angle(centerX, centerY, x, y).toFloat()

        // get distance from the center to the selector
        var dist = distanceBetweenTwoPoints(x, y, centerX, centerY)

        // limit selector position to circle bound
        if (dist >= radius) {
            val ratio = radius / dist
            selectorX = ((1f - ratio) * centerX + ratio * x)
            selectorY = ((1f - ratio) * centerY + ratio * y)
            dist = radius
        } else {
            selectorX = x
            selectorY = y
        }

        angle = degree
        distance = dist
        distanceRange.setCurrent(radius, dist)
        angleRange.setCurrent(360f, angle)

        // call update if listener exist
        if (isOnUpdateListenerInitialised()) {
            onUpdateListener.onUpdate(this)
        }
        invalidate()
    }

    /**
     * Static method that return new coordinates of a point rotated around center with given degree.
     * @param cx center pivot point x coordinate
     * @param cy center pivot point y coordinate
     * @param x rotary point x coordinate
     * @param y rotary point y coordinate
     * @param angle rotational angle
     * @return new point position for the rotated points
     */
    protected fun rotatePoint(cx: Float, cy: Float, x: Float, y: Float, angle: Double): PointF {
        val radians = Math.PI / 180.0 * angle
        val cos = cos(radians).toFloat()
        val sin = sin(radians).toFloat()
        return PointF(
            (cos * (x - cx) + sin * (y - cy) + cx),
            (cos * (y - cy) - sin * (x - cx) + cy)
        )
    }

    /**
     * Static method that returns the coordinates of a point that is distant from the start point,
     * and lies on a line between the start and end points.
     * @param startX start point x coordinate
     * @param startY start point y coordinate
     * @param endX end point x coordinate
     * @param endY end point y coordinate
     * @param distance distance between the start point and the returned point
     * @return coordinate of a point distant from the start point to a given distance
     */
    protected fun distantPoint(startX: Float, startY: Float, endX: Float, endY: Float, distance: Float): PointF {
        val xDist = endX - startX
        val yDist = endY - startY
        val dist = sqrt(xDist * xDist + yDist * yDist.toDouble())
        val fractionOfTotal = distance / dist.toFloat()
        return PointF(
            startX + xDist * fractionOfTotal,
            startY + yDist * fractionOfTotal
        )
    }

    override fun drawSelector(canvas: Canvas) {

        // set fill color
        val fillColor = colorHolder.selectedColor
        selectorPaint.color = fillColor
        selectorPaint.style = Paint.Style.FILL
        canvas.drawCircle(selectorX, selectorY, selectorRadius, selectorPaint)

        // restore stroke color before calling super
        selectorPaint.color = selectorColor
        selectorPaint.style = Paint.Style.STROKE
        super.drawSelector(canvas)
    }

    override fun drawBorder(canvas: Canvas) {

        // draw border stroke
        layersPaint.apply {
            shader = null
            color = borderColor
            strokeWidth = borderStrokeWidth
            style = Paint.Style.STROKE
        }

        canvas.drawCircle(halfWidth, halfHeight, radius + 1, layersPaint)
    }

    override fun update() {}
    override fun redraw() {
        onRedraw()
    }

    companion object {

        /**
         * Static method that return the angle between a center point and a rotary point.
         * @param cx center point x coordinate
         * @param cy center point y coordinate
         * @param x rotary point x coordinate
         * @param y rotary point y coordinate
         * @return angle between the line from the center point to the rotary point and the horizontal line passing through the center
         */
        protected fun angle(cx: Float, cy: Float, x: Float, y: Float): Double {
            val dy = y - cy
            val dx = x - cx
            var theta = atan2(dy.toDouble(), dx.toDouble())     // range (-PI, PI]
            theta *= 180.0 / Math.PI                            // radians to degrees, range (-180, 180]
            return if (theta < 0.0) 360.0 + theta else theta    // return in range [0, 360)
        }
    }
}