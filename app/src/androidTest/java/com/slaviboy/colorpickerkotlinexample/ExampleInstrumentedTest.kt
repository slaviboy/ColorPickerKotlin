package com.slaviboy.colorpickerkotlinexample

import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.ActivityTestRule
import org.junit.Rule
import org.junit.Test

class ExampleInstrumentedTest {

    @get:Rule
    val activityRule: ActivityTestRule<MainActivity> = ActivityTestRule(MainActivity::class.java)

    @Test
    fun MainTest() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
    }
}