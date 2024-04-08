package com.lolo.io.onelist.feature.lists.components.dialogs.components

import android.view.SoundEffectConstants
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.tooling.preview.Preview
import com.lolo.io.onelist.core.design.space

@Composable
fun ColumnScope.DialogButtons(
    onPositiveClicked: () -> Unit,
    onNegativeClicked: () -> Unit
) {

    val view = LocalView.current

    Row(
        modifier = Modifier
            .padding(
                horizontal = MaterialTheme.space.Small,
                vertical = MaterialTheme.space.Tiny
            )
            .align(Alignment.End)
    ) {
        IconButton(onClick = {
            onNegativeClicked()
            view.playSoundEffect(SoundEffectConstants.CLICK)
        }) {
            Icon(imageVector = Icons.Default.Close, contentDescription = "Cancel")
        }
        IconButton(onClick = {
            onPositiveClicked()
            view.playSoundEffect(SoundEffectConstants.CLICK)

        }) {
            Icon(imageVector = Icons.Default.Check, contentDescription = "Save")
        }
    }
}

@Preview
@Composable
private fun Preview_DialogButtons() {
    Column {
        DialogButtons(
            onPositiveClicked = {},
            onNegativeClicked = {}
        )
    }
}
