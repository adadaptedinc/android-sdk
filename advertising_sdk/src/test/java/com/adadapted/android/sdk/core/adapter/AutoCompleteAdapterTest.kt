package com.adadapted.android.sdk.core.adapter

import android.R
import android.widget.AutoCompleteTextView
import androidx.test.platform.app.InstrumentationRegistry
import com.adadapted.android.sdk.core.session.SessionClient
import com.adadapted.android.sdk.ui.adapter.AutoCompleteAdapter
import com.nhaarman.mockitokotlin2.mock
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class AutoCompleteAdapterTest {
    private lateinit var testAutoCompleteAdapter: AutoCompleteAdapter
    private val testContext = InstrumentationRegistry.getInstrumentation().targetContext

    @Before
    fun setup() {
        SessionClient.createInstance(mock(), mock())
        val items: ArrayList<String> = arrayListOf("Milk", "Eggs", "Bread")
        testAutoCompleteAdapter = AutoCompleteAdapter(testContext, R.layout.simple_list_item_1, items)
    }

    @Test
    fun getFilterMatchTest() {
        val autoCompleteTextView = AutoCompleteTextView(testContext)
        autoCompleteTextView.setAdapter(testAutoCompleteAdapter)
        autoCompleteTextView.setText("mil")
        assertEquals("Milk", autoCompleteTextView.adapter.getItem(0))
    }

    @Test
    fun suggestionSelectedTest() {
        val autoCompleteTextView = AutoCompleteTextView(testContext)
        autoCompleteTextView.setAdapter(testAutoCompleteAdapter)
        autoCompleteTextView.setText("bre")
        testAutoCompleteAdapter.suggestionSelected("Bread")
        assert(autoCompleteTextView.adapter.areAllItemsEnabled())
        assertEquals("Bread", autoCompleteTextView.adapter.getItem(0))
    }
}