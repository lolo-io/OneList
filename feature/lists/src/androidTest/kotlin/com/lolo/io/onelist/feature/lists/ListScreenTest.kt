package com.lolo.io.onelist.feature.lists

import androidx.activity.ComponentActivity
import com.lolo.io.onelist.core.testing.fake.fakeDataModule
import com.lolo.io.onelist.core.database.di.daosModule
import com.lolo.io.onelist.core.designsystem.OneListTheme
import com.lolo.io.onelist.core.domain.di.domainModule
import com.lolo.io.onelist.core.testing.core.AbstractComposeTest
import com.lolo.io.onelist.core.testing.data.createEmptyTestList
import com.lolo.io.onelist.core.testing.data.createFakeListWhereAllItemsHaveComment
import com.lolo.io.onelist.core.testing.data.createTestList
import com.lolo.io.onelist.core.testing.data.testLists
import com.lolo.io.onelist.core.testing.fake.FakeOneListRepository
import com.lolo.io.onelist.core.testing.fake.FakeSharedPreferenceHelper
import com.lolo.io.onelist.core.testing.rules.KoinTestRule
import com.lolo.io.onelist.feature.lists.di.listsModule
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import org.koin.androidx.compose.KoinAndroidContext
import org.koin.core.annotation.KoinExperimentalAPI
import java.util.UUID
import kotlin.random.Random

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
    fun setUpUI() {
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
        repository.setFakeLists(testLists)
        setUpUI()

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
        setUpUI()

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
        setUpUI()

        internalCreateList()
    }

    @Test
    fun createLists() {
        repository.setFakeLists(
            listOf()
        )
        setUpUI()

        repeat(3) {
            internalCreateList()
        }
    }

    @Test
    fun editList() {
        val itemList = createTestList()
        repository.setFakeLists(
            listOf(itemList)
        )
        setUpUI()

        with(composeTestRule) {
            val listNameSuffix = UUID.randomUUID().toString().substring(0, 8)
            sharedTestEditList(itemList.title, listNameSuffix)
        }
    }


    @Test
    fun addItemsToList() {
        val itemList = createEmptyTestList()
        repository.setFakeLists(
            listOf(itemList)
        )
        setUpUI()

        with(composeTestRule) {
            val addedItemsTitles = mutableListOf<String>()

            repeat(5) {
                val itemTitle = UUID.randomUUID().toString().substring(0, 8)
                addedItemsTitles.add(itemTitle)
                val itemComment = if (Random.nextBoolean()) {
                    UUID.randomUUID().toString().substring(0, 16)
                } else ""
                sharedAddItemToList(itemTitle, itemComment)
            }

            sharedCheckListItemOrders(addedItemsTitles)
        }
    }

    @Test
    fun deleteList() {
        repository.setFakeLists(
            testLists
        )
        setUpUI()

        with(composeTestRule) {
            val displayedTestsLists = testLists.toMutableList()
            testLists.shuffled().forEach { itemList ->
                sharedDeleteList(itemList.title)
                val indexInTestsLists = displayedTestsLists.indexOf(itemList)
                if (indexInTestsLists < displayedTestsLists.size - 1) {
                    sharedCheckIsSelected(displayedTestsLists[indexInTestsLists + 1].title)
                }
                displayedTestsLists.remove(itemList)
            }
        }
    }

    @Test
    fun selectList() {
        repository.setFakeLists(
            testLists
        )
        setUpUI()
        with(composeTestRule) {
            val displayedTestsLists = testLists.toMutableList()
            testLists.shuffled().forEach { itemList ->
                sharedSelectList(itemList.title)
                displayedTestsLists.remove(itemList)
                displayedTestsLists.forEach {
                    sharedCheckIsNotSelected(it.title)
                }
            }
        }
    }

    @Test
    fun justClearList() {
        val itemList = createTestList()
        repository.setFakeLists(
            listOf(itemList)
        )
        setUpUI()

        with(composeTestRule) {
            sharedJustClearList(itemList.title)
        }
    }

    private fun internalCreateList() {
        with(composeTestRule) {
            val listName = UUID.randomUUID().toString().substring(0, 8)
            sharedTestCreateList(listName)
        }
    }
}

