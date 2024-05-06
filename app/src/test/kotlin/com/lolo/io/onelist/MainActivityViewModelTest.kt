package com.lolo.io.onelist

import com.lolo.io.onelist.core.testing.fake.FakeFirstLaunchLists
import com.lolo.io.onelist.core.testing.fake.FakeOneListRepository
import com.lolo.io.onelist.core.testing.fake.FakeSharedPreferenceHelper
import com.lolo.io.onelist.core.testing.fake.FakeUseCases
import com.lolo.io.onelist.core.testing.rules.MainDispatcherRule
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertContains

class MainActivityViewModelTest {


    @get:Rule
    val dispatcherRule = MainDispatcherRule()

    private lateinit var viewModel: MainActivityViewModel
    private lateinit var fakeRepository: FakeOneListRepository
    private lateinit var fakeSharedPreferenceHelper: FakeSharedPreferenceHelper
    private lateinit var fakeUseCases: FakeUseCases

    @Before
    fun setup() {
        viewModel = MainActivityViewModel(
            firstLaunchLists = FakeFirstLaunchLists(),
            useCases = FakeUseCases(FakeOneListRepository(FakeSharedPreferenceHelper().also {
                fakeSharedPreferenceHelper = it
            }).also {
                fakeRepository = it
            }).also {
                fakeUseCases = it
            },
            preferences = fakeSharedPreferenceHelper
        )
    }

    @Test
    fun testInit_callShouldShowWhatsNew() = runBlocking {

        val collectJob =
            launch(UnconfinedTestDispatcher()) { viewModel.listsLoaded.collect() }

        fakeSharedPreferenceHelper.version = "0.1.1"
        fakeSharedPreferenceHelper.firstLaunch = true

        viewModel.init()

        assertGetterFunctionCalled(FakeUseCases::handleFirstLaunch.name)
        assertGetterFunctionCalled(FakeUseCases::shouldShowWhatsNew.name)

        assertTrue(viewModel.listsLoaded.value)

        collectJob.cancel()
    }

    @Test
    fun testInit_noCallShouldShowWhatsNew() = runBlocking {

        val collectJob =
            launch(UnconfinedTestDispatcher()) { viewModel.listsLoaded.collect() }

        fakeSharedPreferenceHelper.version = BuildConfig.VERSION_NAME
        fakeSharedPreferenceHelper.firstLaunch = true

        viewModel.init()

        assertGetterFunctionCalled(FakeUseCases::handleFirstLaunch.name)

        assertTrue(
            fakeUseCases.calledFunctions.none {
                it.lowercase() == "get${
                    FakeUseCases::shouldShowWhatsNew.name.lowercase().lowercase()
                }"
            }
        )

        assertTrue(viewModel.listsLoaded.value)

        collectJob.cancel()
    }

    private fun assertGetterFunctionCalled(functionName: String) {
        assertContains(
            fakeUseCases.calledFunctions.map { it.lowercase() },
            "get${functionName.lowercase()}"
        )
    }
}