package com.lolo.io.onelist.feature.settings

import androidx.test.espresso.Espresso
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import com.lolo.io.onelist.core.database.di.daosModule
import com.lolo.io.onelist.core.domain.di.domainModule
import com.lolo.io.onelist.core.testing.TestActivityWithSimpleStorage
import com.lolo.io.onelist.core.testing.core.AbstractComposeTest
import com.lolo.io.onelist.core.testing.fake.FakeOneListRepository
import com.lolo.io.onelist.core.testing.fake.FakeSharedPreferenceHelper
import com.lolo.io.onelist.core.testing.fake.fakeDataModule
import com.lolo.io.onelist.core.testing.rules.KoinTestRule
import com.lolo.io.onelist.feature.settings.fake.fakeSettingsModule
import org.hamcrest.Matchers
import org.junit.Rule
import org.junit.Test

class SettingsScreenTest : AbstractComposeTest(
    TestActivityWithSimpleStorage::class.java
) {

    private var repository: FakeOneListRepository
    private var preferencesHelper: FakeSharedPreferenceHelper

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

    @Test
    fun settingsFragmentIsInflated() {
        composeTestRule.setContent {
            SettingsScreen(
                simpleStorageHelper = (composeTestRule.activity as TestActivityWithSimpleStorage)
                    .simpleStorage
            ) {
                Espresso.onView(Matchers.allOf(withId(R.id.settings_fragment), isDisplayed()))
            }
        }
    }
}