package com.slaviboy.colorpickerkotlinexample

import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.app.Activity
import android.content.ContentValues
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Bitmap.CompressFormat
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.slaviboy.colorpicker.components.Base
import com.slaviboy.colorpicker.main.ColorConverter
import com.slaviboy.colorpicker.main.Updater
import com.slaviboy.colorpicker.main.Updater.OnUpdateListener
import com.slaviboy.colorpicker.module.circular.CircularHS
import com.slaviboy.colorpickerkotlinexample.pickers.CircularHSV
import com.slaviboy.colorpickerkotlinexample.pickers.RectangularHSL
import com.slaviboy.colorpickerkotlinexample.pickers.RectangularHSV
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.sql.Timestamp
import java.util.*


class MainActivity : AppCompatActivity() {

    // get color picker views
    lateinit var rectangularHSV: RectangularHSV
    lateinit var rectangularHSL: RectangularHSL
    lateinit var circularHSV: CircularHSV

    lateinit var textViewChangeType: EditText
    lateinit var labelViewChangeType: TextView
    lateinit var updater: Updater
    lateinit var colorConverter: ColorConverter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initColorPickers()
    }

    /**
     * Initialize the color picker by getting reference to them and connecting(attaching)
     * them to a updater that controls the update from onw color window or text view to
     * all the other view connected to the same updater.
     */
    private fun initColorPickers() {

        // get color picker views
        rectangularHSV = findViewById(R.id.rectangular_hsv)
        rectangularHSL = findViewById(R.id.rectangular_hsl)
        circularHSV = findViewById(R.id.circular_hsv)

        // create color convert, that will convert colors from one color model to another
        colorConverter = ColorConverter("#1f538cb5")

        // create updater object, that will update all color window and text views
        updater = Updater(colorConverter)

        // attach updater to all color pickers
        rectangularHSV.attach(updater)
        rectangularHSL.attach(updater)
        circularHSV.attach(updater)

        textViewChangeType = circularHSV.findViewById(R.id.multiple_value)
        labelViewChangeType = circularHSV.findViewById(R.id.multiple_label)

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
        colorConverter.rgba.setRGBA(r, g, b, (1..100).random())
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
            (View.SYSTEM_UI_FLAG_IMMERSIVE
                    or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
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

    /**
     * Request the permission from the user to allow witting in the external storage
     * of the device.
     */
    fun requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                val permissions = arrayOf(WRITE_EXTERNAL_STORAGE)
                val permissionRequestCode = 1
                requestPermissions(permissions, permissionRequestCode)
            }
        }
    }

    /**
     * Save the bitmap to local storage on the phone with given folder location and file name
     * @param bitmap bitmap that will be save to the local storage of the phone
     * @param folderName name of the folder where the file will be located
     * @param fileName name of the file that will be in PNG file format
     * @param fileFormat file format of the image 0=png, 1=jpeg
     * @param imageQuality quality of the image only for Jpeg
     */
    fun saveBitmapToLocalStorage(bitmap: Bitmap, folderName: String = "Screenshots", fileName: String = "test", fileFormat: Int = 0, imageQuality: Int = 100) {

        val fileFormatString = arrayListOf("png", "jpg")

        try {
            val imageFile: File
            val dir = File(Environment.getExternalStorageDirectory(), folderName)
            var success = true
            if (!dir.exists()) {
                success = dir.mkdirs()
            }
            if (success) {
                val date = Date()
                imageFile = File(dir.absolutePath + File.separator + Timestamp(date.time).toString() + "$fileName.${fileFormatString[fileFormat]}")
                imageFile.createNewFile()
            } else {
                return
            }

            val byteArrayOutputStream = ByteArrayOutputStream()
            if (fileFormat == 0) {
                bitmap.compress(CompressFormat.PNG, imageQuality, byteArrayOutputStream)
            } else {
                bitmap.compress(CompressFormat.JPEG, imageQuality, byteArrayOutputStream)
            }

            val fileOutputStream = FileOutputStream(imageFile)
            fileOutputStream.write(byteArrayOutputStream.toByteArray())
            fileOutputStream.close()

            val values = ContentValues()
            values.put(MediaStore.Images.Media.MIME_TYPE, "image/${fileFormatString[fileFormat]}")
            values.put(MediaStore.MediaColumns.DATA, imageFile.absolutePath)

        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

}
