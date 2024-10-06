package com.lolo.io.onelist.feature.lists.components.items_lists

import android.util.Log
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.material.icons.automirrored.sharp.ArrowForward
import androidx.compose.material.icons.automirrored.sharp.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.twotone.ArrowForward
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.lolo.io.onelist.core.data.utils.TestTags
import com.lolo.io.onelist.core.designsystem.Palette
import com.lolo.io.onelist.core.designsystem.colors.appColors
import com.lolo.io.onelist.core.designsystem.space
import com.lolo.io.onelist.core.model.Item
import com.lolo.io.onelist.core.designsystem.preview.ThemedPreview
import com.lolo.io.onelist.feature.lists.DELETE_ANIMATION_DURATION
import com.lolo.io.onelist.feature.lists.R
import com.lolo.io.onelist.feature.lists.components.core.SwipeState
import com.lolo.io.onelist.feature.lists.components.core.reorderable_swipeable_list.SwipeableRow
import com.lolo.io.onelist.feature.lists.components.core.reorderable_swipeable_list.SwipeableRowScope
import com.lolo.io.onelist.feature.lists.components.items_lists.composables.Custom
import com.lolo.io.onelist.feature.lists.components.items_lists.composables.CustomIcons
import com.lolo.io.onelist.feature.lists.components.items_lists.composables.SwipeRightArrow
import org.koin.core.logger.Logger

@Composable
internal fun SwipeableRowScope.SwipeableItem(
    item: Item,
    modifier: Modifier = Modifier,
    onSwipedToStart: () -> Unit = {},
    onSwipedToEnd: () -> Unit = {},
    onSwipedBackToCenter: () -> Unit = {},
    drawItem: @Composable (Item) -> Unit = {}
) {

    val deleteSwipePercentTarget = remember {
        mutableStateOf(1f)
    }


    SwipeableRow(
        modifier = modifier.testTag(TestTags.SwipeableItem),
        backgroundStartToEnd = {
            Box(
                Modifier
                    .background(MaterialTheme.appColors.swipeEditBackground)
                    .fillMaxSize()
                    .padding(start = MaterialTheme.space.Normal)
                    .testTag(TestTags.SwipeableItemEditBackground),
                contentAlignment = Alignment.CenterStart
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    tint = Palette.PURE_WHITE,
                    contentDescription = null
                )
            }
        },
        backgroundEndToStart = {

            Box(
                Modifier
                    .background(MaterialTheme.appColors.swipeDeleteProgressBackground)
                    .fillMaxSize()
                    .testTag(TestTags.SwipeableItemDeleteBackground),
                contentAlignment = Alignment.CenterEnd
            ) {


                LaunchedEffect(swipeState) {

                    if (swipeState == SwipeState.START) {
                        deleteSwipePercentTarget.value = 0f
                    } else {
                        deleteSwipePercentTarget.value = 1f
                    }
                }


                val deleteAnimation = animateFloatAsState(
                    targetValue = deleteSwipePercentTarget.value, animationSpec = tween(
                        durationMillis =
                        if (deleteSwipePercentTarget.value == 1f) 0
                        else DELETE_ANIMATION_DURATION.toInt(),
                        easing = LinearEasing
                    ),
                    label = "deleteSwipe"
                )

                Icon(
                    modifier = Modifier.padding(end = MaterialTheme.space.Normal),
                    imageVector = Icons.Default.Delete,
                    tint = Color.White,
                    contentDescription = null
                )

                Box(
                    Modifier
                        .background(MaterialTheme.appColors.swipeDeleteBackground)
                        .fillMaxHeight()
                        .align(Alignment.CenterStart)
                        .fillMaxWidth(deleteAnimation.value)
                )

                UndoTextIndication(MaterialTheme.appColors.swipeDeleteText)

                Icon(
                    modifier = Modifier.padding(end = MaterialTheme.space.Normal),
                    imageVector = Icons.Default.Delete,
                    tint = MaterialTheme.appColors.swipeDeleteText,
                    contentDescription = null
                )
            }
        },

        onSwipedToEnd = {
            setSwipeState(SwipeState.END)
            onSwipedToEnd()
        },
        onSwipedToStart = {
            setSwipeState(SwipeState.START)
            onSwipedToStart()
        },
        onSwipedBackToCenter = {
            setSwipeState(SwipeState.NONE)
        },
        onInitiateSwipeBackToCenter = {
            onSwipedBackToCenter()
        }
    ) {
        drawItem(item)
    }
}

@Composable
fun BoxScope.UndoTextIndication(color: Color) {
    Row(
        Modifier
            .align(Alignment.CenterStart)
            .padding(start = MaterialTheme.space.Big),
    ) {

        Text(
            text = stringResource(id = R.string.undo),
            color = color
        )

        Icon(
            imageVector = Icons.Custom.SwipeRightArrow,
            tint = color,
            contentDescription = null
        )

    }
}

@Preview
@Composable
private fun Preview_SwipeableItem() = ThemedPreview {
/*
    var swipeState by remember {
        mutableStateOf(SwipeState.NONE)
    }

    Column {
            SwipeableItem(
                Item.preview,
                setSwipeState = {
                    swipeState = it
                },
                drawItem = {
                    ItemUI(Item.preview,
                        onClickDisplayComment = {
                            showPreviewDialog("onClickDisplayComment")
                        }
                    )
                },
            )
            Button(
                modifier = Modifier.padding(MaterialTheme.space.Normal),
                onClick = {
                   swipeState = SwipeState.NONE
                }
            ) {
                Text(text = "Reset Swipe")
            }
        }

 */
}