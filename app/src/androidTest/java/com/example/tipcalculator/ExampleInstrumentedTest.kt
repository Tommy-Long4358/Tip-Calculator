package com.example.tipcalculator

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performTextInput
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.tipcalculator.ui.theme.TipCalculatorTheme

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*
import org.junit.Rule
import java.text.NumberFormat

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest
{
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun useAppContext()
    {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("com.example.tipcalculator", appContext.packageName)
    }


    @Test
    fun calculate_20_percent_tip()
    {
        composeTestRule.setContent {
            TipCalculatorTheme {
                TipTimeLayout()
            }
        }

        // UI components can be accessed as nodes through composeTestRule
        // Populate the TextField for bill amount with a 10 value
        composeTestRule.onNodeWithText("Bill Amount").performTextInput("10")
        composeTestRule.onNodeWithText("Tip Percentage").performTextInput("20")

        // Expected tip to be given by UI
        val expectedTip = NumberFormat.getCurrencyInstance().format(2)

        // Check if node exists in current UI
        composeTestRule.onNodeWithText("Tip Amount: $expectedTip").assertExists("No node with this text was found")
    }
}