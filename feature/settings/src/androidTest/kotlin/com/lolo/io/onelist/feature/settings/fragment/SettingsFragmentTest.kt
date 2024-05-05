package com.lolo.io.onelist.feature.settings.fragment

import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.preference.PreferenceManager
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.rule.GrantPermissionRule
import com.lolo.io.onelist.core.data.shared_preferences.SharedPreferencesHelper
import com.lolo.io.onelist.core.database.di.daosModule
import com.lolo.io.onelist.core.domain.di.domainModule
import com.lolo.io.onelist.core.testing.fake.FakeOneListRepository
import com.lolo.io.onelist.core.testing.fake.FakeSharedPreferenceHelper
import com.lolo.io.onelist.core.testing.fake.fakeDataModule
import com.lolo.io.onelist.core.testing.rules.KoinTestRule
import com.lolo.io.onelist.feature.settings.R
import com.lolo.io.onelist.feature.settings.fake.fakeSettingsModule
import kotlinx.coroutines.runBlocking
import org.hamcrest.Matchers
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test


class SettingsFragmentTest {

    private var repository: FakeOneListRepository
    private var preferencesHelper: FakeSharedPreferenceHelper

    @get:Rule
    var permissionRule: GrantPermissionRule = GrantPermissionRule.grant(
        android.Manifest.permission.READ_EXTERNAL_STORAGE
    )

    private lateinit var scenario: FragmentScenario<SettingsFragment>

    @get:Rule(order = Int.MIN_VALUE)
    val koinTestRule =
        KoinTestRule(
            koinModules = listOf(
                fakeDataModule(
                    sharedPreferencesHelper = FakeSharedPreferenceHelper().apply {
                        firstLaunch = false
                    }.also {
                        preferencesHelper = it
                    },
                    repository = FakeOneListRepository(preferencesHelper)
                        .also { repository = it },
                ),
                fakeSettingsModule,
                daosModule,
                domainModule
            )
        )


    @Before
    fun setUp() {
        scenario = launchFragmentInContainer<SettingsFragment>()
        setSharedPref(SharedPreferencesHelper.THEME_PREF, "light")
        scenario.onFragment{ fragment ->
            fragment.onClickOnShowReleaseNote = {}
        }
    }

    @Test
    fun changeTheme() {
        clickOn(R.string.settings_theme)
        clickOn(R.string.settings_theme_dark)
        assertSharedPrefEquals(SharedPreferencesHelper.THEME_PREF, "dark")
        clickOn(R.string.settings_theme)
        clickOn(R.string.settings_theme_light)
        assertSharedPrefEquals(SharedPreferencesHelper.THEME_PREF, "light")
    }

    @Test
    fun showReleaseNote() = runBlocking {
        var methodCalled = false
        scenario.onFragment { fragment ->
            fragment.onClickOnShowReleaseNote = {
                methodCalled = true
            }
        }

        clickOn(R.string.show_last_release_note)

        assertTrue(methodCalled)
    }

    private fun clickOn(resId: Int) {
        Espresso.onView(
            Matchers.allOf(
                ViewMatchers.withText(resId),
                ViewMatchers.isDisplayed()
            )
        ).perform(ViewActions.click())
    }

    private fun assertSharedPrefEquals(prefKey: String, expected: String) {
        scenario.onFragment { fragment ->
            val sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(fragment.context!!.applicationContext)

            val actual = sharedPreferences.getString(
                prefKey,
                "null"
            ) ?: "null"

            assertEquals(expected, actual)
        }

    }

    private fun setSharedPref(key: String, value: String?) {
        scenario.onFragment { fragment ->
            with(fragment) {
                val sharedPreferences =
                    PreferenceManager.getDefaultSharedPreferences(this.context!!.applicationContext)
                sharedPreferences.edit()
                    .putString(key, value)
                    .apply()
            }
        }
    }
}