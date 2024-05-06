package com.lolo.io.onelist.feature.settings.fragment

import android.net.Uri
import com.lolo.io.onelist.core.testing.fake.FakeOneListRepository
import com.lolo.io.onelist.core.testing.fake.FakeSharedPreferenceHelper
import com.lolo.io.onelist.core.testing.fake.FakeUseCases
import com.lolo.io.onelist.core.testing.rules.MainDispatcherRule
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@RunWith(RobolectricTestRunner::class)
class SettingsFragmentViewModelTest {

    @get:Rule
    val dispatcherRule = MainDispatcherRule()

    private lateinit var viewModel: SettingsFragmentViewModel
    private lateinit var fakeRepository: FakeOneListRepository
    private lateinit var fakePreferences: FakeSharedPreferenceHelper
    private lateinit var fakeUseCases: FakeUseCases

    @Before
    fun setUp() {
        viewModel = SettingsFragmentViewModel(
            FakeUseCases(FakeOneListRepository().also {
                fakeRepository = it
            }).also {
                fakeUseCases = it
            }, FakeSharedPreferenceHelper().also {
                fakePreferences = it
            }
        )
    }

    @Test
    fun getPreferUseFiles() {
        fakePreferences.preferUseFiles = true
        val actualTrue = viewModel.preferUseFiles
        assertTrue(actualTrue)

        fakePreferences.preferUseFiles = false
        val actualFalse = viewModel.preferUseFiles
        assertFalse(actualFalse)
    }

    @Test
    fun getVersion() {
        fakePreferences.version = "1.2.3"
        val actual = viewModel.version
        assertEquals("1.2.3", actual)
    }

    @Test
    fun setBackupPath() {
        fakePreferences.preferUseFiles = true
        viewModel.setBackupPath(Uri.parse("fake/path"), "fake/path")
        assertGetterFunctionCalled(
            fakeUseCases.calledFunctions,
            FakeUseCases::setBackupUri.name
        )
        assertTrue(fakePreferences.preferUseFiles)

        viewModel.setBackupPath(null, null)
        assertGetterFunctionCalled(
            fakeUseCases.calledFunctions,
            FakeUseCases::setBackupUri.name
        )
        assertFalse(fakePreferences.preferUseFiles)
    }


    @Test
    fun importList() = runTest {
        viewModel.importList(Uri.EMPTY)
        assertGetterFunctionCalled(
            fakeUseCases.calledFunctions,
            FakeUseCases::importList.name
        )
    }

    @Test
    fun backupAllListsOnDevice() = runTest {
        viewModel.backupAllListsOnDevice()
        assertGetterFunctionCalled(
            fakeUseCases.calledFunctions,
            FakeUseCases::syncAllLists.name
        )
    }

    @Test
    fun onPreferUseFiles() {
        viewModel.onPreferUseFiles()
        assertGetterFunctionCalled(
            fakeUseCases.calledFunctions,
            FakeUseCases::loadAllLists.name
        )
    }

    @Test
    fun getSyncFolderNotAccessible() {
        fakePreferences.setCanAccessBackupUri(true)
        val actualTrue = viewModel.syncFolderNotAccessible
        assertFalse(actualTrue)

        fakePreferences.setCanAccessBackupUri(false)
        val actualFalse = viewModel.syncFolderNotAccessible
        assertTrue(actualFalse)
    }


    fun assertGetterFunctionCalled(calledFunctions: List<String>, functionName: String) {
        assertContains(
            fakeUseCases.calledFunctions.map { it.lowercase() },
            "get${functionName.lowercase()}"
        )
    }
}