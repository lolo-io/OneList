package com.lolo.io.onelist.feature.lists

import com.lolo.io.onelist.core.data.datamodel.ErrorLoadingList
import com.lolo.io.onelist.core.data.datamodel.ListsWithErrors
import com.lolo.io.onelist.core.testing.data.createTestList
import com.lolo.io.onelist.core.testing.data.testItemWithComment
import com.lolo.io.onelist.core.testing.data.testLists
import com.lolo.io.onelist.core.testing.fake.FakeOneListRepository
import com.lolo.io.onelist.core.testing.fake.FakeSharedPreferenceHelper
import com.lolo.io.onelist.core.testing.fake.FakeUseCases
import com.lolo.io.onelist.core.testing.rules.MainDispatcherRule
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ListScreenViewModelTest {

    @get:Rule
    val dispatcherRule = MainDispatcherRule()

    private lateinit var viewModel: ListScreenViewModel
    private lateinit var fakeRepository: FakeOneListRepository
    private lateinit var fakeSharedPreferenceHelper: FakeSharedPreferenceHelper
    private lateinit var fakeUseCases: FakeUseCases

    @Before
    fun setup() {
        viewModel = ListScreenViewModel(
            FakeUseCases(FakeOneListRepository(FakeSharedPreferenceHelper().also {
                fakeSharedPreferenceHelper = it
            }).also {
                fakeRepository = it
            }).also {
                fakeUseCases = it
            }, fakeSharedPreferenceHelper
        )
    }


    @Test
    fun getAllLists() = runTest {

        val collectJob =
            launch(UnconfinedTestDispatcher()) { viewModel.allLists.collect() }

        val lists = testLists
        fakeRepository.setFakeLists(lists)

        val actual = viewModel.allLists
        assertEquals(lists, actual.value)

        collectJob.cancel()
    }

    @Test
    fun getDisplayedItems_and_getSelectedList() = runTest {

        val collectJob =
            launch(UnconfinedTestDispatcher()) { viewModel.displayedItems.collect() }
        val collectJob2 =
            launch(UnconfinedTestDispatcher()) { viewModel.selectedList.collect() }

        val lists = testLists
        fakeRepository.setFakeLists(lists)
        fakeSharedPreferenceHelper.selectedListIndex = 0

        val actual = viewModel.displayedItems
        assertEquals(lists[0].items, actual.value)

        fakeSharedPreferenceHelper.selectedListIndex = 1
        assertEquals(lists[1].items, actual.value)

        collectJob.cancel()
        collectJob2.cancel()
    }

    @Test
    fun resetError_clears_error_message() {
        // insert an error
        fakeRepository.testMutableAllListsWithErrors.value =
            ListsWithErrors(testLists, listOf(ErrorLoadingList.FileMissingError))

        viewModel.resetError()
        assertNull(viewModel.errorMessage.value, "Error message should be null")
    }

    @Test
    fun error_message_is_updated() {
        // insert an error
        fakeRepository.testMutableAllListsWithErrors.value =
            ListsWithErrors(testLists, listOf(ErrorLoadingList.FileMissingError))

        assertNotNull(viewModel.errorMessage.value, "Error message should be null")
        assertContains(
            viewModel.errorMessage.value!!.restResIds, R.string.error_file_missing,
            "Error message should be file missing"
        )
    }

    @Test
    fun refresh() = runTest {

        val collectJob =
            launch(UnconfinedTestDispatcher()) { viewModel.isRefreshing.collect() }

        viewModel.refresh(false)
        assertEquals(false, viewModel.isRefreshing.value)
        assertGetterFunctionCalled(
            FakeUseCases::loadAllLists.name
        )

        collectJob.cancel()
    }

    @Test
    fun createList() = runTest {

        val collectJob =
            launch(UnconfinedTestDispatcher()) { viewModel.allLists.collect() }

        val newList = createTestList()

        viewModel.createList(newList)
        val actual = viewModel.allLists.value.first()
        assertEquals(newList.title, actual.title)
        assertEquals(newList.items, actual.items)

        collectJob.cancel()
    }

    @Test
    fun editList() = runTest {
        val collectJob =
            launch(UnconfinedTestDispatcher()) { viewModel.allLists.collect() }
        val collectJob2 =
            launch(UnconfinedTestDispatcher()) { viewModel.selectedList.collect() }

        val lists = testLists
        fakeRepository.setFakeLists(lists)
        fakeSharedPreferenceHelper.selectedListIndex = 1


        viewModel.editItem(lists[1].items[1].copy(title = "Edited Title"))
        val actual = viewModel.allLists.value
        assertEquals("Edited Title", actual[1].items[1].title)

        assertGetterFunctionCalled(FakeUseCases::editItemOfList.name)

        collectJob.cancel()
        collectJob2.cancel()
    }

    @Test
    fun selectList() {
        val lists = testLists
        fakeRepository.setFakeLists(lists)
        viewModel.selectList(lists[1])
        assertGetterFunctionCalled(FakeUseCases::selectList.name)
    }

    @Test
    fun reorderLists() = runTest {
        val collectJob =
            launch(UnconfinedTestDispatcher()) { viewModel.selectedList.collect() }
        val collectJob2 =
            launch(UnconfinedTestDispatcher()) { viewModel.allLists.collect() }

        val lists = testLists
        fakeRepository.setFakeLists(lists)

        fakeSharedPreferenceHelper.selectedListIndex = 1
        val selectedList = lists[1]

        val listsShuffled = lists.shuffled()

        viewModel.reorderLists(listsShuffled)

        val expected = fakeUseCases.reorderLists(listsShuffled, selectedList)
        assertGetterFunctionCalled(FakeUseCases::reorderLists.name)

        assertEquals(expected.map { it.id }, viewModel.allLists.value.map { it.id })

        collectJob.cancel()
        collectJob2.cancel()
    }

    @Test
    fun deleteList_first_noDeletFile() = runTest {
        val collectJob =
            launch(UnconfinedTestDispatcher()) { viewModel.selectedList.collect() }
        val collectJob2 =
            launch(UnconfinedTestDispatcher()) { viewModel.allLists.collect() }

        val lists = testLists
        fakeRepository.setFakeLists(lists)
        fakeSharedPreferenceHelper.selectedListIndex = 0

        var methodCalled = false
        viewModel.deleteList(lists[0], false) {
            methodCalled = true
        }
        assertFalse(methodCalled)
        assertTrue { viewModel.allLists.value.none { it.id == lists[0].id } }
        assertEquals(lists[1], viewModel.selectedList.value)

        collectJob.cancel()
        collectJob2.cancel()
    }

    @Test
    fun deleteList_last_deleteFile() = runTest {
        val collectJob =
            launch(UnconfinedTestDispatcher()) { viewModel.selectedList.collect() }
        val collectJob2 =
            launch(UnconfinedTestDispatcher()) { viewModel.allLists.collect() }

        val lists = testLists
        fakeRepository.setFakeLists(lists)
        fakeSharedPreferenceHelper.selectedListIndex = 0

        var methodCalled = false
        viewModel.deleteList(lists[lists.size - 1], true) {
            methodCalled = true
        }
        assertTrue(methodCalled)
        assertTrue { viewModel.allLists.value.none { it.id == lists[lists.size - 1].id } }
        assertEquals(lists[lists.size - 2], viewModel.selectedList.value)

        collectJob.cancel()
        collectJob2.cancel()
    }

    @Test
    fun clearList() {
        viewModel.clearList(createTestList())
        assertGetterFunctionCalled(
            FakeUseCases::clearList.name
        )
    }

    @Test
    fun onSelectedListReordered() = runTest {
        val collectJob =
            launch(UnconfinedTestDispatcher()) { viewModel.selectedList.collect() }
        fakeSharedPreferenceHelper.selectedListIndex = 0
        val lists = testLists
        fakeRepository.setFakeLists(lists)

        viewModel.onSelectedListReordered(listOf())

        assertGetterFunctionCalled(
            FakeUseCases::setItemsOfList.name
        )
        collectJob.cancel()
    }

    @Test
    fun switchItemStatus() = runTest {
        val collectJob =
            launch(UnconfinedTestDispatcher()) { viewModel.selectedList.collect() }
        fakeSharedPreferenceHelper.selectedListIndex = 0
        val lists = testLists
        fakeRepository.setFakeLists(lists)

        viewModel.switchItemStatus(testItemWithComment)

        assertGetterFunctionCalled(
            FakeUseCases::switchItemStatus.name
        )

        collectJob.cancel()
    }

    @Test
    fun switchItemCommentShown() = runTest {
        val collectJob =
            launch(UnconfinedTestDispatcher()) { viewModel.selectedList.collect() }
        fakeSharedPreferenceHelper.selectedListIndex = 0
        val lists = testLists
        fakeRepository.setFakeLists(lists)

        viewModel.switchItemCommentShown(testItemWithComment)

        assertGetterFunctionCalled(
            FakeUseCases::switchItemCommentShown.name
        )

        collectJob.cancel()
    }

    @Test
    fun addItem() = runTest {
        val collectJob =
            launch(UnconfinedTestDispatcher()) { viewModel.selectedList.collect() }
        fakeSharedPreferenceHelper.selectedListIndex = 0
        val lists = testLists
        fakeRepository.setFakeLists(lists)

        viewModel.addItem(testItemWithComment)

        assertGetterFunctionCalled(
            FakeUseCases::addItemToList.name
        )

        collectJob.cancel()
    }

    @Test
    fun createListThenAddItem() = runTest {
        val collectJob =
            launch(UnconfinedTestDispatcher()) { viewModel.selectedList.collect() }
        fakeSharedPreferenceHelper.selectedListIndex = 0
        val lists = testLists
        fakeRepository.setFakeLists(lists)

        viewModel.createListThenAddItem(createTestList(), testItemWithComment)

        assertGetterFunctionCalled(
            FakeUseCases::createList.name
        )
        assertGetterFunctionCalled(
            FakeUseCases::addItemToList.name
        )

        collectJob.cancel()
    }

    @Test
    fun removeItem() = runTest {

        val collectJob =
            launch(UnconfinedTestDispatcher()) { viewModel.selectedList.collect() }
        fakeSharedPreferenceHelper.selectedListIndex = 0
        val lists = testLists
        fakeRepository.setFakeLists(lists)

        viewModel.removeItem(testItemWithComment)

        assertGetterFunctionCalled(
            FakeUseCases::removeItemFromList.name
        )

        collectJob.cancel()
    }

    @Test
    fun editItem()  = runTest {
        val collectJob =
            launch(UnconfinedTestDispatcher()) { viewModel.selectedList.collect() }
        fakeSharedPreferenceHelper.selectedListIndex = 0
        val lists = testLists
        fakeRepository.setFakeLists(lists)

        viewModel.editItem(testItemWithComment)

        assertGetterFunctionCalled(
            FakeUseCases::editItemOfList.name
        )

        collectJob.cancel()
    }

    private fun assertGetterFunctionCalled(functionName: String) {
        assertContains(
            fakeUseCases.calledFunctions.map { it.lowercase() },
            "get${functionName.lowercase()}"
        )
    }
}