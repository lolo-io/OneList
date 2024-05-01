package com.lolo.io.onelist.core.data.shared_preferences

import android.app.Activity
import android.app.Application
import androidx.activity.ComponentActivity
import androidx.test.core.app.ActivityScenario
import com.lolo.io.onelist.core.testing.TestApplication
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.File
import kotlin.test.assertEquals

@RunWith(RobolectricTestRunner::class)
@Config(application = TestApplication::class)
class SharedPreferencesHelperTest {
    @Test
    fun backupDisplayPath() {
        withActivity {
            testSharedPreference(
                changePref = { backupDisplayPath = "FAKE" },
                expected = "FAKE",
                actual = { backupDisplayPath }
            )
            testSharedPreference(
                changePref = { backupDisplayPath = null },
                expected = null,
                actual = { backupDisplayPath }
            )
        }
    }

    @Test
    fun backupDisplayUri() {
        withActivity {
            testSharedPreference(
                changePref = { backupUri = "FAKE" },
                expected = "FAKE",
                actual = { backupUri }
            )
            testSharedPreference(
                changePref = { backupUri = null },
                expected = null,
                actual = { backupUri }
            )
        }
    }

    @Test
    fun version() {
        withActivity {
            testSharedPreference(
                changePref = { version = "7.7.7" },
                expected = "7.7.7",
                actual = { version }
            )
        }
    }

    @Test
    fun theme() {
        withActivity {
            testSharedPreference(
                changePref = { theme = "custom" },
                expected = "custom",
                actual = { theme }
            )
        }
    }

    @Test
    fun firstLaunch() {
        withActivity {
            testSharedPreference(
                changePref = { firstLaunch = false },
                expected = false,
                actual = { firstLaunch }
            )
            testSharedPreference(
                changePref = { firstLaunch = true },
                expected = true,
                actual = { firstLaunch }
            )
        }
    }

    @Test
    fun preferUseFiles() {
        withActivity {
            testSharedPreference(
                changePref = { preferUseFiles = true },
                expected = true,
                actual = { preferUseFiles }
            )
            testSharedPreference(
                changePref = { preferUseFiles = false },
                expected = false,
                actual = { preferUseFiles }
            )
        }
    }

    @Test
    fun selectedListIndex() {
        withActivity {
            testSharedPreference(
                changePref = { selectedListIndex = 10 },
                expected = 10,
                actual = { selectedListIndex }
            )
        }
    }
    @Test
    fun selectedListIndexStateFlow() {

        withActivity {
            testSharedPreference(
                changePref = { selectedListIndex = 11 },
                expected = 11,
                actual = { selectedListIndexStateFlow.value }
            )
        }
    }
    @Test
    fun canAccessBackupUri_backupUriNull() {
        withActivity {
            val sharedPreferencesHelper = SharedPreferencesHelperImpl(
                this.application.applicationContext as Application
            )
            assertEquals(true, sharedPreferencesHelper.canAccessBackupUri)
        }
    }

    @Test
    fun canAccessBackupUri_badBackupUri() {
        withActivity {
            val sharedPreferencesHelper = SharedPreferencesHelperImpl(
                this.application.applicationContext as Application
            )
            sharedPreferencesHelper.backupUri = "Bad Uri"
            assertEquals(false, sharedPreferencesHelper.canAccessBackupUri)
        }
    }

    @Test
    fun canAccessBackupUri_goodBackupUri() {
        withActivity {
            val sharedPreferencesHelper = SharedPreferencesHelperImpl(
                this.application.applicationContext as Application
            )
            sharedPreferencesHelper.backupUri = File("Test").toURI().toString()
            assertEquals(true, sharedPreferencesHelper.canAccessBackupUri)
        }
    }

    private fun withActivity(block: Activity.() -> Unit) {
        val scenario = ActivityScenario.launch(ComponentActivity::class.java)
        scenario.onActivity { activity ->
            block(activity)
        }
    }

    private fun Activity.testSharedPreference(
        changePref: SharedPreferencesHelper.() -> Unit,
        expected: Any?,
        actual: SharedPreferencesHelper.() -> Any?,
    ) {
        val sharedPreferencesHelper = SharedPreferencesHelperImpl(
            this.application.applicationContext as Application
        )
        changePref(sharedPreferencesHelper)

        assertEquals(expected, actual(sharedPreferencesHelper))
    }

}