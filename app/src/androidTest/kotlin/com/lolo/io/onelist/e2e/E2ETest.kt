package com.lolo.io.onelist.e2e

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.lolo.io.onelist.MainActivity
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalTestApi::class)
class E2ETest {

    @get:Rule(order = Int.MIN_VALUE)
    val koinTestRule = KoinTestRule()

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun test() = runTest {
        composeTestRule.onNodeWithText("1ListDev").assertExists()
    }

}