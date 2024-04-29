package com.lolo.io.onelist.feature.lists

import androidx.activity.ComponentActivity
import com.lolo.io.onelist.core.data.di.dataModule
import com.lolo.io.onelist.core.data.reporitory.OneListRepository
import com.lolo.io.onelist.core.testing.fake.fakeDataModule
import com.lolo.io.onelist.core.database.di.daosModule
import com.lolo.io.onelist.core.designsystem.OneListTheme
import com.lolo.io.onelist.core.domain.di.domainModule
import com.lolo.io.onelist.core.testing.core.AbstractComposeTest
import com.lolo.io.onelist.core.testing.data.createFakeListWhereAllItemsHaveComment
import com.lolo.io.onelist.core.testing.data.createTestList
import com.lolo.io.onelist.core.testing.fake.FakeOneListRepository
import com.lolo.io.onelist.core.testing.fake.FakeSharedPreferenceHelper
import com.lolo.io.onelist.core.testing.fake.fakeOneListRepository
import com.lolo.io.onelist.core.testing.fake.fakeSharedPreferenceHelper
import com.lolo.io.onelist.core.testing.rules.KoinTestRule
import com.lolo.io.onelist.di.appModule
import com.lolo.io.onelist.feature.lists.di.listsModule
import com.lolo.io.onelist.feature.settings.di.settingsModule
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.koin.androidx.compose.KoinAndroidContext
import org.koin.core.annotation.KoinExperimentalAPI
import java.util.UUID

class ListScreenTest : AbstractComposeTest(
    ComponentActivity::class.java,
) {

    private var repository: FakeOneListRepository
    private var preferencesHelper: FakeSharedPreferenceHelper

    @get:Rule(order = Int.MIN_VALUE)
    val koinTestRule =
        KoinTestRule(
            koinModules = listOf(
                listsModule,
                fakeDataModule(
                    sharedPreferencesHelper = FakeSharedPreferenceHelper().apply {
                        firstLaunch = false
                    }.also {
                        preferencesHelper = it
                    },
                    repository = FakeOneListRepository(preferencesHelper)
                        .also { repository = it },

                    ),
                daosModule,
                domainModule
            )

        )

    @OptIn(KoinExperimentalAPI::class)
    @Before
    fun setUp() {
        composeTestRule.setContent {
            KoinAndroidContext {
                OneListTheme {
                    ListsScreen {}
                }
            }
        }
    }

    @Test
    fun swipeDeleteItem() = runTest {
        with(composeTestRule) {
            val randomIndex = repository.selectedList.items.indices
                .random()

            sharedTestSwipeDeleteItem(randomIndex)
        }
    }

    @Test
    fun swipeEditItem() = runTest {

        repository.setFakeLists(
            createFakeListWhereAllItemsHaveComment()
        )

        with(composeTestRule) {
            val randomIndex = repository.selectedList.items.indices
                .random()
            val item = repository.selectedList.items[randomIndex]

            sharedTestSwipeEditItem(randomIndex, item.title, item.comment)
        }
    }

    @Test
    fun createList() {
        repository.setFakeLists(
            listOf()
        )
        internalCreateList()
    }

    @Test
    fun createLists() {
        repository.setFakeLists(
            listOf()
        )
        (1..3).forEach { _ ->
            internalCreateList()
        }
    }

    @Test
    fun editList() {
        val itemList = createTestList()
        repository.setFakeLists(
            listOf(itemList)
        )
        with(composeTestRule) {

            val listNameSuffix = UUID.randomUUID().toString().substring(0, 8)
            sharedTestEditList(itemList.title, listNameSuffix)
        }
    }


    // todo add items to list
    // todo delete list
    // todo select list

    private fun internalCreateList() {
        with(composeTestRule) {
            val listName = UUID.randomUUID().toString().substring(0, 8)
            sharedTestCreateList(listName)
        }
    }
}

