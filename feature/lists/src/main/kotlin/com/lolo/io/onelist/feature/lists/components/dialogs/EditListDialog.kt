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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.lolo.io.onelist.core.data.utils.TestTags
import com.lolo.io.onelist.core.designsystem.space
import com.lolo.io.onelist.core.model.ItemList
import com.lolo.io.onelist.core.designsystem.preview.ThemedPreview
import com.lolo.io.onelist.feature.lists.R
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

    Column(
        modifier = Modifier.testTag(TestTags.EditListDialog)
    ) {
        Box(
            modifier = Modifier.padding(
                horizontal = MaterialTheme.space.Normal,
                vertical = MaterialTheme.space.SmallUpper
            )
        ) {
            OneListTextField(
                modifier = Modifier
                    .focusRequester(textField)
                    .fillMaxWidth()
                    .testTag(TestTags.EditListDialogInput),
                value = value,
                placeholder = stringResource(id = R.string.list_title),
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
private fun Preview_EditListDialog() = ThemedPreview {
    ScopedComposable(
        rememberDialogScope { showPreviewDialog("Dismiss") }) {
        EditListDialog {
            showPreviewDialog(it)
        }
    }
}

@Preview
@Composable
private fun Preview_CreateListDialog() = ThemedPreview {
    ScopedComposable(
        rememberDialogScope { showPreviewDialog("Dismiss") }) {
        CreateListDialog {
            showPreviewDialog(it)
        }
    }
}