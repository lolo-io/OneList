package com.lolo.io.onelist.core.domain.use_cases

import com.lolo.io.onelist.core.testing.data.createTestList
import com.lolo.io.onelist.core.testing.fake.FakeOneListRepository
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import kotlin.test.assertContains

class SyncAllListsTest {
    private lateinit var repository: FakeOneListRepository
    private lateinit var syncAllLists: SyncAllLists

    @Before
    fun setUp() {
        repository = FakeOneListRepository()
        syncAllLists = SyncAllLists(repository)
    }

    @Test
    fun syncAllListsUseCase() = runTest {
        syncAllLists()
        assertContains(
            repository.calledFunctions,
            FakeOneListRepository::backupAllListsToFiles.name
        )
    }
}