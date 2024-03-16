package com.lolo.io.onelist.feature.lists.components.add_item_input

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.lolo.io.onelist.core.design.space
import com.lolo.io.onelist.core.ui.composables.ComposePreview
import com.lolo.io.onelist.feature.lists.components.core.OneListTextField
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
internal fun AddItemInput(
    value: String,
    onValueChange: (String) -> Unit,
    commentValue: String = "",
    onCommentValueChange: (String) -> Unit = {},
    onSubmit: () -> Unit = {},
    modifier: Modifier = Modifier,

    ) {
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
            modifier = Modifier.fillMaxWidth(),
            value = value,
            placeholder = "Add",
            onValueChange = onValueChange,
            singleLine = true,
            leadingIcon = {
                Icon(imageVector = Icons.Default.Add, contentDescription = null)
            },
            trailingIcon = {
                if (value.isNotEmpty()) {


                    if (animatedSubmitAlpha > 0) {
                        IconButton(
                            modifier = Modifier.alpha(animatedSubmitAlpha),
                            onClick = onSubmit
                        ) {
                            Icon(imageVector = Icons.Default.Check, contentDescription = null)
                        }
                    }
                }
            },
            focusedTrailingIconColor = MaterialTheme.colorScheme.primary
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
                    onValueChange = onCommentValueChange,
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.Add, contentDescription = null)
                    },
                    trailingIcon = {
                        if (animatedClearCommentAlpha > 0f) {
                            IconButton(
                                modifier = Modifier.alpha(animatedClearCommentAlpha),
                                onClick = {
                                    onCommentValueChange("")
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
            mutableStateOf(180f)
        }

        TextButton(
            modifier = Modifier
                .size(36.dp)
                .padding(MaterialTheme.space.Tiny),
            onClick = {
                arrowRotation += 180f
                if (arrowRotation == 340f) arrowRotation = 180f
                showCommentInput = !showCommentInput
            },
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
                imageVector = Icons.Default.KeyboardArrowUp, contentDescription = "Add Comment"
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
        modifier = Modifier.fillMaxWidth(),
        value = text,
        onValueChange = { text = it },
        commentValue = comment,
        onCommentValueChange = { comment = it },
        onSubmit = { showPreviewDialog("Submit") }
    )
}