package com.lolo.io.onelist.feature.lists.components.header

import android.view.SoundEffectConstants
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.lolo.io.onelist.R
import com.lolo.io.onelist.core.design.app
import com.lolo.io.onelist.core.design.space
import com.lolo.io.onelist.core.ui.composables.ComposePreview


data class OneListHeaderActions(
    val onClickCreateList: () -> Unit = {},
    val onClickShareList: () -> Unit = {},
    val onClickDeleteList: () -> Unit = {},
    val onClickEditList: () -> Unit = {},
    val onClickSettings: () -> Unit = {}
)

@Composable
internal fun OneListHeader(
    actions: OneListHeaderActions = OneListHeaderActions(),
    showSelectedListControls: Boolean = false,
) {
    val view = LocalView.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = MaterialTheme.space.Normal)
            .padding(vertical = MaterialTheme.space.Tiny),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(MaterialTheme.space.Small)
        ) {
            Image(painter = painterResource(id = R.drawable.logo_inv), contentDescription = null)
            Text(
                text = stringResource(id = R.string.app_name),
                style = MaterialTheme.typography.bodyLarge
            )

            IconButton(onClick = {
                actions.onClickSettings()
                view.playSoundEffect(SoundEffectConstants.CLICK)
            }) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Settings",
                    tint = MaterialTheme.colorScheme.app.settingsIcon
                )
            }
        }


        val targetRotationDefaultsControls = animateFloatAsState(
            targetValue = if (showSelectedListControls) 0f else 1f,
            animationSpec = tween(
                durationMillis = 500,
                easing = FastOutSlowInEasing
            ),
            label = "targetRotationDefaultsControls"
        )

        val targetRotationListsControls = animateFloatAsState(
            targetValue = if (showSelectedListControls) 1f else 0f,
            animationSpec = tween(
                durationMillis = 500,
                easing = FastOutSlowInEasing
            ),
            label = "targetRotationListsControls"
        )


        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = {
                if (showSelectedListControls) {
                    actions.onClickEditList()
                } else {
                    actions.onClickShareList()
                }
                view.playSoundEffect(SoundEffectConstants.CLICK)
            }) {
                Icon(
                    modifier = Modifier.alpha(targetRotationDefaultsControls.value),
                    imageVector = Icons.Default.Share, contentDescription = "Share List",
                    tint = MaterialTheme.colorScheme.app.shareListIcon

                )

                Icon(
                    modifier = Modifier.alpha(targetRotationListsControls.value),
                    imageVector = Icons.Default.Edit, contentDescription = "Edit List",
                    tint = MaterialTheme.colorScheme.app.editListIcon
                )

            }

            IconButton(onClick = {
                if (showSelectedListControls) {
                    actions.onClickDeleteList()
                } else {
                    actions.onClickCreateList()
                }
                view.playSoundEffect(SoundEffectConstants.CLICK)
            }) {
                Icon(
                    modifier = Modifier
                        .alpha(targetRotationDefaultsControls.value)
                        .scale(1.2f),
                    imageVector = Icons.Default.Add, contentDescription = "Create List",
                    tint = MaterialTheme.colorScheme.app.addListIcon
                )

                Icon(
                    modifier = Modifier.alpha(targetRotationListsControls.value),
                    imageVector = Icons.Default.Delete, contentDescription = "Delete List",
                    tint = MaterialTheme.colorScheme.app.deleteListIcon
                )
            }
        }
    }
}

@Preview
@Composable
private fun Preview_OneListHeader() = ComposePreview {
    OneListHeader()
}