package com.lolo.io.onelist.feature.lists.components.dialogs

import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.tooling.preview.Preview
import com.lolo.io.onelist.core.data.utils.TestTags
import com.lolo.io.onelist.core.designsystem.colors.appColors
import com.lolo.io.onelist.core.designsystem.space
import com.lolo.io.onelist.core.model.preview
import com.lolo.io.onelist.core.ui.composables.ComposePreview
import com.lolo.io.onelist.feature.lists.R
import com.lolo.io.onelist.feature.lists.components.dialogs.components.DialogButtons
import com.lolo.io.onelist.feature.lists.components.dialogs.components.DialogScope
import com.lolo.io.onelist.feature.lists.components.dialogs.components.ScopedComposable
import com.lolo.io.onelist.feature.lists.components.dialogs.components.rememberDialogScope

@Composable
internal fun DialogScope.DeleteListDialog(
    list: com.lolo.io.onelist.core.model.ItemList,
    onDeleteList: (deleteFile: Boolean) -> Unit = {},
    onJustClearList: () -> Unit = {}
) {

    var deleteFile by remember { mutableStateOf(list.uri != null) }

    Column(modifier = Modifier
        .fillMaxWidth()
        .testTag(TestTags.DeleteListDialog)) {
        Row(
            modifier = Modifier
                .padding(
                    horizontal = MaterialTheme.space.Normal,
                    vertical = MaterialTheme.space.SmallUpper
                )
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(MaterialTheme.space.SmallUpper)
        ) {
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = null,
                tint = MaterialTheme.appColors.dialogDeleteWarning
            )

            Text(
                modifier = Modifier.fillMaxWidth(),
                text = list.title,
                style = MaterialTheme.typography.titleMedium
            )
        }

        HorizontalDivider()

        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = MaterialTheme.space.Normal,
                    vertical = MaterialTheme.space.SmallUpper
                ),
            text = stringResource(id = R.string.delete_list_forever_warning),
            style = MaterialTheme.typography.bodyLarge
        )

        if(list.uri != null) {
            Row(
                modifier = Modifier
                    .clip(MaterialTheme.shapes.large)
                    .clickable(
                        indication = rememberRipple(color = MaterialTheme.appColors.dialogDeleteCheckBoxRipple),
                        interactionSource = remember { MutableInteractionSource() },
                        onClick = {
                            deleteFile = !deleteFile
                        })
                    .align(Alignment.End)
                    .padding(
                        horizontal = MaterialTheme.space.Normal,
                        vertical = MaterialTheme.space.SmallUpper
                    ),
                horizontalArrangement = Arrangement.spacedBy(MaterialTheme.space.Small),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(id = R.string.also_delete_file),
                    style = MaterialTheme.typography.labelLarge,
                    fontStyle = FontStyle.Italic,
                    color = MaterialTheme.appColors.dialogDeleteDeleteFile
                )
                Checkbox(checked = deleteFile, onCheckedChange = null)
            }
        }

        HorizontalDivider()

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            TextButton(
                modifier = Modifier.testTag(TestTags.JustClearListButton),
                onClick = onJustClearList,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = MaterialTheme.appColors.dialogDeleteJutsClearList
                )
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_clear_all_24dp),
                    contentDescription = "Clear List"
                )

                Text(
                    modifier = Modifier
                        .padding(
                            horizontal = MaterialTheme.space.Tiny,
                            vertical = MaterialTheme.space.SmallUpper
                        ),
                    text = stringResource(id = R.string.clear_list),
                    style = MaterialTheme.typography.labelMedium
                )


            }

            DialogButtons(onPositiveClicked = {
                onDeleteList(deleteFile)
            }, onNegativeClicked = {
                dismiss()
            })
        }


    }
}

@Preview
@Composable
private fun Preview_DeleteListDialog() = ComposePreview {
    Surface {
        ScopedComposable(
            rememberDialogScope { showPreviewDialog("Dismiss") }) {
            DeleteListDialog(
                com.lolo.io.onelist.core.model.ItemList.preview.apply { uri = Uri.EMPTY }
            )
        }
    }
}