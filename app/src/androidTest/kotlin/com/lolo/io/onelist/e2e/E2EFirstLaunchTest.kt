package com.lolo.io.onelist.e2e

import com.lolo.io.onelist.MainActivity
import com.lolo.io.onelist.core.data.di.dataModule
import com.lolo.io.onelist.core.database.di.daosModule
import com.lolo.io.onelist.core.domain.di.domainModule
import com.lolo.io.onelist.core.testing.core.AbstractComposeTest
import com.lolo.io.onelist.core.testing.core.clickOnListChip
import com.lolo.io.onelist.core.testing.core.scrollToItemIfNecessary
import com.lolo.io.onelist.core.testing.core.testHasOnlyFirstLaunchSizeListsAmountOfLists
import com.lolo.io.onelist.core.testing.core.testItemIsInList
import com.lolo.io.onelist.core.testing.core.testItemsListIsScrollable
import com.lolo.io.onelist.core.testing.core.testListChipIsShown
import com.lolo.io.onelist.core.testing.rules.KoinTestRule
import com.lolo.io.onelist.di.appModule
import com.lolo.io.onelist.feature.lists.di.listsModule
import com.lolo.io.onelist.feature.lists.tuto.FirstLaunchLists
import com.lolo.io.onelist.feature.settings.di.settingsModule
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test


class E2EFirstLaunchTest:AbstractComposeTest(
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

    private val firstLaunchLists
        get() = FirstLaunchLists(composeTestRule.activity.application)
            .firstLaunchLists()

    @Test
    fun test() = runTest {
        with(composeTestRule) {
            testHasOnlyFirstLaunchSizeListsAmountOfLists(firstLaunchLists)
            firstLaunchLists.forEach {
                testListChipIsShown(it.title)
                clickOnListChip(it.title)
                testItemsListIsScrollable()
                it.items.forEachIndexed { index, item ->
                    scrollToItemIfNecessary(index)
                    testItemIsInList(item.title)
                }
            }
        }
    }
}