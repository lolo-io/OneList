package com.lolo.io.onelist.e2e

import com.lolo.io.onelist.MainActivity
import com.lolo.io.onelist.core.data.di.dataModule
import com.lolo.io.onelist.core.database.di.daosModule
import com.lolo.io.onelist.core.domain.di.domainModule
import com.lolo.io.onelist.core.testing.core.AbstractComposeTest
import com.lolo.io.onelist.core.testing.rules.KoinTestRule
import com.lolo.io.onelist.di.appModule
import com.lolo.io.onelist.feature.lists.di.listsModule
import com.lolo.io.onelist.feature.lists.sharedTestSwipeDeleteItem
import com.lolo.io.onelist.feature.lists.tuto.FirstLaunchLists
import com.lolo.io.onelist.feature.settings.di.settingsModule
import org.junit.Rule
import org.junit.Test
import org.koin.androidx.fragment.android.KoinFragmentFactory
import org.koin.core.context.startKoin


class E2EListActionsTest: AbstractComposeTest(
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
    fun testSwipeDeleteItem() {

        with(composeTestRule) {
            val randomIndex = firstLaunchLists[1].items.indices
                .random()
            sharedTestSwipeDeleteItem(randomIndex)
        }
    }

}

