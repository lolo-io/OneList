package com.lolo.io.onelist.feature.lists.components.dialogs.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.lolo.io.onelist.core.design.space


@Composable
fun DialogContainer(
    shown: Boolean,
    dismiss: () -> Unit,
    content: @Composable DialogScope.() -> Unit
) {

    val dialogScope = rememberDialogScope(dismiss)

    if (shown) {
        ScopedComposable(dialogScope) {
            Dialog(
                onDismissRequest = { dialogScope.dismiss() },
                properties = DialogProperties(usePlatformDefaultWidth = false)
            ) {
                Box(
                    modifier = Modifier
                        .padding(horizontal = MaterialTheme.space.Normal)
                        .fillMaxWidth()
                        .background(
                            shape = MaterialTheme.shapes.medium,
                            color = MaterialTheme.colorScheme.surface
                        )
                        .border(
                            width = 1.dp,
                            MaterialTheme.colorScheme.secondary,
                            shape = MaterialTheme.shapes.medium,
                        )
                ) {
                    content()
                }
            }
        }
    }
}