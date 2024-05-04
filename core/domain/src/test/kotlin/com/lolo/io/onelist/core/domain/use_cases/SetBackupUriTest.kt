package com.lolo.io.onelist.core.domain.use_cases

import com.lolo.io.onelist.core.testing.data.createTestList
import com.lolo.io.onelist.core.testing.fake.FakeOneListRepository
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import kotlin.test.assertContains

class SetBackupUriTest {
    private lateinit var repository: FakeOneListRepository
    private lateinit var setBackupUri: SetBackupUri

    @Before
    fun setUp() {
        repository = FakeOneListRepository()
        setBackupUri = SetBackupUri(repository)
    }

    @Test
    fun saveListToDbUseCase() = runTest {
        setBackupUri(null, null)
        assertContains(repository.calledFunctions, FakeOneListRepository::setBackupUri.name)
    }
}