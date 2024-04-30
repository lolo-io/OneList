package com.lolo.io.onelist.feature.lists.components.items_lists

import android.content.res.Configuration
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lolo.io.onelist.core.data.utils.TestTags
import com.lolo.io.onelist.core.designsystem.app
import com.lolo.io.onelist.core.designsystem.colors.appColors
import com.lolo.io.onelist.core.designsystem.dimen
import com.lolo.io.onelist.core.designsystem.space
import com.lolo.io.onelist.core.model.Item
import com.lolo.io.onelist.core.model.preview
import com.lolo.io.onelist.core.ui.composables.ComposePreview
import com.lolo.io.onelist.feature.lists.R

@Composable
fun ItemUI(
    item: Item,
    onClick: () -> Unit = {},
    onClickDisplayComment: () -> Unit = {},
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                onClick = { onClick() },
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            )
            .testTag(TestTags.ItemUiSurface),
        color = Color.Transparent,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = MaterialTheme.space.SmallUpper)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Box(
                            modifier = Modifier
                                .padding(
                                    start = MaterialTheme.space.SmallUpper,
                                    end = MaterialTheme.space.Tiny
                                )
                                .alignBy { it.measuredHeight }
                        ) {
                            Icons.Default.CheckCircle
                            Icon(
                                modifier = Modifier.size(MaterialTheme.space.Small),
                                painter = painterResource(R.drawable.ic_bullet_24dp),
                                contentDescription = null,
                                tint = when (item.done) {
                                    false -> MaterialTheme.appColors.itemBullet
                                    true -> MaterialTheme.appColors.itemDone
                                }
                            )
                        }

                        Text(
                            item.title,
                            style = when (item.done) {
                                false -> MaterialTheme.typography.app.itemTitle
                                true -> MaterialTheme.typography.app.itemTitleDone
                            },
                            color = when (item.done) {
                                false -> Color.Unspecified // Use Default
                                true -> MaterialTheme.appColors.itemDone
                            },
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = MaterialTheme.space.Small)
                                .padding(vertical = MaterialTheme.space.Tiny)
                                .heightIn(MaterialTheme.dimen.listItemMinHeight)
                                .alignByBaseline()
                                .padding(vertical = MaterialTheme.space.Tiny)
                                .wrapContentHeight(align = Alignment.CenterVertically)
                                .testTag(TestTags.ItemUiTitle),
                        )


                        if (item.comment.isNotEmpty()) {
                            Box(
                                modifier = Modifier
                                    .align(Alignment.Top)
                                    .padding(top = MaterialTheme.space.Tiny)
                                    .testTag(TestTags.itemCommentArrowItemTitle(item.title))
                            ) {
                                val animatedArrowRotation by animateFloatAsState(
                                    targetValue = if (item.commentDisplayed) 0f else 180f,
                                    animationSpec = tween(
                                        durationMillis = 300,
                                        easing = FastOutSlowInEasing
                                    ), label = ""
                                )
                                IconButton(
                                    modifier = Modifier.testTag(TestTags.ItemUiArrowComment),
                                    onClick = onClickDisplayComment
                                ) {
                                    Image(
                                        modifier = Modifier
                                            .size(width = 24.dp, height = (24 * 0.6).dp)
                                            .rotate(animatedArrowRotation),
                                        contentScale = ContentScale.FillBounds,
                                        colorFilter = ColorFilter.tint(MaterialTheme.appColors.itemArrow),
                                        imageVector = Icons.Default.KeyboardArrowUp,
                                        contentDescription = "Add Comment"
                                    )
                                }
                            }
                        }
                    }
                }
            }
            if (item.commentDisplayed) {
                Text(
                    item.comment,
                    style = when (item.done) {
                        false -> MaterialTheme.typography.app.itemComment
                        true -> MaterialTheme.typography.app.itemCommentDone
                    },
                    lineHeight = 16.sp,
                    color = MaterialTheme.appColors.itemComment,
                    modifier = Modifier
                        .padding(
                            start = MaterialTheme.space.xBig,
                            bottom = MaterialTheme.space.Normal
                        )
                        .wrapContentHeight(align = Alignment.CenterVertically),
                )
            }
        }
    }
}

@Preview
@Composable
private fun Preview_ItemRow() = ComposePreview {

    var item by remember { mutableStateOf(com.lolo.io.onelist.core.model.Item.preview) }
    ItemUI(item,
        onClickDisplayComment = {
            item = item.copy(commentDisplayed = !item.commentDisplayed)
        }
    )
}

@Preview
@Composable
private fun Preview_ItemRowWithComment() = ComposePreview {
    ItemUI(com.lolo.io.onelist.core.model.Item.preview.copy(commentDisplayed = true))
}

@Preview
@Composable
private fun Preview_ItemRowDone() = ComposePreview {
    ItemUI(
        com.lolo.io.onelist.core.model.Item.preview.copy(
            done = true
        )
    )
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview
@Composable
private fun Preview_ItemRowDoneWithComment() = ComposePreview {
    ItemUI(
        com.lolo.io.onelist.core.model.Item.preview.copy(
            done = true,
            commentDisplayed = true
        )
    )
}

@Preview
@Composable
private fun Preview_ItemRowLong() = ComposePreview {
    ItemUI(com.lolo.io.onelist.core.model.Item.preview.copy(title = "Long Long Long Long Long Long Long Long Long Long Long Long Long Long Long Long Long Long Long Long "))

}
