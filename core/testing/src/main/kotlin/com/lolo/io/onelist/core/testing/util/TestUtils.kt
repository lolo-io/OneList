package com.lolo.io.onelist.core.testing.util

import android.app.Activity
import androidx.activity.ComponentActivity
import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.test.core.app.ActivityScenario
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest

suspend fun assertWaiting(
    timeoutInSec: Int = 10,
    messageOnFail: String? = null,
    condition: () -> Boolean
) {

    runBlocking(Dispatchers.IO) {
        var timeout = timeoutInSec
        while (timeout > 0 && !condition()) {
            delay(1000)
            timeout -= 1
        }
        assert(condition()) {
            messageOnFail ?: "Condition could not be respected after $timeoutInSec seconds"
        }
    }
}

suspend fun assertWaitingNode(
    timeoutInSec: Int = 10,
    messageOnFail: String? = null,
    semanticsNodeInteraction: () -> SemanticsNodeInteraction
) {

    runBlocking(Dispatchers.IO) {
        var timeout = timeoutInSec
        var result : SemanticsNodeInteraction? = null
        while (timeout > 0 && result == null) {
            try {
                result = semanticsNodeInteraction()
            } catch(e: AssertionError) {
                delay(1000)
            }
            timeout -= 1
        }

        assert(result != null) {
            messageOnFail ?: "Condition could not be respected after $timeoutInSec seconds"
        }
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