package com.lolo.io.onelist.feature.lists.components.add_item_input

import android.view.SoundEffectConstants
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.lolo.io.onelist.core.design.colors.appColors
import com.lolo.io.onelist.core.design.space
import com.lolo.io.onelist.core.ui.composables.ComposePreview
import com.lolo.io.onelist.feature.lists.components.core.OneListTextField

@Composable
internal fun AddItemInput(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    commentValue: String = "",
    onCommentValueChange: (String) -> Unit = {},
    onSubmit: () -> Unit = {},
) {

    val keyboardController = LocalSoftwareKeyboardController.current
    val view = LocalView.current

    val focusRequester = remember {
        FocusRequester()
    }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {

        val iconsAnimationDuration = 300

        var showCommentInput by remember {
            mutableStateOf(false)
        }


        val animatedSubmitAlpha by animateFloatAsState(
            targetValue = if (value.isEmpty()) 0f else 1f,
            animationSpec = tween(
                durationMillis = iconsAnimationDuration,
                easing = FastOutSlowInEasing
            ), label = ""
        )


        OneListTextField(
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(focusRequester),
            value = value,
            placeholder = "Add",
            onValueChange = onValueChange,
            singleLine = true,
            leadingIcon = {
                Icon(imageVector = Icons.Default.Add, contentDescription = null)
            },
            onKeyboardDoneInput = {
                if (value.isNotEmpty()) {
                    onSubmit()
                } else {
                    keyboardController?.hide()
                }
                view.playSoundEffect(SoundEffectConstants.CLICK)
            },
            trailingIcon = {
                if (value.isNotEmpty()) {
                    if (animatedSubmitAlpha > 0) {
                        IconButton(
                            modifier = Modifier.alpha(animatedSubmitAlpha),
                            onClick = {
                                onSubmit()
                                view.playSoundEffect(SoundEffectConstants.CLICK)
                                focusRequester.requestFocus()
                            },
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = MaterialTheme.appColors.addItemCheck
                            )
                        }
                    }
                }
            },
        )
        AnimatedVisibility(
            visible = showCommentInput
        ) {
            Box(Modifier.padding(start = MaterialTheme.space.Normal)) {

                val animatedClearCommentAlpha by animateFloatAsState(
                    targetValue = if (commentValue.isEmpty()) 0f else 1f,
                    animationSpec = tween(
                        durationMillis = iconsAnimationDuration,
                        easing = FastOutSlowInEasing
                    ), label = ""
                )


                OneListTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = MaterialTheme.space.Tiny),
                    value = commentValue,
                    placeholder = "Comment",
                    onValueChange = {
                        onCommentValueChange(it)
                    },
                    leadingIcon = {
                        Icon(
                            modifier = Modifier.rotate(90f),
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = null
                        )
                    },
                    trailingIcon = {
                        if (animatedClearCommentAlpha > 0f) {
                            IconButton(
                                modifier = Modifier.alpha(animatedClearCommentAlpha),
                                onClick = {
                                    onCommentValueChange("")
                                    view.playSoundEffect(SoundEffectConstants.CLICK)
                                }) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = "Clear Comment"
                                )
                            }
                        }
                    }
                )
            }
        }


        var arrowRotation by remember {
            mutableFloatStateOf(180f)
        }

        val showCommentArrow by remember(value) {
            derivedStateOf { value.isNotEmpty() || showCommentInput || commentValue.isNotEmpty() }
        }

        val animatedArrowVisibility by animateFloatAsState(
            targetValue = if(showCommentArrow) 1f else 0f,
            animationSpec = tween(
                durationMillis = iconsAnimationDuration,
                easing = FastOutSlowInEasing
            ), label = ""
        )

        TextButton(
            modifier = Modifier
                .size(36.dp)
                .padding(MaterialTheme.space.Tiny)
                .alpha(animatedArrowVisibility),
            onClick = {
                arrowRotation += 180f
                if (arrowRotation == 340f) arrowRotation = 180f
                showCommentInput = !showCommentInput
                view.playSoundEffect(SoundEffectConstants.CLICK)
            },
            enabled = showCommentArrow,
            contentPadding = PaddingValues(0.dp)
        ) {

            val animatedArrowRotation by animateFloatAsState(
                targetValue = arrowRotation,
                animationSpec = tween(
                    durationMillis = iconsAnimationDuration,
                    easing = FastOutSlowInEasing
                ), label = ""
            )
            Image(
                modifier = Modifier
                    .size(width = 24.dp, height = (24 * 0.6).dp)
                    .rotate(animatedArrowRotation),
                contentScale = ContentScale.FillBounds,
                imageVector = Icons.Default.KeyboardArrowUp,
                contentDescription = "Add Comment",
                colorFilter = ColorFilter.tint(MaterialTheme.appColors.addItemCommentArrow)
            )
        }

    }

}

@Preview
@Composable
private fun Preview_AddItemInput() = ComposePreview {
    var text by remember { mutableStateOf("Preview text") }
    var comment by remember { mutableStateOf("") }
    AddItemInput(
        value = text,
        onValueChange = { text = it },
        commentValue = comment,
        onCommentValueChange = { comment = it },
        onSubmit = { showPreviewDialog("Submit") },
        modifier = Modifier.fillMaxWidth(),
    )
}