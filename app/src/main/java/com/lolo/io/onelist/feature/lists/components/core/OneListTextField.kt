package com.lolo.io.onelist.feature.lists.components.core

import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.lolo.io.onelist.core.design.colors.appColors
import com.lolo.io.onelist.core.design.space
import com.lolo.io.onelist.core.ui.util.ifThen

/*
    Have to use BasicTextField with DecorationBox just because default compose TextField
    definition doesn't allow to set contentPadding.
 */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun OneListTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    singleLine: Boolean = false,
    showBorder: Boolean = true,
    onKeyboardDoneInput: () -> Unit = {},
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null
) {

    val borderShape = MaterialTheme.shapes.medium
    val interactionSource = remember { MutableInteractionSource() }
    var lineCount by remember { mutableIntStateOf(0) }
    var singleLineFix by remember { mutableStateOf(false) }

    var textFieldValueState by remember {
        mutableStateOf(
            TextFieldValue(value, selection = TextRange(Int.MAX_VALUE))
        )
    }

    LaunchedEffect(value) {
        if(value.isEmpty()) {
            textFieldValueState = TextFieldValue(value, selection = TextRange.Zero)
        }
    }

    BasicTextField(
        modifier = modifier
            .ifThen(showBorder) {
                border(
                    width = 1.dp,
                    color = MaterialTheme.appColors.textFieldBorder,
                    borderShape
                )
            }
            .heightIn(36.dp, if (lineCount <= 1) 36.dp else Dp.Unspecified),
        value = textFieldValueState,
        onValueChange = {
            textFieldValueState = it
            onValueChange(it.text)
        },
        textStyle = MaterialTheme.typography.bodyLarge.copy(
            color = MaterialTheme.appColors.textFieldText
        ),
        interactionSource = interactionSource,
        singleLine = singleLine,
        maxLines = if (singleLineFix) 1 else Int.MAX_VALUE,
        onTextLayout = {
            lineCount = it.lineCount
            singleLineFix = if (!singleLine) {
                it.lineCount <= 1
            } else singleLine
        },

        keyboardActions = KeyboardActions(
            onDone = { onKeyboardDoneInput() }
        ),
        cursorBrush = SolidColor(MaterialTheme.appColors.textFieldCursor),
    ) { innerTextField ->
        TextFieldDefaults.DecorationBox(
            value = value,
            contentPadding = PaddingValues(MaterialTheme.space.Tiny),
            innerTextField = innerTextField,
            enabled = true,
            singleLine = singleLineFix,
            visualTransformation = VisualTransformation.None,
            interactionSource = interactionSource,
            leadingIcon = leadingIcon,
            trailingIcon = trailingIcon,
            shape = borderShape,
            colors = TextFieldDefaults.colors(
                focusedContainerColor = if (showBorder) MaterialTheme.appColors.textFieldColors.focusedContainerColor
                else
                    MaterialTheme.appColors.textFieldBackgroundNoBorder,
                unfocusedContainerColor = if (showBorder) MaterialTheme.appColors.textFieldColors.unfocusedContainerColor
                else
                    MaterialTheme.appColors.textFieldBackgroundNoBorder,
                cursorColor = MaterialTheme.appColors.textFieldColors.cursorColor,
                focusedLeadingIconColor = MaterialTheme.appColors.textFieldColors.focusedLeadingIconColor,
                unfocusedLeadingIconColor = MaterialTheme.appColors.textFieldColors.unfocusedLeadingIconColor,
                focusedTrailingIconColor = MaterialTheme.appColors.textFieldColors.focusedTrailingIconColor,
                unfocusedTrailingIconColor = MaterialTheme.appColors.textFieldColors.unfocusedTrailingIconColor,
                focusedIndicatorColor = MaterialTheme.appColors.textFieldColors.focusedIndicatorColor,
                unfocusedIndicatorColor = MaterialTheme.appColors.textFieldColors.focusedIndicatorColor,
                selectionColors = MaterialTheme.appColors.textFieldColors.textSelectionColors
            ),
            placeholder = {
                Text(
                    text = placeholder,
                    color = MaterialTheme.appColors.textFieldPlaceholder
                )
            }
        )
    }
}