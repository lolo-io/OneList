package com.lolo.io.onelist.core.domain.use_cases

import android.net.Uri
import com.lolo.io.onelist.core.testing.fake.FakeOneListRepository
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import kotlin.test.assertContains

@RunWith(RobolectricTestRunner::class)
class ImportListTest {
    private lateinit var repository: FakeOneListRepository
    private lateinit var importList : ImportList

    @Before
    fun setUp() {
        repository = FakeOneListRepository()
        importList = ImportList(repository)
    }

    @Test
    fun importListTestUseCase() = runTest {
        repository.importList(Uri.EMPTY)
        assertContains(repository.calledFunctions, FakeOneListRepository::importList.name)
    }
}