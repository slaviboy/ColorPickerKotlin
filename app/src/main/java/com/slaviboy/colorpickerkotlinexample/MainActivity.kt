package com.slaviboy.colorpickerkotlinexample

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.slaviboy.colorpicker.Updater
import com.slaviboy.colorpicker.Updater.OnUpdateListener
import com.slaviboy.colorpicker.converter.ColorConverter
import com.slaviboy.colorpicker.window.Base
import com.slaviboy.colorpickerkotlinexample.pickers.CircularHSV
import com.slaviboy.colorpickerkotlinexample.pickers.RectangularHSL
import com.slaviboy.colorpickerkotlinexample.pickers.RectangularHSV

class MainActivity : AppCompatActivity() {

    lateinit var textViewChangeType: EditText
    lateinit var labelViewChangeType: TextView
    lateinit var updater: Updater
    lateinit var colorConverter: ColorConverter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initColorPickers()
    }

    private fun initColorPickers() {

        // get color picker views
        val rectangularHSV: RectangularHSV = findViewById(R.id.picker1)
        val rectangularHSL: RectangularHSL = findViewById(R.id.picker2)
        val circularHSV: CircularHSV = findViewById(R.id.picker3)

        // create color convert, that will convert from one color model to another
        colorConverter = ColorConverter(160, 73, 184, 50)

        // create updater object, that will update all color window and text views
        updater = Updater(colorConverter)

        // attach updater to all color pickers
        rectangularHSV.attach(updater)
        rectangularHSL.attach(updater)
        circularHSV.attach(updater)

        textViewChangeType = circularHSV.findViewById(R.id.rgba_value)
        labelViewChangeType = circularHSV.findViewById(R.id.rgba_label)

        // attach listener to the updater
        updater.setOnUpdateListener(object : OnUpdateListener {
            override fun onTextViewUpdate(textView: TextView) {
                // triggered when user changes text view value
            }

            override fun onColorWindowUpdate(colorWindow: Base) {
                // triggered when user changes color window value
            }
        })
    }

    /**
     * Hide the system UI
     */
    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            hideSystemUI(this)
        }
    }

    /**
     * Change the text view type, by changing the tag and updating
     * the it in the updater class.
     */
    fun onUpdateModelTypeClick(view: View) {
        if (::textViewChangeType.isInitialized) {
            val tagString = (view as TextView).text.toString()
            textViewChangeType.tag = tagString
            labelViewChangeType.text = tagString
            val type = Updater.getType(textViewChangeType.tag.toString())
            updater.updateTextViewTag(textViewChangeType, type)
        }
    }

    /**
     * Generate new random color
     */
    fun onGenerateRandomColor(view: View) {
        val range = 0..255
        val r = range.random()
        val g = range.random()
        val b = range.random()
        colorConverter.setRGBA(r, g, b, (1..100).random())
        updater.updateViews()

        Log.i("color-picker", "$r, $g, $b")
    }

    /**
     * Enables regular immersive mode.
     * For "lean back" mode, remove SYSTEM_UI_FLAG_IMMERSIVE.
     * Or for "sticky immersive," replace it with SYSTEM_UI_FLAG_IMMERSIVE_STICKY
     */
    private fun hideSystemUI(activity: Activity) {

        val decorView = activity.window.decorView
        decorView.systemUiVisibility =
            (View.SYSTEM_UI_FLAG_IMMERSIVE // Set the content to appear under the system bars so that the
                    // content doesn't resize when the system bars hide and show.
                    or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN // Hide the nav bar and status bar
                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_FULLSCREEN)
    }

    /**
     * Shows the system bars by removing all the flags
     * except for the ones that make the content appear under the system bars.
     * @param activity
     */
    private fun showSystemUI(activity: Activity) {
        val decorView = activity.window.decorView
        decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
    }
}
