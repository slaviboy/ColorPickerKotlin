package com.slaviboy.colorpicker.pickers

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.slaviboy.colorpicker.Updater
import com.slaviboy.colorpicker.window.Base

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