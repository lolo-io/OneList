package com.lolo.io.onelist.feature.whatsnew

import android.view.SoundEffectConstants
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.lolo.io.onelist.core.common.R as commonR
import com.lolo.io.onelist.core.designsystem.colors.appColors
import com.lolo.io.onelist.core.designsystem.space
import com.lolo.io.onelist.core.ui.composables.ComposePreview

@Composable
internal fun WhatsNewScreen(
    data: WhatsNewData = currentReleaseWhatsNewData(LocalContext.current),
    onClickContinue: () -> Unit,
) {
    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(MaterialTheme.space.Big),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(MaterialTheme.space.xBig)
            ) {

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        modifier = Modifier.size(48.dp),
                        painter = painterResource(id = commonR.drawable.ic_launcher_foreground),
                        contentDescription = null,
                        tint = MaterialTheme.appColors.oneListTopLogo
                    )
                    Text(
                        text = stringResource(id = commonR.string.app_name),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }

                Text(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    text = data.title,
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.appColors.whatsNewTitle,
                )

                data.items.forEach { item ->
                    when (item) {
                        is WhatsNewItem.WhatsNewTitleItem -> {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(MaterialTheme.space.Small)
                            ) {
                                Box(modifier = Modifier.alignBy { it.measuredHeight / 2 }) {
                                    if (item.imageVector != null) {
                                        Icon(
                                            modifier = Modifier.size(MaterialTheme.space.Big),
                                            imageVector = item.imageVector,
                                            tint = MaterialTheme.appColors.whatsNewItemIcon,
                                            contentDescription = null,
                                        )
                                    } else if (item.iconRes != null) {
                                        Icon(
                                            modifier = Modifier.size(MaterialTheme.space.Big),
                                            painter = painterResource(id = item.iconRes),
                                            tint = MaterialTheme.appColors.whatsNewItemIcon,
                                            contentDescription = null
                                        )
                                    }
                                }


                                Column(
                                    modifier = Modifier
                                        .alignByBaseline()
                                ) {
                                    item.title?.let {
                                        Text(
                                            text = item.title,
                                            style = MaterialTheme.typography.titleMedium,
                                        )
                                    }

                                    item.description?.let {
                                        Text(
                                            modifier = Modifier.padding(top = MaterialTheme.space.Tiny),
                                            text = item.description,
                                            style = MaterialTheme.typography.bodyMedium,
                                        )
                                    }
                                }
                            }
                        }

                        is WhatsNewItem.WhatsNewTextItem -> {
                            item.description?.let {
                                Text(
                                    text = item.description,
                                    style = MaterialTheme.typography.bodyMedium,
                                )
                            }
                        }
                    }
                }
            }

            val view = LocalView.current
            Button(onClick = {
                view.playSoundEffect(SoundEffectConstants.CLICK)
                onClickContinue()
            }) {
                Text(
                    text = stringResource(id = R.string.continue_button),
                )
            }
        }
    }
}

@Preview
@Composable
private fun Preview_WhatsNewScreen() = ComposePreview {

    Surface(modifier = Modifier.fillMaxSize()) {
        WhatsNewScreen(
            onClickContinue = { showPreviewDialog("Continue") }
        )
    }
}