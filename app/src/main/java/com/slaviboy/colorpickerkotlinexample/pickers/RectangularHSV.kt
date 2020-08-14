package com.slaviboy.colorpickerkotlinexample.pickers

import android.content.Context
import android.util.AttributeSet
import com.slaviboy.colorpicker.main.ColorPicker
import com.slaviboy.colorpickerkotlinexample.R

class RectangularHSV : ColorPicker {

    constructor(context: Context) : super(context) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(context)
    }

    private fun init(context: Context) {
        setViews(context, R.layout.color_picker_hsv_rectangular)
    }
}
