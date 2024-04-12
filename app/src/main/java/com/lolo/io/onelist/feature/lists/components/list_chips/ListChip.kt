package com.lolo.io.onelist.feature.lists.components.list_chips

import android.content.res.Configuration
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
import com.lolo.io.onelist.core.design.colors.appColors

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
            labelColor = MaterialTheme.appColors.listChipDefaultText,
            selectedContainerColor = when (state) {
                ListChipState.SELECTED, ListChipState.DRAGGED -> MaterialTheme.appColors.listChipSelectedContainer
                else -> Color.Transparent
            },
            selectedLabelColor = when (state) {
                ListChipState.SELECTED, ListChipState.DRAGGED -> MaterialTheme.appColors.listChipSelectedText
                ListChipState.SHADOW -> MaterialTheme.appColors.listChipShadowText
                else -> Color.Unspecified
            },
        ),
        border = FilterChipDefaults.filterChipBorder(
            enabled = true,
            selected = asSelected,
            borderColor = MaterialTheme.appColors.listChipDefaultBorder,
            borderWidth = 1.dp,
            selectedBorderWidth = when (state) {
                ListChipState.SHADOW -> 0.2.dp
                else -> 1.dp
            },
            selectedBorderColor = when (state) {
                ListChipState.SELECTED, ListChipState.DRAGGED -> MaterialTheme.appColors.listSelectedBorder

                ListChipState.SHADOW -> MaterialTheme.appColors.listChipShadowBorder
                else -> Color.Unspecified
            },
        )
    )
}

@Preview
@Composable
private fun Preview_Default() = OneListTheme {
    ListChip("List Chip")
}


@Preview
@Composable
private fun Preview_Selected() = OneListTheme {
    ListChip("List Chip", state = ListChipState.SELECTED)
}

@Preview
@Composable
private fun Preview_Dragged() = OneListTheme {
    ListChip("List Chip", state = ListChipState.DRAGGED)
}

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun Preview_Shadow() = OneListTheme {
    ListChip("List Chip", state = ListChipState.SHADOW)
}

