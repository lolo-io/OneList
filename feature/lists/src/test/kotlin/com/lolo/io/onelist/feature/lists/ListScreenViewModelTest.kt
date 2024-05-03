package com.lolo.io.onelist.feature.lists

import com.lolo.io.onelist.core.data.datamodel.ErrorLoadingList
import com.lolo.io.onelist.core.data.datamodel.ListsWithErrors
import com.lolo.io.onelist.core.testing.data.testLists
import com.lolo.io.onelist.core.testing.fake.FakeOneListRepository
import com.lolo.io.onelist.core.testing.fake.FakeSharedPreferenceHelper
import com.lolo.io.onelist.core.testing.fake.createFakeUseCases
import com.lolo.io.onelist.core.testing.rules.MainDispatcherRule
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertContains
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class ListScreenViewModelTest {

    @get:Rule
    val dispatcherRule = MainDispatcherRule()

    private lateinit var viewModel: ListScreenViewModel
    private var fakeRepository: FakeOneListRepository = FakeOneListRepository()

    @Before
    fun setup() {
        viewModel = ListScreenViewModel(
            createFakeUseCases(fakeRepository), FakeSharedPreferenceHelper()
        )
    }

    @Test
    fun resetError_clears_error_message()  {
        // insert an error
        fakeRepository.testMutableAllListsWithErrors.value =
            ListsWithErrors(testLists, listOf(ErrorLoadingList.FileMissingError))

        viewModel.resetError()
        assertNull(viewModel.errorMessage.value, "Error message should be null")
    }

    @Test
    fun error_message_is_updated()  {
        // insert an error
        fakeRepository.testMutableAllListsWithErrors.value =
            ListsWithErrors(testLists, listOf(ErrorLoadingList.FileMissingError))

        assertNotNull(viewModel.errorMessage.value, "Error message should be null")
        assertContains(viewModel.errorMessage.value!!.restResIds, R.string.error_file_missing,
            "Error message should be file missing")
    }

}