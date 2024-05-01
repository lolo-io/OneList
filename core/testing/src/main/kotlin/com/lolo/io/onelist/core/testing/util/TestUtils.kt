package com.lolo.io.onelist.core.testing.util

import kotlinx.coroutines.delay

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