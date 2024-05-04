package com.lolo.io.onelist.core.domain.use_cases

import com.lolo.io.onelist.core.testing.data.createTestList
import com.lolo.io.onelist.core.testing.fake.FakeOneListRepository
import com.lolo.io.onelist.core.testing.fake.FakeSharedPreferenceHelper
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals

class ShouldShowWhatsNewTest {
    private lateinit var preferences: FakeSharedPreferenceHelper
    private lateinit var shouldShowWhatsNew: ShouldShowWhatsNew

    @Before
    fun setUp() {
        preferences = FakeSharedPreferenceHelper()
        shouldShowWhatsNew = ShouldShowWhatsNew(preferences)
    }

    @Test
    fun shouldShowWhatsNewUseCase_majorChange() = runTest {
        preferences.version = "1.1.0"
        val actual = shouldShowWhatsNew("2.0.0")
        assertEquals(true, actual)
    }

    @Test
    fun shouldShowWhatsNewUseCase_minorChange() = runTest {
        preferences.version = "1.1.0"
        val actual = shouldShowWhatsNew("1.2.0")
        assertEquals(true, actual)
    }

    @Test
    fun shouldShowWhatsNewUseCase_patchChange() = runTest {
        preferences.version = "1.1.0"
        val actual = shouldShowWhatsNew("1.1.1")
        assertEquals(false, actual)
    }
}