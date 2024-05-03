package com.lolo.io.onelist.feature.lists.components.dialogs.components

import android.view.SoundEffectConstants
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import com.lolo.io.onelist.core.data.utils.TestTags
import com.lolo.io.onelist.core.designsystem.colors.appColors
import com.lolo.io.onelist.core.designsystem.space
import com.lolo.io.onelist.core.designsystem.preview.ThemedPreview

@Composable
fun DialogButtons(
    onPositiveClicked: () -> Unit,
    onNegativeClicked: () -> Unit,
) {

    val view = LocalView.current

    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.End) {

        Row(
            modifier = Modifier
                .padding(
                    horizontal = MaterialTheme.space.Small,
                    vertical = MaterialTheme.space.Tiny
                )
                .align(Alignment.End)
        ) {
            IconButton(
                modifier = Modifier.testTag(TestTags.CommonDialogNegativeButton),
                onClick = {
                onNegativeClicked()
                view.playSoundEffect(SoundEffectConstants.CLICK)
            }) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Cancel",
                    tint = MaterialTheme.appColors.dialogButtonCancel
                )
            }
            IconButton( modifier = Modifier.testTag(TestTags.CommonDialogPositiveButton),
                onClick = {
                onPositiveClicked()
                view.playSoundEffect(SoundEffectConstants.CLICK)

            }) {
                Icon(
                    imageVector = Icons.Default.Check, contentDescription = "Save",
                    tint = MaterialTheme.appColors.dialogButtonPrimary
                )
            }
        }
    }
}

@Preview
@Composable
private fun Preview_DialogButtons() = ThemedPreview {
    Column {
        DialogButtons(
            onPositiveClicked = {},
            onNegativeClicked = {}
        )
    }
}
