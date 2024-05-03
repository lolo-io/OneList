package com.lolo.io.onelist.core.testing.util

import android.app.Activity
import androidx.activity.ComponentActivity
import androidx.test.core.app.ActivityScenario
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.runTest

suspend fun assertWaiting(
    timeoutInSec: Int = 10,
    messageOnFail: String? = null,
    condition: () -> Boolean
) {

    var timeout = timeoutInSec
    while (timeout > 0 && !condition()) {
        delay(1000)
        timeout -= 1
    }
    assert(condition()) {
        messageOnFail ?: "Condition could not be respected after $timeoutInSec seconds"
    }
}

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