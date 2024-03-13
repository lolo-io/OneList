package com.lolo.io.onelist.feature.lists.components

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.lolo.io.onelist.core.design.OneListTheme
import com.lolo.io.onelist.core.design.ext

enum class ListChipState {
    DEFAULT, SELECTED, DRAGGED, SHADOW
}

@Composable
fun ListChip(
    label: String,
    state: ListChipState = ListChipState.DEFAULT,
    onClick: () -> Unit = {}
) {
    val asSelected = state == ListChipState.SELECTED ||
            state == ListChipState.DRAGGED ||
            state == ListChipState.SHADOW

    FilterChip(
        modifier = Modifier
            .scale(if (state == ListChipState.DRAGGED) 1.1f else 1f)
            .padding(2.dp)
            .height(FilterChipDefaults.Height),
        selected = asSelected,
        onClick = onClick,
        label = { Text(label) },
        colors = FilterChipDefaults.filterChipColors(
            containerColor = Color.Transparent,
            labelColor = MaterialTheme.colorScheme.ext.listChipDefaultText,
            selectedContainerColor = when (state) {
                ListChipState.DRAGGED -> MaterialTheme.colorScheme.ext.listChipDraggingBackground
                else -> Color.Transparent
            },
            selectedLabelColor = when (state) {
                ListChipState.SELECTED -> MaterialTheme.colorScheme.primary
                ListChipState.DRAGGED -> MaterialTheme.colorScheme.primary
                ListChipState.SHADOW -> MaterialTheme.colorScheme.ext.listChipDraggingBackground
                else -> MaterialTheme.colorScheme.ext.listChipDraggingBackground
            },
        ),
        border = FilterChipDefaults.filterChipBorder(
            enabled = true,
            selected = asSelected,
            borderColor = MaterialTheme.colorScheme.ext.listChipDefaultBorder,
            borderWidth = 1.dp,
            selectedBorderWidth = when (state) {
                ListChipState.SHADOW -> 0.2.dp
                else -> 1.dp
            },
            selectedBorderColor = when (state) {
                ListChipState.DRAGGED -> MaterialTheme.colorScheme.ext.listChipDefaultBorder
                ListChipState.SELECTED -> MaterialTheme.colorScheme.primary
                else -> MaterialTheme.colorScheme.ext.listChipDraggingBackground
            },
        )
    )
}

@Preview
@Composable
private fun Preview_Default() = WithTheme {
    ListChip("List Chip")
}


@Preview
@Composable
private fun Preview_Selected() = WithTheme {
    ListChip("List Chip", state = ListChipState.SELECTED)
}

@Preview
@Composable
private fun Preview_Dragged() = WithTheme {
    ListChip("List Chip", state = ListChipState.DRAGGED)
}

@Preview
@Composable
private fun Preview_Shadow() = WithTheme {
    ListChip("List Chip", state = ListChipState.SHADOW)
}

@Composable
fun WithTheme(content: @Composable () -> Unit) {
    OneListTheme {
        content()
    }
}