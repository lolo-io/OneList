package com.lolo.io.onelist.core.data.utils

import android.app.Activity
import androidx.activity.ComponentActivity
import androidx.core.net.toUri
import androidx.test.core.app.ActivityScenario
import kotlinx.coroutines.test.runTest
import java.io.File

const val TEST_FILES_FOLDER_PATH = "src/test/kotlin/com/lolo/io/onelist/core/data/test_files_folder"
val testFilesFolderPathUri
    get() = File(TEST_FILES_FOLDER_PATH).toUri()

const val EXISTING_TEST_LIST_FILE_NAME = "test_list_file"

val existingListFileUri
    get() =  File("$TEST_FILES_FOLDER_PATH/$EXISTING_TEST_LIST_FILE_NAME.1list").toUri()


fun withActivity(block: Activity.() -> Unit) {
    val scenario = ActivityScenario.launch(ComponentActivity::class.java)
    scenario.onActivity { activity ->
        block(activity)
    }
}

fun suspendWithActivity(block: suspend Activity.() -> Unit) {
    val scenario = ActivityScenario.launch(ComponentActivity::class.java)
    scenario.onActivity { activity ->
        runTest {
            block(activity)
        }
    }
}