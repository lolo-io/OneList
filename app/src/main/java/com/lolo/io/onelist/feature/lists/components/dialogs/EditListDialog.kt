package com.lolo.io.onelist.feature.lists.components.dialogs

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.tooling.preview.Preview
import com.lolo.io.onelist.core.design.space
import com.lolo.io.onelist.core.model.ItemList
import com.lolo.io.onelist.core.ui.composables.ComposePreview
import com.lolo.io.onelist.feature.lists.components.core.OneListTextField
import com.lolo.io.onelist.feature.lists.components.dialogs.components.DialogButtons
import com.lolo.io.onelist.feature.lists.components.dialogs.components.DialogScope
import com.lolo.io.onelist.feature.lists.components.dialogs.components.ScopedComposable
import com.lolo.io.onelist.feature.lists.components.dialogs.components.rememberDialogScope

@Composable
fun DialogScope.EditListDialog(
    list: ItemList? = null,
    onSubmit: (listName: String) -> Unit
) {

    var value by rememberSaveable {
        mutableStateOf(
            list?.title ?: ""
        )
    }

    val textField = FocusRequester()

    LaunchedEffect(textField) {
        textField.requestFocus()
    }

    Column {
        Box(
            modifier = Modifier.padding(
                horizontal = MaterialTheme.space.Normal,
                vertical = MaterialTheme.space.SmallUpper
            )
        ) {
            OneListTextField(
                modifier = Modifier
                    .focusRequester(textField)
                    .fillMaxWidth(),
                value = value,
                placeholder = "List name",
                onValueChange = { value = it },
                singleLine = true,
                showBorder = false,
                onKeyboardDoneInput = {
                    if (value.isNotEmpty()) {
                        onSubmit(value)
                    }
                },
            )
        }
        HorizontalDivider()

        DialogButtons(onPositiveClicked = {
            if (value.isNotEmpty()) {
                onSubmit(value)
            }
        }, onNegativeClicked = {
            dismiss()
        })
    }

}

@Composable
fun DialogScope.CreateListDialog(
    onSubmit: (listName: String) -> Unit
) {
    EditListDialog(onSubmit = onSubmit)
}


@Preview
@Composable
private fun Preview_EditListDialog() = ComposePreview {
    ScopedComposable(
        rememberDialogScope { showPreviewDialog("Dismiss") }) {
        EditListDialog {
            showPreviewDialog(it)
        }
    }
}

@Preview
@Composable
private fun Preview_CreateListDialog() = ComposePreview {
    ScopedComposable(
        rememberDialogScope { showPreviewDialog("Dismiss") }) {
        CreateListDialog {
            showPreviewDialog(it)
        }
    }
}