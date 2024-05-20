package com.lolo.io.onelist.navigation

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.test.espresso.Espresso
import androidx.test.espresso.NoActivityResumedException
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import com.lolo.io.onelist.MainActivity
import com.lolo.io.onelist.core.data.di.dataModule
import com.lolo.io.onelist.core.data.utils.TestTags
import com.lolo.io.onelist.core.database.di.daosModule
import com.lolo.io.onelist.core.domain.di.domainModule
import com.lolo.io.onelist.core.testing.core.AbstractComposeTest
import com.lolo.io.onelist.core.testing.rules.KoinTestRule
import com.lolo.io.onelist.di.appModule
import com.lolo.io.onelist.feature.lists.di.listsModule
import com.lolo.io.onelist.feature.settings.di.settingsModule
import kotlinx.coroutines.test.runTest
import org.hamcrest.Matchers
import org.junit.Rule
import org.junit.Test
import androidx.test.espresso.matcher.ViewMatchers.withContentDescription
import androidx.test.espresso.matcher.ViewMatchers.withText
import com.lolo.io.onelist.feature.settings.R

class OneListNavHostTest : AbstractComposeTest(
    activityClass = MainActivity::class.java,
) {

    @get:Rule(order = Int.MIN_VALUE)
    val koinTestRule = KoinTestRule(
        koinModules = listOf(
            appModule,
            listsModule,
            settingsModule,
            dataModule,
            daosModule,
            domainModule
        )
    )

    @Test
    fun listsScreenToSettingsToWhatsNew_thenContinueThenBackArrow() = runTest {
        with(composeTestRule) {
            assertNavToSettingsThenWhatsNew()

            onNodeWithTag(TestTags.WhatsNewContinue).performClick()

            onNodeWithTag(TestTags.SettingsScreen).assertIsDisplayed()

            val backButton = Espresso.onView(
                Matchers.allOf(
                    withContentDescription(
                        androidx.appcompat.R.string.abc_action_bar_up_description),
                    isDisplayed()
                )
            )

            backButton.perform(ViewActions.click())
            onNodeWithTag(TestTags.ListsScreen).assertIsDisplayed()
        }
    }

    @Test
    fun listsScreenToSettingsToWhatsNew_thenBackButtonTwice() = runTest {
        with(composeTestRule) {
            assertNavToSettingsThenWhatsNew()
            Espresso.pressBack()
            onNodeWithTag(TestTags.SettingsScreen).assertIsDisplayed()
            Espresso.pressBack()
            onNodeWithTag(TestTags.ListsScreen).assertIsDisplayed()
        }
    }

    private fun assertNavToSettingsThenWhatsNew() {
        with(composeTestRule) {
            onNodeWithTag(TestTags.SettingsButton).assertIsDisplayed()
            onNodeWithTag(TestTags.SettingsButton).performClick()
            onNodeWithTag(TestTags.SettingsScreen).assertIsDisplayed()


            val releaseNoteButton = Espresso.onView(
                Matchers.allOf(
                    withText(R.string.show_last_release_note),
                    isDisplayed()
                )
            )

            releaseNoteButton.perform(ViewActions.click())

            onNodeWithTag(TestTags.WhatsNewScreen).assertIsDisplayed()
            onNodeWithTag(TestTags.WhatsNewContinue).assertIsDisplayed()
        }
    }

    @Test(expected = NoActivityResumedException::class)
    fun homeDestination_back_quitsApp() {
        composeTestRule.apply {
            Espresso.pressBack()
        }
    }

}