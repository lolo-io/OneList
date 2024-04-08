package com.lolo.io.onelist.feature.lists.components.dialogs.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

interface DialogScope {
    fun dismiss()
}

@Composable
fun rememberDialogScope(dismiss: () -> Unit): DialogScope {
    return remember {
        object : DialogScope {
            override fun dismiss() {
                dismiss()
            }
        }
    }
}

@Composable
fun ScopedComposable(scope: DialogScope, content: @Composable DialogScope.() -> Unit) {
    content(scope)
}