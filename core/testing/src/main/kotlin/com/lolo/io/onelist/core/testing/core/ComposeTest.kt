package com.lolo.io.onelist.core.testing.core

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import org.junit.Rule

open class ComposeTest {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()


    protected fun assertNodeTagIsShown(tag: String): SemanticsNodeInteraction {
        return composeTestRule
            .onNodeWithTag(tag)
            .assertExists()
    }

    protected fun assertNodeTagContainsText(tag: String, text: String): SemanticsNodeInteraction {
        return composeTestRule
            .onNodeWithTag(tag)
            .assertExists()
            .assertTextContains(text)
    }

    protected fun assertNodeTagIsNotShown(tag: String) {
        composeTestRule
            .onNodeWithTag(tag)
            .assertDoesNotExist()
    }

    protected fun assertTextIsShown(text: String): SemanticsNodeInteraction {
        return composeTestRule
            .onNodeWithText(text)
            .assertExists()
    }

    protected fun assertTextIsNotShown(text: String) {
        return composeTestRule
            .onNodeWithText(text)
            .assertDoesNotExist()
    }

}