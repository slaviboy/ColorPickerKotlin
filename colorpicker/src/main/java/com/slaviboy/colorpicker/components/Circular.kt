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
package com.slaviboy.colorpicker.components

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PointF
import android.util.AttributeSet
import com.slaviboy.colorpicker.R
import com.slaviboy.colorpicker.data.Range
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * Circular class represents a circular color window model, the selector follows a circular path by changing the
 * view containing this model set the initial circle radius. The radius matches half of the minimum side, so if
 * width is bigger than height that means the radius is half of the height.
 */
open class Circular : Base {

    constructor(context: Context) : super(context) {
        setCircularAttributeSet(context, null)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        setCircularAttributeSet(context, null)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        setCircularAttributeSet(context, null)
    }

    internal var angle: Float            // degree between a line from center to current selector position and the horizontal line passing from the center, measured in degrees [0,360]
    internal var distance: Float         // distance between the circle center and current selector position, measured in pixels [0, min(width/2, height/2)]
    internal var radius: Float           // show current circle radius it matches half the minimum from the two sides: Min(width/2, height/2) minus the stroke width
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
    internal fun setCircularAttributeSet(context: Context, attrs: AttributeSet?) {

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

    /**
     * Method that changes the position of the selector and makes sure its is bound
     * to a circle shape, that way if the new position is outside the circle the method
     * corrects it before being applied.
     * @param x new x coordinate of the selector
     * @param x new y coordinate of the selector
     */
    override fun moveSelector(x: Float, y: Float) {

        val centerX = halfWidth
        val centerY = halfHeight

        // get degree [0, 360]
        val degree = angleBetweenTwoPoint(centerX, centerY, x, y)

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

    override fun drawSelector(canvas: Canvas) {

        // set fill color
        val fillColor = colorHolder.selectedColor
        selectorPaint.color = fillColor
        selectorPaint.style = Paint.Style.FILL
        canvas.drawCircle(selectorX, selectorY, selectorRadius, selectorPaint)

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
         * Static method that return new coordinates of a point rotated around center with given degree.
         * @param cx center pivot point x coordinate
         * @param cy center pivot point y coordinate
         * @param x rotary point x coordinate
         * @param y rotary point y coordinate
         * @param angle rotational angle in degree
         * @return new point position for the rotated points
         */
        fun rotatePoint(cx: Float, cy: Float, x: Float, y: Float, angle: Float): PointF {
            val radians = Math.PI / 180.0 * angle
            val cos = cos(radians).toFloat()
            val sin = sin(radians).toFloat()
            return PointF(
                (cos * (x - cx) + sin * (y - cy) + cx),
                (cos * (y - cy) - sin * (x - cx) + cy)
            )
        }

        /**
         * Calculate the distance between two points with coordinates (x0,y0) and (x1,y1)
         * @param x0 x coordinate of first point
         * @param y0 y coordinate of first point
         * @param x1 x coordinate of second point
         * @param y1 y coordinate of second point
         */
        fun distanceBetweenTwoPoints(x0: Float, y0: Float, x1: Float, y1: Float): Float {
            val dX = (x0 - x1)
            val dY = (y0 - y1)
            return sqrt(dX * dX + dY * dY.toDouble()).toFloat()
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
        fun distantPoint(startX: Float, startY: Float, endX: Float, endY: Float, distance: Float): PointF {
            val xDist = endX - startX
            val yDist = endY - startY
            val dist = sqrt(xDist * xDist + yDist * yDist.toDouble())
            val fractionOfTotal = distance / dist.toFloat()
            return PointF(
                startX + xDist * fractionOfTotal,
                startY + yDist * fractionOfTotal
            )
        }

        /**
         * Static method that return the angle between the horizontal line that passes through the center points
         * and the line that passes through the center point and a rotary point.
         * @param cx center point x coordinate
         * @param cy center point y coordinate
         * @param x rotary point x coordinate
         * @param y rotary point y coordinate
         * @return angle between the line from the center point to the rotary point and the horizontal line passing through the center
         */
        fun angleBetweenTwoPoint(cx: Float, cy: Float, x: Float, y: Float): Float {
            val dy = y - cy
            val dx = x - cx
            var theta = atan2(dy.toDouble(), dx.toDouble())               // range (-PI, PI]
            theta *= 180.0 / Math.PI                                      // radians to degrees, range (-180, 180]
            val finalAngle = if (theta < 0.0) 360.0 + theta else theta    // return in range [0, 360)
            return finalAngle.toFloat()
        }
    }
}