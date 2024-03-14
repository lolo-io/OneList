package com.lolo.io.onelist.feature.lists.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.lolo.io.onelist.R
import com.lolo.io.onelist.core.design.app
import com.lolo.io.onelist.core.design.dimen
import com.lolo.io.onelist.core.design.space
import com.lolo.io.onelist.core.model.Item
import com.lolo.io.onelist.core.model.preview
import com.lolo.io.onelist.core.ui.composables.ComposePreview

@Composable
fun ItemRow(
    item: Item,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
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
                        modifier = Modifier.padding(
                            start = MaterialTheme.space.SmallUpper,
                            end = MaterialTheme.space.Tiny
                        )
                    ) {
                        Icon(
                            modifier = Modifier.size(MaterialTheme.space.Small),
                            painter = painterResource(R.drawable.ic_circle),
                            contentDescription = null,
                            tint = when(item.done) {
                                false -> MaterialTheme.colorScheme.app.itemBullet
                                true -> MaterialTheme.colorScheme.app.itemDone
                            }
                        )
                    }

                    Text(
                        item.title,
                        style = when(item.done) {
                            false -> MaterialTheme.typography.app.itemTitle
                            true -> MaterialTheme.typography.app.itemTitleDone
                        },
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = MaterialTheme.space.Small)
                            .heightIn(MaterialTheme.dimen.listItemMinHeight)
                            .wrapContentHeight(align = Alignment.CenterVertically),
                    )


                    Box(
                        modifier = Modifier.padding(
                            horizontal = MaterialTheme.space.Tiny
                        )
                    ) {
                        Icon(
                            painter = when (item.commentDisplayed) {
                                true -> painterResource(R.drawable.ic_expand_more_add)
                                false -> painterResource(R.drawable.ic_expand_more_add)
                            },
                            contentDescription = "Show/Hide Comment",
                        )

                    }
                }
            }
        }
        if (item.commentDisplayed) {
            Text(
                item.comment,
                style = when(item.done) {
                    false -> MaterialTheme.typography.app.itemComment
                    true -> MaterialTheme.typography.app.itemCommentDone
                },
                modifier = Modifier
                    .padding(start = MaterialTheme.space.xBig, bottom = MaterialTheme.space.Normal)
                    .wrapContentHeight(align = Alignment.CenterVertically),
            )
        }
    }
}

@Preview
@Composable
private fun Preview_ItemRow() = ComposePreview {
    ItemRow(Item.preview)
}

@Preview
@Composable
private fun Preview_ItemRowWithComment() = ComposePreview {
    ItemRow(Item.preview.copy(commentDisplayed = true))
}

@Preview
@Composable
private fun Preview_ItemRowDone() = ComposePreview {
    ItemRow(
        Item.preview.copy(
            done = true
        )
    )
}

@Preview
@Composable
private fun Preview_ItemRowDoneWithComment() = ComposePreview {
    ItemRow(
        Item.preview.copy(
            done = true,
            commentDisplayed = true
        )
    )
}