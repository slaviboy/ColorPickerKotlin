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
package com.slaviboy.colorpicker.main

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.slaviboy.colorpicker.components.Base

/**
 * ColorPicker class that can be extended and used to create custom color pickers.
 * By creating xml, files and setting color windows, and text views you can create
 * customizable color pickers.
 */
open class ColorPicker : ConstraintLayout {

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    private lateinit var updater: Updater                   // update object, used to update all color picker elements
    private lateinit var colorWindows: MutableList<Base>    // color windows that are attached to the layout
    private lateinit var textViews: MutableList<TextView>   // text views that are attached to the layout

    /**
     * Set up color windows and text views, by getting them from the xml layout
     * that is being inflated. And then attach them if updater is already set.
     * @param context  context object
     * @param layoutId id of the layout that will be inflated
     */
    protected fun setViews(context: Context, layoutId: Int) {

        val layoutInflater = context
            .getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val parentView = layoutInflater.inflate(layoutId, this, true) as ViewGroup

        // separate the text views and the color windows
        textViews = ArrayList()
        colorWindows = ArrayList()
        for (i in 0 until parentView.childCount) {
            val childView: View = parentView.getChildAt(i)
            if (childView is Base) {
                colorWindows.add(childView as Base)
            } else if (childView is TextView && Updater.getType(childView) != Updater.TYPE_NONE) {
                // make sure the text has tag attach to it and text type is defined before attachment
                textViews.add(childView)
            }
        }

        if (::updater.isInitialized) {
            attach(updater)
        }
    }

    /**
     * Attach color windows and text views to, given updater object, that will keep track
     * of the elements that are attached and will respond to any changes, and update the
     * other components. So if user changes the selector of a color window, the other color
     * windows and text views will change responsively.
     * @param updater updater object responsible for updating all components(elements) responsively
     */
    fun attach(updater: Updater) {
        this.updater = updater

        // attach color windows and text views to the updater
        if (::colorWindows.isInitialized && colorWindows.size > 0) {
            updater.attachColorWindows(colorWindows)
        }

        if (::textViews.isInitialized && textViews.size > 0) {
            updater.attachTextViews(textViews)
        }
    }
}