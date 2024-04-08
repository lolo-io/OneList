package com.lolo.io.onelist.feature.lists.components.dialogs

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.tooling.preview.Preview
import com.lolo.io.onelist.core.design.space
import com.lolo.io.onelist.core.model.Item
import com.lolo.io.onelist.core.model.ItemList
import com.lolo.io.onelist.core.ui.composables.ComposePreview
import com.lolo.io.onelist.feature.lists.components.core.OneListTextField
import com.lolo.io.onelist.feature.lists.components.dialogs.components.DialogButtons
import com.lolo.io.onelist.feature.lists.components.dialogs.components.DialogScope
import com.lolo.io.onelist.feature.lists.components.dialogs.components.ScopedComposable
import com.lolo.io.onelist.feature.lists.components.dialogs.components.rememberDialogScope

@Composable
fun DialogScope.EditItemDialog(
    item: Item,
    onSubmit: (item: Item) -> Unit,
) {

    var itemTitle by rememberSaveable {
        mutableStateOf(item.title)
    }

    var itemComment by rememberSaveable {
        mutableStateOf(item.comment)
    }

    val textField = remember { FocusRequester() }

    LaunchedEffect(true) {
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
                value = itemTitle,
                placeholder = "Item name",
                onValueChange = { itemTitle = it },
                singleLine = true,
                showBorder = false,
                onKeyboardDoneInput = {
                    if (itemTitle.isNotEmpty()) {
                        onSubmit(item.copy(title = itemTitle, comment = itemComment))
                    }
                },
            )
        }
        HorizontalDivider()


        Box(
            modifier = Modifier.padding(
                vertical = MaterialTheme.space.SmallUpper
            )
        ) {

            OneListTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = MaterialTheme.space.Tiny),
                value = itemComment,
                showBorder = false,
                placeholder = "Item Comment",
                onValueChange = {
                    itemComment = it
                },
                leadingIcon = {
                    Icon(
                        modifier = Modifier.rotate(90f),
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = null
                    )
                },
            )
        }


        HorizontalDivider()

        DialogButtons(onPositiveClicked = {
            if (itemTitle.isNotEmpty()) {
                onSubmit(item.copy(title = itemTitle, comment = itemComment))
            }
        }, onNegativeClicked = {
            dismiss()
        })
    }

}


@Preview
@Composable
private fun Preview_EditItemDialog() = ComposePreview {
    ScopedComposable(
        rememberDialogScope { showPreviewDialog("Dismiss") }) {
        EditItemDialog(Item("Test"), onSubmit = {
            showPreviewDialog(it.title)
        })
    }
}

